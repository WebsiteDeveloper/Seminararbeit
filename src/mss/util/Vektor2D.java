/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mss.util;

/**
 *
 * @author Bernhard Sirlinger
 */
public class Vektor2D {
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
    
    public double betrag() {
        return Math.sqrt(Math.pow(this.x, 2.0) + Math.pow(this.y, 2.0));
    }
    
    public void normalisieren() {
        double betrag = this.betrag();
        this.setX(this.x/betrag);
        this.setY(this.y/betrag);
    }
    
    public static Vektor2D add(Vektor2D vec1, Vektor2D vec2) {
        Vektor2D erg = new Vektor2D(vec1.getX() + vec2.getX(), vec1.getY() + vec2.getY());
        
        return erg;
    }
    
    public static Vektor2D skalarMultiply(double skalar, Vektor2D vec) {
        Vektor2D erg = new Vektor2D(skalar * vec.getX(), skalar * vec.getY());
        
        return erg;
    }
}
