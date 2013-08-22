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
import mss.util.Planet;
import mss.util.Vektor2D;

/**
 *
 * @author Admin
 */
public class Rechenmodul implements Observer, Observable, Runnable {
    public final static String ergTrenner = "~~";
    public final static String rowTrenner = "--------";
    
    private HashMap<String, Observer> observers = new HashMap<>();
    
    private ArrayList<Rechner> integratoren = new ArrayList<>();
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
    
    public void rechenschritt(ArrayList<Planet> planeten) {
        String msg = "";
        
        for(int i = 0; i < planeten.size(); i++) {
            Vektor2D v = planeten.get(i).getV(),
                     deltaCoords;
            
            for(int j = 0; j < planeten.size(); j++) {
                if(i != j) {
                    v = Vektor2D.add(v, this.getDeltaV(planeten.get(j), planeten.get(i)));
                }
            }
            
            deltaCoords = this.getDeltaCoords(v);
            msg += planeten.get(i).getLabel() + Rechenmodul.ergTrenner + deltaCoords.getX() + Rechenmodul.ergTrenner + deltaCoords.getY() + Rechenmodul.ergTrenner + v.getX() + Rechenmodul.ergTrenner + v.getY() + Rechenmodul.rowTrenner;
        }
        
        msg = msg.substring(0, msg.length() - Rechenmodul.rowTrenner.length());
        
        this.notifyObservers(msg);
    }
    
    private Vektor2D getDeltaCoords(Vektor2D v) {
        switch (this.integrator) {
            case EULER:
                Euler euler = (Euler)this.integratoren.get(Integratoren.EULER.ordinal());
                Vektor2D coordsE = euler.getNewX(v, this.deltaT);
                return coordsE;
            case LEAPFROG:
                Leapfrog leapfrog = (Leapfrog)this.integratoren.get(Integratoren.LEAPFROG.ordinal());
                Vektor2D coordsL = leapfrog.getNewX(v, this.deltaT);
                return coordsL;
            case RUNGE_KUTTA_KLASSISCH:
                RungeKuttaKlassisch rungekutta = (RungeKuttaKlassisch)this.integratoren.get(Integratoren.RUNGE_KUTTA_KLASSISCH.ordinal());
                Vektor2D coordsRKK = rungekutta.getNewX(v, this.deltaT);
                return coordsRKK;
            default:
                return null;
        }
    }
    
    private Vektor2D getDeltaV(Planet center, Planet moon) {
        switch (this.integrator) {
            case EULER:
                Euler euler = (Euler)this.integratoren.get(Integratoren.EULER.ordinal());
                Vektor2D vE = euler.getDeltaV(center.getCoords(), moon.getCoords(), center.getMass(), this.deltaT);
                return vE;
            case LEAPFROG:
                Leapfrog leapfrog = (Leapfrog)this.integratoren.get(Integratoren.LEAPFROG.ordinal());
                Vektor2D vL = leapfrog.getDeltaV(center.getCoords(), moon.getCoords(), center.getMass(), this.deltaT);
                return vL;
            case RUNGE_KUTTA_KLASSISCH:
                RungeKuttaKlassisch rungekutta = (RungeKuttaKlassisch)this.integratoren.get(Integratoren.RUNGE_KUTTA_KLASSISCH.ordinal());
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
    public void notifyObservers(String message) {
        Collection<Observer> values = this.observers.values();
        Object[] toArray = values.toArray();
        for(Object temp : toArray) {
            ((Observer)temp).notify(message);
        }
    }

    @Override
    public void run() {
    }

    @Override
    public void notify(String msg) {
        if(msg.startsWith("DeltaChange")) {
            this.setDeltaT(Double.parseDouble(msg.split(" ")[1]));
        }
    }

    @Override
    public void sendPlanets(String msg, ArrayList<Planet> planets) {
        this.rechenschritt(planets);
    }

    @Override
    public void sendPlanetsToObservers(ArrayList<Planet> planets) {
    }
}
