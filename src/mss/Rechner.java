/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mss;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import mss.util.Planet;

/**
 *
 * @author Bernhard Sirlinger
 */
public class Rechner implements Observable {
    private HashMap<String, Observer> observers;

    public Rechner() {
        this.observers = new HashMap<>();
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
            ((Observer) temp).notify(message);
        }
    }

    @Override
    public void sendPlanetsToObservers(ArrayList<Planet> planets) {
    }
    
}
