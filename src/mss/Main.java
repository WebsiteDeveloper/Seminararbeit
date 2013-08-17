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

    public Main() {
        this.planets = new ArrayList<>();
        this.observers = new HashMap<>();

        this.planets.add(new Planet("Sun", new Vektor2D(0, 3), 1e10, 1, new Vektor2D(0, 0)));
        this.planets.add(new Planet("Planet", new Vektor2D(0, 0), 100, 0.5, new Vektor2D(-0.05, 0.05)));
        this.planets.add(new Planet("Planet2", new Vektor2D(0, -3), 1e10, 1, new Vektor2D()));
        this.startPlanets = (ArrayList<Planet>)this.planets.clone();
    }

    private void run(String msg) {
        this.time = this.getTime();

        while (this.shouldReset) {
            this.shouldReset = false;
            this.planets = (ArrayList<Planet>)this.startPlanets.clone();
            this.sendPlanetsToObservers(this.planets);
            
            while (!this.shouldRun) {
                if (Main.closed) {
                    return;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
            }
            
            while (true) {
                if (this.getDelta() >= 1 && !this.paused) {
                    this.sendPlanetsToObservers(this.planets);
                    this.time = this.getTime();
                }

                if (Main.closed) {
                    return;
                }
                if(this.shouldReset) {
                    break;
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
            this.planets.set(i, new Planet(temp.getLabel(), newCoords, temp.getMass(), temp.getRadix(), newV));
        }
    }

    @Override
    public void notify(String msg) {
        if (msg.length() >= 9 && msg.substring(0, 9).equals("AddPlanet")) {
        } else if (msg.length() >= 7 && msg.substring(0, 7).equals("Restart")) {
            this.paused = false;
        } else if (msg.length() >= 5 && msg.substring(0, 5).equals("Reset")) {
            this.paused = true;
            this.shouldRun = false;
            this.shouldReset = true;
        } else if (msg.length() >= 5 && msg.substring(0, 5).equals("Start")) {
            this.shouldRun = true;
            this.paused = false;
            this.shouldReset = false;
        } else if (msg.length() >= 5 && msg.substring(0, 5).equals("Pause")) {
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
        int delta = (int) (current_time - this.time);

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
    public void sendPlanets(ArrayList<Planet> planets) {
    }

    /**
     *
     * @param planets
     */
    @Override
    public void sendPlanetsToObservers(ArrayList<Planet> planets) {
        Collection<Observer> values = this.observers.values();
        Object[] toArray = values.toArray();

        for (Object temp : toArray) {
            ((Observer) temp).sendPlanets(planets);
        }
    }

    @Override
    public void run() {
        this.run("");
    }
}
