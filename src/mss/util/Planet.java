/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mss.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

/**
 *
 * @author Bernhard Sirlinger
 */
public class Planet {

    private String label;
    private Vektor2D coords;
    private double mass;
    private double radix;
    private Vektor2D v;
    private Color color;

    public Planet(String label, Vektor2D coords, double mass, double radix, Vektor2D v, Color color) {
        this.label = label;
        this.coords = coords;
        this.mass = mass;
        this.radix = radix;
        this.v = v;
        this.color = color;
    }

    public Planet(String label, double x, double y, double mass, double radix, Vektor2D v) {
        this.label = label;
        this.coords = new Vektor2D(x, y);
        this.mass = mass;
        this.radix = radix;
        this.v = v;
    }

    public Vektor2D getCoords() {
        return coords;
    }

    public String getLabel() {
        return label;
    }

    public double getMass() {
        return mass;
    }

    public double getRadix() {
        return radix;
    }

    public Vektor2D getV() {
        return v;
    }

    public void setCoords(Vektor2D coords) {
        this.coords = coords;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setRadix(double radix) {
        this.radix = radix;
    }

    public void setV(Vektor2D v) {
        this.v = v;
    }

    public void draw2D() {
        GL11.glBegin(GL11.GL_POLYGON);
        GL11.glColor3f((float)(this.color.getRed()/255.0), (float)(this.color.getGreen()/255.0), (float)(this.color.getBlue()/255.0));
        for (double angle = 0; angle <= 360; angle += 1) {
            GL11.glVertex2d(this.coords.getX() + Math.sin(angle) * this.radix, this.coords.getY() + Math.cos(angle) * this.radix);
        }
        GL11.glEnd();
    }

    public Color getColor() {
        return this.color;
    }
    
    @Override
    public String toString() {
        return "Planet " + this.label + " " + this.coords.getX() + " " + this.coords.getY() + " " + this.mass + " " + this.radix + " " + this.v.getX() + " " + this.v.getY() + " " + this.color.getRed() + " " + this.color.getGreen() + " " + this.color.getBlue() + "  -  " + super.toString();
    }
    
    public static boolean areColliding(Planet planet1, Planet planet2) {
        double x1 = planet1.getCoords().getX(),
               y1 = planet1.getCoords().getY(),
               x2 = planet2.getCoords().getX(),
               y2 = planet2.getCoords().getY();
        
        double r1 = planet1.getRadix(),
               r2 = planet2.getRadix();
        
        return (Math.pow((x2-x1), 2) + Math.pow((y1-y2), 2) <= Math.pow((r1+r2), 2));
    }
}
