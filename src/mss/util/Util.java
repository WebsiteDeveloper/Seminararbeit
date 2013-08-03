/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mss.util;

/**
 *
 * @author Bernhard Sirlinger
 */
public class Util {
    /**
     *
     */
    public static final double G  = 6.67384e-11;
    
    /**
     *
     * @param m
     * @param ortsVektor
     * @return
     */
    public static Vektor2D getA(double m, Vektor2D ortsVektor) {
        //Der Betrag des Ortsvektors entspricht dem Radius r
        double r = ortsVektor.betrag();
        
        //Berechnung des skalaren Wertes nach Newtons Gravitations Formel F = G * m/r² * rVec/r;
        double skalar = Util.G * (m/(Math.pow(r, 2.0)));
        
        //Normalisierung des Vektors
        ortsVektor.normalisieren();
        
        Vektor2D erg = new Vektor2D(skalar * ortsVektor.getX(), skalar * ortsVektor.getY());
        return erg;
		}
}