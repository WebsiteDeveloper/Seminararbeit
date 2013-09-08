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
package mss.util;

/**
 *
 * @author Bernhard Sirlinger
 */
public class Vektor3D extends Vektor {
    private double x;
    private double y;
    private double z;

    /**
     *
     */
    public Vektor3D() {
        this(0.0, 0.0, 0.0);
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public Vektor3D(int x, int y, int z) {
        this((double)x, (double)y, (double)z);
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public Vektor3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     *
     * @return
     */
    public double getX() {
        return x;
    }

    /**
     *
     * @return
     */
    public double getY() {
        return y;
    }

    /**
     *
     * @return
     */
    public double getZ() {
        return z;
    }

    /**
     *
     * @param x
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     *
     * @param y
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     *
     * @param z
     */
    public void setZ(double z) {
        this.z = z;
    }
    
    @Override
    public double betrag() {
        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2));
    }

    @Override
    public void normalisieren() {
        double betrag = this.betrag();
        this.x /= betrag;
        this.y /= betrag;
        this.z /= betrag;
    }
    
    /**
     *
     * @param vec1
     * @param vec2
     * @return
     */
    public static Vektor3D add(Vektor3D vec1, Vektor3D vec2) {
        Vektor3D erg = new Vektor3D(vec1.getX() + vec2.getX(), vec1.getY() + vec2.getY(), vec1.getZ() + vec2.getZ());
        
        return erg;
    }
    
    /**
     *
     * @param skalar
     * @param vec
     * @return
     */
    public static Vektor3D skalarMultiply(double skalar, Vektor3D vec) {
        Vektor3D erg = new Vektor3D(skalar * vec.getX(), skalar * vec.getY(), skalar * vec.getZ());
        
        return erg;
    }
}
