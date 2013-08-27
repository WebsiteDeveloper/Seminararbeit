/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mss;

import java.util.ArrayList;
import mss.util.Notifications;
import mss.util.Planet;

/**
 *
 * @author Bernhard Sirlinger
 */
public interface Observer {
    /**
     *
     * @param type
     * @param data
     */
    public void notify(Notifications type, String data);
    /**
     *
     * @param type
     * @param planets
     */
    public void sendPlanets(Notifications type, ArrayList<Planet> planets);
}
