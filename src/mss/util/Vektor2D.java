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
public class Vektor2D extends Vektor {
    private double x;
    private double y;
    
    public Vektor2D() {
        this.x = 0.0;
        this.y = 0.0;
    }
    
    /**
     *
     * @param x
     * @param y
     */
    public Vektor2D(int x, int y) {
        this((double)x, (double)y);
    }
    
    /**
     *
     * @param x
     * @param y
     */
    public Vektor2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     *
     * @param vec
     */
    public Vektor2D(Vektor2D vec) {
        this(vec.getX(), vec.getY());
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
    
    @Override
    public double betrag() {
        return Math.sqrt(Math.pow(this.x, 2.0) + Math.pow(this.y, 2.0));
    }
    
    @Override
    public void normalisieren() {
        double betrag = this.betrag();
        this.x /= betrag;
        this.y /= betrag;
    }

    @Override
    public Object clone() {
        return new Vektor2D(this.x, this.y);
    }
    
    /**
     *
     * @param vec1
     * @param vec2
     * @return
     */
    public static Vektor2D add(Vektor2D vec1, Vektor2D vec2) {
        Vektor2D erg = new Vektor2D(vec1.getX() + vec2.getX(), vec1.getY() + vec2.getY());
        
        return erg;
    }
    
    /**
     *
     * @param skalar
     * @param vec
     * @return
     */
    public static Vektor2D skalarMultiply(double skalar, Vektor2D vec) {
        Vektor2D erg = new Vektor2D(skalar * vec.getX(), skalar * vec.getY());
        
        return erg;
    }

    @Override
    public String toString() {
        return "Vektor2D (" +  this.x + "|" + this.y + ") " + super.toString();
    }
}
