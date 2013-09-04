/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mss.integratoren;

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
public class Rechenmodul implements Observer, Observable, Runnable {
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
        this.integratoren.add(new Leapfrog());
        this.integratoren.add(new RungeKuttaKlassisch());
    }

    public void setData(ArrayList<Planet> startPlanets) {
        this.data = startPlanets;
    }
    
    public ArrayList<ArrayList<Planet>> computeUntilT(double deltaT, ArrayList<Planet> startPlanets) {
        this.deltaT = deltaT;

        ArrayList<ArrayList<Planet>> erg = new ArrayList<>(500);
        erg.add(startPlanets);
        erg.add(this.rechenschritt(startPlanets));
        for (int i = 1;; i++) {
            erg.add(this.rechenschritt(erg.get(i)));

            String collisions = Util.findCollisions(erg.get(i + 1));
            if (!collisions.isEmpty()) {
                System.out.println(collisions);
                return erg;
            }
        }
    }

    public ArrayList<Planet> rechenschritt(ArrayList<Planet> planeten) {
        int size = planeten.size();
        ArrayList<Planet> ergs = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            Planet temp = planeten.get(i);
            Vektor2D v = temp.getV(),
                    deltaCoords;

            for (int j = 0; j < size; j++) {
                if (i != j) {
                    v = Vektor2D.add(v, this.getDeltaV(planeten.get(j), temp));
                }
            }

            deltaCoords = this.getDeltaCoords(v);
            ergs.add(new Planet(temp.getLabel(), new Vektor2D(temp.getCoords().getX() + deltaCoords.getX(), temp.getCoords().getY() + deltaCoords.getY()), temp.getMass(), temp.getRadix(), (Vektor2D) v.clone(), temp.getColor()));
        }

        return ergs;
    }

    private Vektor2D getDeltaCoords(Vektor2D v) {
        switch (this.integrator) {
            case EULER:
                Euler euler = (Euler) this.integratoren.get(Integratoren.EULER.ordinal());
                Vektor2D coordsE = euler.getNewX(v, this.deltaT);
                return coordsE;
            case LEAPFROG:
                Leapfrog leapfrog = (Leapfrog) this.integratoren.get(Integratoren.LEAPFROG.ordinal());
                Vektor2D coordsL = leapfrog.getNewX(v, this.deltaT);
                return coordsL;
            case RUNGE_KUTTA_KLASSISCH:
                RungeKuttaKlassisch rungekutta = (RungeKuttaKlassisch) this.integratoren.get(Integratoren.RUNGE_KUTTA_KLASSISCH.ordinal());
                Vektor2D coordsRKK = rungekutta.getNewX(v, this.deltaT);
                return coordsRKK;
            default:
                return null;
        }
    }

    private Vektor2D getDeltaV(Planet center, Planet moon) {
        switch (this.integrator) {
            case EULER:
                Euler euler = (Euler) this.integratoren.get(Integratoren.EULER.ordinal());
                Vektor2D vE = euler.getDeltaV(center.getCoords(), moon.getCoords(), center.getMass(), this.deltaT);
                return vE;
            case LEAPFROG:
                Leapfrog leapfrog = (Leapfrog) this.integratoren.get(Integratoren.LEAPFROG.ordinal());
                Vektor2D vL = leapfrog.getDeltaV(center.getCoords(), moon.getCoords(), center.getMass(), this.deltaT);
                return vL;
            case RUNGE_KUTTA_KLASSISCH:
                RungeKuttaKlassisch rungekutta = (RungeKuttaKlassisch) this.integratoren.get(Integratoren.RUNGE_KUTTA_KLASSISCH.ordinal());
                Vektor2D vRKK = rungekutta.getDeltaV(center.getCoords(), moon.getCoords(), center.getMass(), this.deltaT);
                return vRKK;
            default:
                return new Vektor2D();
        }
    }

    public void setIntegrator(Integratoren integrator) {
        this.integrator = integrator;
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
        ArrayList<ArrayList<Planet>> computedValues = this.computeUntilT(this.deltaT, this.data);
        this.sendPlanetsToObservers(Notifications.RESULT, computedValues);
    }

    @Override
    public void notify(Notifications type, String data) {
        switch (type) {
            case DELTA_CHANGE:
                this.setDeltaT(Double.parseDouble(data));
                break;
        }
    }

    @Override
    public void sendPlanets(Notifications type, ArrayList<ArrayList<Planet>> planets) {
    }

    @Override
    public void sendPlanetsToObservers(Notifications type, ArrayList<ArrayList<Planet>> results) {
        Collection<Observer> values = this.observers.values();
        Object[] toArray = values.toArray();

        for (Object temp : toArray) {
            ((Observer) temp).sendPlanets(type, results);
        }
    }
}
