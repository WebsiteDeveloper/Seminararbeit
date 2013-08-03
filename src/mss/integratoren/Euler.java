/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mss.integratoren;

import mss.util.Util;
import mss.util.Vektor2D;

/**
 *
 * @author Bernhard Sirlinger
 * @version 0.0.1
 */
public class Euler implements Rechner {
    
    /**
     *
     * @param v
     * @param deltaT
     * @return
     */
    @Override
    public Vektor2D getNewX(Vektor2D v, double deltaT) {
        Vektor2D erg = new Vektor2D(v.getX() * deltaT, v.getY() * deltaT);
        
        return erg;
    }
    
    /**
     *
     * @param center
     * @param planet
     * @param masseCenter
     * @param planetV
     * @param deltaT
     * @return
     */
    @Override
    public Vektor2D getDeltaV(Vektor2D center, Vektor2D planet, double masseCenter, double deltaT) {
        Vektor2D ortsVektor = new Vektor2D(center.getX() - planet.getX(), center.getY() - planet.getY());
        
        Vektor2D a = Util.getA(masseCenter, ortsVektor);
        Vektor2D v = new Vektor2D(a.getX() * deltaT, a.getY() * deltaT);
        
        return v;
    }
}
