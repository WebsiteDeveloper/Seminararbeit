/*
 * The MIT License
 *
 * Copyright 2013 Bernhard Sirlinger.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package mss.integratoren;

import mss.util.Util;
import mss.util.Vektor2D;

/**
 *
 * @author Bernhard Sirlinger
 */
public class RungeKuttaKlassisch implements Rechner {
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
        double deltaT_halbe = deltaT/2.0;
        double einSechstel = 1.0 / 6.0;
        
        Vektor2D ortsVektor = new Vektor2D(center.getX() - planet.getX(), center.getY() - planet.getY());
        //Schritt y'
        Vektor2D a = Util.getA(masseCenter, ortsVektor);
        Vektor2D vTemp1 = new Vektor2D(a.getX() * deltaT, a.getY() * deltaT);
        
        //Schritt yA
        Vektor2D coordsA = Vektor2D.add(planet, new Vektor2D(deltaT_halbe * vTemp1.getX(), deltaT_halbe * vTemp1.getY()));
        Vektor2D ortsVektor1 = new Vektor2D(center.getX() - coordsA.getX(), center.getY() - coordsA.getY());
        Vektor2D a1 = Util.getA(masseCenter, ortsVektor1);
        Vektor2D vTemp2 = new Vektor2D(a1.getX() * deltaT, a1.getY() * deltaT);
        
        //Schritt yB
        Vektor2D coordsB = Vektor2D.add(planet, new Vektor2D(deltaT_halbe * vTemp2.getX(), deltaT_halbe * vTemp2.getY()));
        Vektor2D ortsVektor2 = new Vektor2D(center.getX() - coordsB.getX(), center.getY() - coordsB.getY());
        Vektor2D a2 = Util.getA(masseCenter, ortsVektor2);
        Vektor2D vTemp3 = new Vektor2D(a2.getX() * deltaT, a2.getY() * deltaT);
        
        //Schritt yC
        Vektor2D coordsC = Vektor2D.add(planet, new Vektor2D(deltaT_halbe * vTemp3.getX(), deltaT_halbe * vTemp3.getY()));
        Vektor2D ortsVektor3 = new Vektor2D(center.getX() - coordsC.getX(), center.getY() - coordsC.getY());
        Vektor2D a3 = Util.getA(masseCenter, ortsVektor3);
        Vektor2D vTemp4 = new Vektor2D(a3.getX() * deltaT, a3.getY() * deltaT);
        
        Vektor2D v = Vektor2D.skalarMultiply(deltaT * einSechstel, Vektor2D.add(Vektor2D.add(vTemp1, Vektor2D.skalarMultiply(2, Vektor2D.add(vTemp2, vTemp3))), vTemp4));
        return v;
    }
}
