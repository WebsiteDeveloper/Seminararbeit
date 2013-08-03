/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mss.util;

import org.lwjgl.opengl.GL11;

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

    public Planet(String label, Vektor2D coords, double mass, double radix, Vektor2D v) {
        this.label = label;
        this.coords = coords;
        this.mass = mass;
        this.radix = radix;
        this.v = v;
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
        GL11.glColor3f(1.0f, 0.2f, 0.0f);
        for (double angle = 0; angle <= 360; angle += 1) {
            GL11.glVertex2d(this.coords.getX() + Math.sin(angle) * this.radix, this.coords.getY() + Math.cos(angle) * this.radix);
        }
        GL11.glEnd();
    }
}
