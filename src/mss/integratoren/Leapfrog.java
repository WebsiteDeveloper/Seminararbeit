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
    public Vektor2D getDelta(Vektor2D v, double deltaT) {
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
    public Vektor2D getDeltaV(Vektor2D center, Vektor2D planet, Vektor2D vPlanet,  double masseCenter, double deltaT) {
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
