/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mss.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

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
     * @param vektor Der Vektor zwischen den beiden Planeten
     * @return
     */
    public static Vektor2D getA(double m, Vektor2D vektor) {
        //Der Betrag des Vektors zwischen den Planeten entspricht dem Radius r
        double r = vektor.betrag();
        
        //Berechnung des skalaren Wertes nach Newtons Gravitations Formel F = G * m/r² * rVec/r;
        double skalar = Util.G * (m/(Math.pow(r, 2.0)));
        
        //Normalisierung des Vektors
        vektor.normalisieren();
        
        Vektor2D erg = new Vektor2D(skalar * vektor.getX(), skalar * vektor.getY());
        return erg;
		}
    
    public static ArrayList<Planet> getDataFromDataFile(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        
        String data = null;
        String[] lines;
        
        lines = data.split("[\n]|[\r\n]|[\r]");
        
        for(String line : lines) {
            
        }
        
        return null;
    }
}