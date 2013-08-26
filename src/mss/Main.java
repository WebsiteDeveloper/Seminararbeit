/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mss;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import mss.integratoren.Rechenmodul;
import mss.util.Planet;
import mss.util.Vektor2D;
import org.lwjgl.Sys;
import org.lwjgl.util.Color;

/**
 *
 * @author Bernhard Sirlinger
 */
public class Main implements Observer, Observable, Runnable {

    public static boolean closed = false;
    private HashMap<String, Observer> observers;
    private ArrayList<Planet> planets;
    private ArrayList<Planet> startPlanets;
    private long time;
    private boolean shouldRun;
    private boolean paused;
    private boolean shouldReset = true;

    @SuppressWarnings("unchecked")
    public Main() {
        this.planets = new ArrayList<>();
        this.observers = new HashMap<>();

        this.planets.add(new Planet("Sun", new Vektor2D(0, 3), 1e10, 1, new Vektor2D(0, 0), new Color(255, 255, 255)));
        this.planets.add(new Planet("Planet", new Vektor2D(0, 0), 100, 0.5, new Vektor2D(-0.05, 0.05), new Color(244, 233, 10)));
        this.planets.add(new Planet("Planet2", new Vektor2D(0, -3), 1e10, 1, new Vektor2D(), new Color(255, 255, 255)));
        this.startPlanets = (ArrayList<Planet>)this.planets.clone();
    }

    @SuppressWarnings("unchecked")
    private void run(String msg) {
        this.time = this.getTime();

        while (this.shouldReset) {
            this.shouldReset = false;
            this.planets = (ArrayList<Planet>)this.startPlanets.clone();
            this.sendPlanetsToObservers("Display", this.planets);
            
            while (!this.shouldRun) {
                if (Main.closed) {
                    return;
                }
                if(this.shouldReset) {
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
            }
            
            while (true) {
                if (Main.closed) {
                    return;
                }
                if(this.shouldReset) {
                    break;
                }
                
                long delta = this.getDelta();
                if (delta >= 1 && !this.paused) {
                    this.sendPlanetsToObservers("Update", this.planets);
                    this.time = this.getTime();
                }
            }
        }
    }

    private void parseErgs(String ergs) {
        String[] rows;
        String[] values;
        rows = ergs.split(Rechenmodul.rowTrenner);
        
        for (int i = 0; i < rows.length && i < this.planets.size(); i++) {
            values = rows[i].split(Rechenmodul.ergTrenner);
            Planet temp = this.planets.get(i);
            Vektor2D newCoords = new Vektor2D(temp.getCoords().getX() + Double.parseDouble(values[1]), temp.getCoords().getY() + Double.parseDouble(values[2]));
            Vektor2D newV = new Vektor2D(Double.parseDouble(values[3]), Double.parseDouble(values[4]));
            this.planets.set(i, new Planet(temp.getLabel(), newCoords, temp.getMass(), temp.getRadix(), newV, temp.getColor()));
        }
    }

    @Override
    public void notify(String msg) {
        if (msg.startsWith("AddPlanet")) {
        } else if(msg.startsWith("DeltaChange")) {
            this.notifyObservers(msg);
        } else if (msg.startsWith("Restart")) {
            this.paused = false;
        } else if (msg.startsWith("Reset")) {
            this.paused = true;
            this.shouldRun = false;
            this.shouldReset = true;
        } else if (msg.startsWith("Start")) {
            this.shouldRun = true;
            this.paused = false;
            this.shouldReset = false;
        } else if (msg.startsWith("Pause")) {
            this.paused = true;
        } else if (msg.contains(Rechenmodul.ergTrenner)) {
            this.parseErgs(msg);
            return;
        }
        
        System.out.println(msg);
    }

    private long getTime() {
        return (Sys.getTime() * 1000 / Sys.getTimerResolution());
    }

    private long getDelta() {
        long current_time = this.getTime();
        long delta = current_time - this.time;

        return delta;
    }

    @Override
    public void registerObserver(String key, Observer observer) {
        this.observers.put(key, observer);
    }

    @Override
    public void removeObserver(String key) {
        this.observers.remove(key);
    }

    /**
     *
     * @param message
     */
    @Override
    public void notifyObservers(String message) {
        Collection<Observer> values = this.observers.values();
        Object[] toArray = values.toArray();

        for (Object temp : toArray) {
            ((Observer) temp).notify(message);
        }
    }

    @Override
    public void sendPlanets(String msg, ArrayList<Planet> planets) {
        switch (msg) {
            case "Result":
                this.planets = planets;
                break;
            case "Reset":
                this.startPlanets = planets;
                break;
        }
    }

    /**
     * 
     * @param msg
     * @param planets
     */
    @Override
    public void sendPlanetsToObservers(String msg, ArrayList<Planet> planets) {
        Collection<Observer> values = this.observers.values();
        Object[] toArray = values.toArray();

        for (Object temp : toArray) {
            ((Observer) temp).sendPlanets(msg, planets);
        }
    }

    @Override
    public void run() {
        this.run("");
    }
}
