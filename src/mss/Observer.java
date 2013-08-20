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
public interface Observer {
    /**
     *
     * @param msg
     */
    public void notify(String msg);
    /**
     *
     * @param planets
     */
    public void sendPlanets(String msg, ArrayList<Planet> planets);
}
