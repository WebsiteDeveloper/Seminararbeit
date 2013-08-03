/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mss.integratoren;

import mss.util.Vektor2D;

/**
 *
 * @author Admin
 */
public interface Rechner {
    /**
     *
     * @param v
     * @param deltaT
     * @return
     */
    public Vektor2D getNewX(Vektor2D v, double deltaT);
    /**
     *
     * @param center
     * @param planet
     * @param masseCenter
     * @param planetV
     * @param deltaT
     * @return
     */
    public Vektor2D getDeltaV(Vektor2D center, Vektor2D planet, double masseCenter, double deltaT);
}
