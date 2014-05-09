/*
 * The MIT License
 *
 * Copyright 2013 Bernhard Sirlinger.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package mss.integratoren;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import mss.Observable;
import mss.Observer;
import mss.util.Notifications;
import mss.util.Planet;
import mss.util.Util;
import mss.util.Vektor2D;

/**
 *
 * @author Bernhard Sirlinger
 */
public class Rechenmodul implements Observable, Runnable {

    private final HashMap<String, Observer> observers = new HashMap<>();

    private final ArrayList<Rechner> integratoren = new ArrayList<>();
    private ArrayList<Planet> data = new ArrayList<>();
    private Integratoren integrator;
    private double deltaT;

    public Rechenmodul(double deltaT) {
        this.integrator = Integratoren.RUNGE_KUTTA_KLASSISCH;
        this.deltaT = deltaT;
        this.addIntegratoren();
    }

    public Rechenmodul(Integratoren integrator, double deltaT) {
        this.integrator = integrator;
        this.deltaT = deltaT;
        this.addIntegratoren();
    }

    private void addIntegratoren() {
        this.integratoren.add(new Euler());
        this.integratoren.add(new RungeKuttaKlassisch());
    }

    public void setData(ArrayList<Planet> startPlanets) {
        this.data = startPlanets;
    }

    public ArrayList<ArrayList<Planet>> computeUntilT(double deltaT, ArrayList<Planet> startPlanets, File tempFile) {
        this.deltaT = deltaT;

        ArrayList<Planet> temp;
        ArrayList<ArrayList<Planet>> erg = new ArrayList<>(500);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonWriter writer = null;
        try {
            OutputStream out = new FileOutputStream(tempFile);
            writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.setIndent("  ");
        } catch (IOException ex) {
        }
        
        if(writer != null) {
            try {
                writer.setLenient(false);
                writer.beginArray();
                    erg.add(startPlanets);
                    temp = this.rechenschritt(startPlanets);
                    writer.beginArray();
                        for(Planet planet : startPlanets) {
                            gson.toJson(planet, Planet.class, writer);
                    }
                    writer.endArray();
                    writer.beginArray();
                        for(Planet planet : temp) {
                            gson.toJson(planet, Planet.class, writer);
                        }
                    writer.endArray();
                    writer.flush();
                    erg.add(temp);
                    
                    for (int i = 1;; i++) {
                        temp = this.rechenschritt(temp);

                        writer.beginArray();
                        for(Planet planet : temp) {
                            gson.toJson(planet, Planet.class, writer);
                        }
                        writer.endArray();
                        writer.flush();
                        if (i % 20 == 0) {
                            erg.add(temp);
                        }
                        String collisions = Util.findCollisions(temp);
                        if (!collisions.isEmpty()) {
                            erg.add(temp);
                            System.out.println(collisions);
                            break;
                        }
                    
                        if (i > 1000 / deltaT) {
                            break;
                        }
                    }
                writer.endArray();
                writer.flush();
                writer.close();
            } catch (IOException ex) {
            }
        } else {
            System.exit(-1);
        }
        
        return erg;
    }

    public ArrayList<Planet> rechenschritt(ArrayList<Planet> planeten) {
        int size = planeten.size();
        ArrayList<Planet> ergs = new ArrayList<>(size);

        //Durchlaufe alle Planeten
        for (int i = 0; i < size; i++) {
            //Sich bewegender Planet
            Planet movingPlanet = planeten.get(i);
            Vektor2D currentV = movingPlanet.getV(),
                    deltaCoords = new Vektor2D(0, 0),
                    deltaV = new Vektor2D(0, 0);

            for (int j = 0; j < size; j++) {
                if (i != j) {
                    deltaV = Vektor2D.add(deltaV, this.getDeltaV(planeten.get(j), movingPlanet));
                }
            }
            currentV = new Vektor2D(currentV.getX() + deltaV.getX(), currentV.getY() + deltaV.getY());
            deltaCoords = this.getDeltaCoords(currentV);
            ergs.add(new Planet(movingPlanet.getLabel(), Vektor2D.add(movingPlanet.getCoords(), deltaCoords), movingPlanet.getMass(), movingPlanet.getRadix(), currentV, movingPlanet.getColor()));
        }

        return ergs;
    }

    private Vektor2D getDeltaCoords(Vektor2D v) {
        switch (this.integrator) {
            case EULER:
                Euler euler = (Euler) this.integratoren.get(Integratoren.EULER.ordinal());
                Vektor2D coordsE = euler.getDelta(v, this.deltaT);
                return coordsE;
            case RUNGE_KUTTA_KLASSISCH:
                RungeKuttaKlassisch rungekutta = (RungeKuttaKlassisch) this.integratoren.get(Integratoren.RUNGE_KUTTA_KLASSISCH.ordinal());
                Vektor2D coordsRKK = rungekutta.getDelta(v, this.deltaT);
                return coordsRKK;
            default:
                return null;
        }
    }

    private Vektor2D getDeltaV(Planet center, Planet moon) {
        if(this.integrator == null) {
            this.integrator = Integratoren.RUNGE_KUTTA_KLASSISCH;//TODO: revert Workaround
        }
        switch (this.integrator) {
            case EULER:
                Euler euler = (Euler) this.integratoren.get(Integratoren.EULER.ordinal());
                Vektor2D vE = euler.getDeltaV(center.getCoords(), moon.getCoords(), moon.getV(), center.getMass(), this.deltaT);
                return vE;
            case RUNGE_KUTTA_KLASSISCH:
                RungeKuttaKlassisch rungekutta = (RungeKuttaKlassisch) this.integratoren.get(Integratoren.RUNGE_KUTTA_KLASSISCH.ordinal());
                Vektor2D vRKK = rungekutta.getDeltaV(center.getCoords(), moon.getCoords(), moon.getV(), center.getMass(), this.deltaT);
                return vRKK;
            default:
                return new Vektor2D();
        }
    }

    public void setIntegrator(Integratoren integrator) {
        if(this.integrator != null) {
            this.integrator = integrator;
        } else {
            System.out.println("No valid Integrator Provided");
        }
    }

    public void setDeltaT(double deltaT) {
        this.deltaT = deltaT;
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
        File tempFile = null;
        try {
            tempFile = File.createTempFile("MSS-", "-DATA");
            tempFile.deleteOnExit();
        } catch (IOException ex) {
        }

        ArrayList<ArrayList<Planet>> computedValues = this.computeUntilT(this.deltaT, this.data, tempFile);

        this.sendDataToObservers(Notifications.RESULT, computedValues, tempFile);
    }

    @Override
    public void sendDataToObservers(Notifications type, ArrayList<ArrayList<Planet>> results, File tempFile) {
        Collection<Observer> values = this.observers.values();
        Object[] toArray = values.toArray();

        for (Object temp : toArray) {
            ((Observer) temp).sendData(type, results, tempFile);
        }
    }

    public Integratoren getIntegrator() {
        return this.integrator;
    }
}
