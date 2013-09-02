/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mss;

import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import mss.util.Notifications;
import mss.util.Planet;
import mss.util.ScreenshotSaver;
import mss.util.Util;
import mss.util.Vektor2D;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author Bernhard Sirlinger
 */
public class View implements Observer, Observable, Runnable {

    private static boolean closeRequested = false;
    private final static AtomicReference<Dimension> newCanvasSize = new AtomicReference<>();

    private enum ChangeType {
        INCREASE, DECREASE
    };

    private enum Directions {
        UP, DOWN, LEFT, RIGHT
    };

    private ArrayList<Planet> planets;
    private ArrayList<Planet> startPlanets;
    private long time;
    private final HashMap<String, Observer> observers = new HashMap<>();
    private String title;
    private final Canvas canvas = new Canvas();
    private final JFrame frame;
    private final JPanel panel = new JPanel();


    private boolean isPaused = true;
    private int zoomLevel = 1;
    private final DoubleBuffer buffer;

    private String lastOpenedFilePath = "";
    private final String empty = "";

    private final JMenuBar menuBar;

    public View(String title) {
        this.planets = new ArrayList<>();
        this.planets.add(new Planet("Sun", new Vektor2D(0, 3), 1e10, 1, new Vektor2D(0, 0), new org.lwjgl.util.Color(255, 255, 255)));
        this.planets.add(new Planet("Planet", new Vektor2D(0, 0), 100, 0.5, new Vektor2D(-0.05, 0.05), new org.lwjgl.util.Color(244, 233, 10)));
        this.planets.add(new Planet("Planet2", new Vektor2D(0, -3), 1e10, 1, new Vektor2D(), new org.lwjgl.util.Color(255, 255, 255)));
        this.startPlanets = (ArrayList<Planet>) this.planets.clone();
        
        this.buffer = BufferUtils.createDoubleBuffer(16);
        this.initBuffer();
        
        this.title = title;
        this.frame = new JFrame(title);
        
        this.panel.setPreferredSize(new Dimension(160, 600));
        this.panel.setBackground(Color.red);

        this.addListeners();

        this.menuBar = new JMenuBar();
        this.frame.setJMenuBar(this.menuBar);

        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem openFile = new JMenuItem("Open File");
        openFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyObservers(Notifications.PAUSE, empty);
                isPaused = true;
                openFile();
                canvas.requestFocus();
            }
        });
        fileMenu.add(openFile);

        JMenuItem start = new JMenuItem("Start Simulation");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyObservers(Notifications.START, empty);
                isPaused = false;
                canvas.requestFocus();
            }
        });
        fileMenu.add(start);

        JMenuItem reset = new JMenuItem("Reset Simulation");
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyObservers(Notifications.RESET, empty);
                canvas.requestFocus();
            }
        });
        fileMenu.add(reset);

        JMenuItem pause = new JMenuItem("Pause");
        pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPaused) {
                    notifyObservers(Notifications.PAUSE, empty);
                    isPaused = true;
                    ((JMenuItem) e.getSource()).setText("Restart");
                } else {
                    notifyObservers(Notifications.RESUME, empty);
                    isPaused = false;
                    ((JMenuItem) e.getSource()).setText("Pause");
                }
                canvas.requestFocus();
            }
        });
        fileMenu.add(pause);

        JMenuItem about = new JMenuItem("About");
        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((JMenuItem) e.getSource()).setName("");
                if (!isPaused) {
                    notifyObservers(Notifications.PAUSE, empty);
                    isPaused = true;
                    ((JMenuItem) e.getSource()).setName("selfPaused");
                }
                showAboutDialog();
                canvas.requestFocus();
            }
        });
        helpMenu.add(about);

        this.menuBar.add(fileMenu);
        this.menuBar.add(helpMenu);
    }
    
    private void initBuffer() {
        this.buffer.put(0, this.zoomLevel);
        this.buffer.put(1, 0);
        this.buffer.put(2, 0);
        this.buffer.put(3, 0);
        this.buffer.put(4, 0);
        this.buffer.put(5, this.zoomLevel);
        this.buffer.put(6, 0);
        this.buffer.put(7, 0);
        this.buffer.put(8, 0);
        this.buffer.put(9, 0);
        this.buffer.put(10, this.zoomLevel);
        this.buffer.put(11, 0);
        this.buffer.put(12, 0);
        this.buffer.put(13, 0);
        this.buffer.put(14, 0);
        this.buffer.put(15, this.zoomLevel);
    }
    
    private void addListeners() {
        this.canvas.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                newCanvasSize.set(new Dimension(canvas.getSize().width, canvas.getSize().height));
            }
        });

        this.frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layout(e.getComponent().getWidth(), e.getComponent().getHeight());
            }
        });

        this.frame.addWindowListener(new WindowAdapter() {
            private boolean wasAlreadyPaused;

            @Override
            public void windowGainedFocus(WindowEvent e) {
                if (!this.wasAlreadyPaused) {
                    notifyObservers(Notifications.RESUME, empty);
                    isPaused = false;
                }
                canvas.requestFocusInWindow();
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                if (!isPaused) {
                    isPaused = true;
                    notifyObservers(Notifications.PAUSE, empty);
                } else {
                    this.wasAlreadyPaused = true;
                }
                super.windowLostFocus(e);
            }
            
            @Override
            public void windowClosing(WindowEvent e) {
                closeRequested = true;
            }
        });
    }
    
    private void layout(int width, int height) {
        this.panel.setSize((int) Math.floor(width * 0.2), height);
        this.panel.setPreferredSize(this.panel.getSize());
        this.canvas.setSize(width - this.panel.getWidth(), height);
        this.canvas.setPreferredSize(this.canvas.getSize());
        this.canvas.setLocation(this.panel.getWidth(), 0);
    }

    public void init() {
        try {
            Display.setParent(this.canvas);
            Keyboard.enableRepeatEvents(true);
            this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            this.frame.setLayout(new BorderLayout());
            this.frame.add(this.canvas, BorderLayout.EAST);
            this.frame.add(this.panel, BorderLayout.WEST);
            this.frame.setPreferredSize(new Dimension(1024, 786));
            this.frame.setMinimumSize(new Dimension(800, 600));
            this.frame.pack();

            this.frame.setVisible(true);

            Display.setTitle(this.title);
            Display.setResizable(true);
            Display.setFullscreen(false);
            Display.create();
            this.initOpenGL();

            LWJGLRenderer renderer = new LWJGLRenderer();

            Dimension newDim;

            while (!Display.isCloseRequested() && !View.closeRequested) {
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                newDim = newCanvasSize.getAndSet(null);
                checkKeyInput();
                if (newDim != null) {
                    GL11.glViewport(0, 0, newDim.width, newDim.height);
                    renderer.syncViewportSize();
                }

                for (int i = 0; i < this.planets.size(); i++) {
                    this.planets.get(i).draw2D();
                }

                checkKeyInput();
                checkMouseInput();
                Display.update();
                Display.sync(60);
            }

            Display.destroy();
            this.frame.dispose();
        } catch (LWJGLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    private void checkKeyInput() {
        while (Keyboard.next()) {
            if (!Keyboard.getEventKeyState()) {
                switch (Keyboard.getEventKey()) {
                    case Keyboard.KEY_F1:
                        this.saveScreenshot();
                        break;
                }
            }

            switch (Keyboard.getEventKey()) {
                case Keyboard.KEY_UP:
                    this.changeTranslationMatrix(Directions.UP);
                    break;
                case Keyboard.KEY_DOWN:
                    this.changeTranslationMatrix(Directions.DOWN);
                    break;
                case Keyboard.KEY_LEFT:
                    this.changeTranslationMatrix(Directions.LEFT);
                    break;
                case Keyboard.KEY_RIGHT:
                    this.changeTranslationMatrix(Directions.RIGHT);
                    break;
                case Keyboard.KEY_R:
                    this.resetTranslationMatrix();
                    break;
                case Keyboard.KEY_ADD:
                    System.out.println("Add");
                    break;
                case Keyboard.KEY_SUBTRACT:
                    System.out.println("Subtract");
                    break;
            }
        }
    }

    private void initOpenGL() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);

        GL11.glLoadMatrix(this.buffer);

        GL11.glOrtho(-Display.getWidth(), Display.getWidth(), -Display.getHeight(), Display.getHeight(), 1, -1);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void showAboutDialog() {
        JDialog dialog = new JDialog(this.frame, true);
        dialog.setSize(200, 200);
        dialog.setLocation(this.frame.getX() + this.frame.getWidth() / 2 - 100, this.frame.getY() + this.frame.getHeight() / 2 - 100);
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
                notifyObservers(Notifications.RESUME, "");
                isPaused = false;
                e.getWindow().dispose();
            }
        });
    }

    private void saveScreenshot() {
        final File f = new File(View.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        GL11.glReadBuffer(GL11.GL_FRONT);
        int width = Display.getWidth();
        int height = Display.getHeight();
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(width * height * 4);
        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, byteBuffer);
        ScreenshotSaver saver = new ScreenshotSaver(byteBuffer, f.getParent(), width, height);
        saver.start();
    }

    @SuppressWarnings("unchecked")
    private void openFile() {
        JFileChooser fileChooser;

        if (this.lastOpenedFilePath.isEmpty()) {
            final File f = new File(View.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            fileChooser = new JFileChooser(f.getParentFile());
        } else {
            fileChooser = new JFileChooser(this.lastOpenedFilePath);
        }
        int state = fileChooser.showOpenDialog(this.frame);

        if (state == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            this.lastOpenedFilePath = selectedFile.getAbsolutePath();
            HashMap<String, Object> dataFromDataFile = Util.getDataFromDataFile(selectedFile);
            if ("".equals((String) dataFromDataFile.get("Error")) && !((ArrayList<Planet>) dataFromDataFile.get("Planets")).isEmpty()) {
                this.sendPlanetsToObservers(Notifications.RESET, (ArrayList<Planet>) dataFromDataFile.get("Planets"));
                this.notifyObservers(Notifications.DELTA_CHANGE, "" + dataFromDataFile.get("deltaT"));
                this.notifyObservers(Notifications.RESET, empty);
            } else {
                this.showInvalidFileDialog((String) dataFromDataFile.get("Error"));
            }
        } else {
            notifyObservers(Notifications.RESUME, empty);
            this.isPaused = false;
        }
    }

    private void showInvalidFileDialog(String errors) {
        System.out.println(errors);
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
    public void notifyObservers(Notifications type, String data) {
        Collection<Observer> values = this.observers.values();
        Object[] toArray = values.toArray();

        for (Object temp : toArray) {
            ((Observer) temp).notify(type, data);
        }
    }

    @Override
    public void run() {
        this.init();
    }

    @Override
    public void notify(Notifications type, String data) {
    }

    @Override
    public void sendPlanets(Notifications type, ArrayList<Planet> planets) {
        switch (type) {
            case DISPLAY:
                this.planets = planets;
                this.isPaused = false;
                break;
            case UPDATE:
                this.planets = planets;
                this.isPaused = false;
                break;
        }

    }

    @Override
    public void sendPlanetsToObservers(Notifications type, ArrayList<Planet> planets) {
        Collection<Observer> values = this.observers.values();
        Object[] toArray = values.toArray();

        for (Object temp : toArray) {
            ((Observer) temp).sendPlanets(type, planets);
        }
    }

    private void checkMouseInput() {
        int delta = Mouse.getDWheel();

        if (delta > 0) {
            this.changeZoomFactor(ChangeType.INCREASE);
        } else if (delta < 0) {
            this.changeZoomFactor(ChangeType.DECREASE);
        }
    }

    private void resetTranslationMatrix() {
        this.buffer.put(12, 0);
        this.buffer.put(13, 0);
        this.initOpenGL();
    }
    
    private void changeTranslationMatrix(Directions direction) {
        switch(direction) {
            case UP:
                this.buffer.put(13, this.buffer.get(13) + 0.01);
                break;
            case DOWN:
                this.buffer.put(13, this.buffer.get(13) - 0.01);
                break;
            case LEFT:
                this.buffer.put(12, this.buffer.get(12) - 0.01);
                break;
            case RIGHT:
                this.buffer.put(12, this.buffer.get(12) + 0.01);
                break;
        }
        
        this.initOpenGL();
    }

    private void changeZoomFactor(ChangeType change) {
        switch (change) {
            case DECREASE:
                if (this.zoomLevel != 1) {
                this.zoomLevel -= 1;
            } else {
                this.zoomLevel = -1;
            }
                break;
            case INCREASE:
                if (this.zoomLevel != -1) {
                this.zoomLevel += 1;
            } else {
                this.zoomLevel = 1;
            }
                break;
        }

        if (this.zoomLevel > 0) {
            this.buffer.put(0, this.zoomLevel);
            this.buffer.put(5, this.zoomLevel);
            this.buffer.put(10, this.zoomLevel);
        } else if (this.zoomLevel < 0) {
            this.buffer.put(0, 1.0 / -this.zoomLevel);
            this.buffer.put(5, 1.0 / -this.zoomLevel);
            this.buffer.put(10, 1.0 / -this.zoomLevel);
        }

        this.initOpenGL();
    }
    
    private long getTime() {
        return (Sys.getTime() * 1000 / Sys.getTimerResolution());
    }

    private long getDelta() {
        long current_time = this.getTime();
        long delta = current_time - this.time;

        return delta;
    }
}
