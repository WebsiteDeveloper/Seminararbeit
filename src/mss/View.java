/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mss;

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import mss.util.Planet;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author Bernhard Sirlinger
 */
public class View implements Observer, Observable, Runnable {
    private static boolean closeRequested = false;
    private final static AtomicReference<Dimension> newCanvasSize = new AtomicReference<Dimension>();
    
    
    private ArrayList<Planet> planets = new ArrayList<>();
    private HashMap<String, Observer> observers;
    private String title;
    private final Canvas canvas = new Canvas();
    private JFrame frame;
    
    private JMenuBar menuBar;
    
    public View(String title) {
        this.observers = new HashMap<>();
        this.title = title;
        this.frame = new JFrame(title);
        
        this.canvas.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                newCanvasSize.set(new Dimension(canvas.getSize().width, canvas.getSize().height));
                //canvas.setBounds(200, 0, canvas.getSize().width - 200, canvas.getSize().height);
            }
        });

        this.frame.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                canvas.requestFocusInWindow();
            }
        });

        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeRequested = true;
            }
        });
        
        this.menuBar = new JMenuBar();
        this.frame.setJMenuBar(this.menuBar);
        
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        
        
        
        JMenuItem start = new JMenuItem("Start Calculation");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyObservers("Start");
            }
        });
        fileMenu.add(start);
        
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyObservers("Pause");
                showAboutDialog();
            }
        });
        helpMenu.add(about);
        
        this.menuBar.add(fileMenu);
        this.menuBar.add(helpMenu);
    }

    public void init() {
        try {
            Display.setParent(this.canvas);
            this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            //this.frame.setLayout(new BorderLayout());
            this.frame.add(canvas, BorderLayout.CENTER);
            this.frame.setPreferredSize(new Dimension(1024, 786));
            this.frame.setMinimumSize(new Dimension(800, 600));
            this.frame.pack();
            this.frame.setVisible(true);
            //Display.setDisplayMode(mode);
            Display.setTitle(this.title);
            Display.setResizable(true);
            Display.setFullscreen(false);
            Display.create();
            this.initOpenGL();

            LWJGLRenderer renderer = new LWJGLRenderer();
            Widgets gameUI = new Widgets();
            GUI gui = new GUI(gameUI, renderer);
            ThemeManager theme = ThemeManager.createThemeManager(Widgets.class.getResource("chutzpah.xml"), renderer);
            gui.applyTheme(theme);

            Dimension newDim;
            
            this.notifyObservers("DisplayInit");
            while (!Display.isCloseRequested() && !gameUI.quit  && !this.closeRequested) {
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                
                newDim = newCanvasSize.getAndSet(null);

                if (newDim != null) {
                    GL11.glViewport(0, 0, newDim.width, newDim.height);
                    renderer.syncViewportSize();
                }
                
                for (int i = 0; i < this.planets.size(); i++) {
                    this.planets.get(i).draw2D();
                }
                
                
                gui.update();
                Display.update();
                Display.sync(60);
            }

            gui.destroy();
            theme.destroy();
            Display.destroy();
            this.frame.dispose();
            Main.closed = true;
        } catch (LWJGLException | IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    private void initOpenGL() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);

        GL11.glLoadIdentity();
        GL11.glOrtho(-Display.getWidth() / 20, Display.getWidth() / 20, Display.getHeight() / 20, -Display.getHeight() / 20, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        //GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void showAboutDialog() {
        JDialog dialog = new JDialog(this.frame, true);
        dialog.setSize(200, 200);
        dialog.setLocation(this.frame.getWidth()/2 - 100, this.frame.getHeight()/2 - 100);
        dialog.setEnabled(true);
        dialog.setVisible(true);
        dialog.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }
            @Override
            public void windowClosing(WindowEvent e) {
            }
            @Override
            public void windowClosed(WindowEvent e) {
            }
            @Override
            public void windowIconified(WindowEvent e) {
            }
            @Override
            public void windowDeiconified(WindowEvent e) {
            }
            @Override
            public void windowActivated(WindowEvent e) {
            }
            @Override
            public void windowDeactivated(WindowEvent e) {
                notifyObservers("Restart");
                e.getWindow().dispose();
            }
        });
    }
    
    private void saveScreenshot() {
        
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        Display.setTitle(this.title);
    }

    public void setPlanets(ArrayList<Planet> planets) {
        this.planets = planets;
    }

    @Override
    public void registerObserver(String key, Observer observer) {
        this.observers.put(key, observer);
    }

    @Override
    public void removeObserver(String key) {
        this.observers.remove(key);
    }

    @Override
    public void notifyObservers(String message) {
        Collection<Observer> values = this.observers.values();
        Object[] toArray = values.toArray();

        for (Object temp : toArray) {
            ((Observer) temp).notify(message);
        }
    }

    @Override
    public void run() {
        this.init();
    }

    @Override
    public void notify(String msg) {
    }

    @Override
    public void sendPlanets(ArrayList<Planet> planets) {
        this.planets = planets;
    }

    @Override
    public void sendPlanetsToObservers(ArrayList<Planet> planets) {
    }
}
