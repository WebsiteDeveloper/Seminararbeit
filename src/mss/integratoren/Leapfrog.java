/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mss.integratoren;

import mss.util.Util;
import mss.util.Vektor2D;

/**
 *
 * @author Admin
 */
public class Leapfrog implements Rechner {
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
        double deltaT_halbe = deltaT / 2.0;
        
        Vektor2D ortsVektor = new Vektor2D(center.getX() - planet.getX(), center.getY() - planet.getY());
        Vektor2D v1 = Vektor2D.skalarMultiply(deltaT_halbe, Util.getA(masseCenter, ortsVektor));
        Vektor2D coords1 = Vektor2D.add(planet, Vektor2D.skalarMultiply(deltaT_halbe, v1));
        
        Vektor2D coords2 = Vektor2D.add(coords1, Vektor2D.skalarMultiply(deltaT_halbe, v1));
        ortsVektor = new Vektor2D(center.getX() - coords2.getX(), center.getY() - coords2.getY());
        Vektor2D v2 = Vektor2D.add(v1, Vektor2D.skalarMultiply(deltaT_halbe, Util.getA(masseCenter, ortsVektor)));
        
        return v2;
    }
}
