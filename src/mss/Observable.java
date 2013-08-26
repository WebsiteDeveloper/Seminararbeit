/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mss;

import java.util.ArrayList;
import mss.util.Planet;

/**
 *
 * @author Bernhard Sirlinger
 */
public interface Observable {
    public void registerObserver(String key, Observer observer);
    public void removeObserver(String key);
    public void notifyObservers(String message);
    public void sendPlanetsToObservers(String msg, ArrayList<Planet> planets);
}
