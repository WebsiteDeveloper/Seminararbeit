/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mss;

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import mss.util.Planet;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author Bernhard Sirlinger
 */
public class View implements Observer, Observable, Runnable {

    private ArrayList<Planet> planets = new ArrayList<>();
    private HashMap<String, Observer> observers;
    private String title;

    public View(String title) {
        this.observers = new HashMap<>();
        this.title = title;
    }

    public void init() {
        try {
            DisplayMode mode = new DisplayMode(800, 600);

            Display.setDisplayMode(mode);
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
            
            this.notifyObservers("DisplayInit");
            this.notifyObservers("Start");
            while (!Display.isCloseRequested() && !gameUI.quit) {
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                for(int i = 0; i < this.planets.size(); i++) {
                    this.planets.get(i).draw2D();
                }
                gui.update();
                Display.update();
                Display.sync(60);
            }

            gui.destroy();
            theme.destroy();
            Display.destroy();
            Main.closed = true;
        } catch (LWJGLException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void initOpenGL() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);

        GL11.glLoadIdentity();
        GL11.glOrtho(-Display.getWidth()/20, Display.getWidth()/20, Display.getHeight()/20, -Display.getHeight()/20, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        
        //GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
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
