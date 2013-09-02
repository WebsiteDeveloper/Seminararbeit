/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mss;

import java.util.HashMap;
import mss.integratoren.Integratoren;
import mss.integratoren.Rechenmodul;

/**
 *
 * @author Bernhard Sirlinger
 */
public class Controller implements Runnable {
    private final Rechenmodul modul;
    private final View frame;
    private final HashMap<String, Thread> threads = new HashMap<>();
    
    public Controller() {
        this.modul = new Rechenmodul(Integratoren.RUNGE_KUTTA_KLASSISCH, 0.01);
        this.frame = new View("MSS");
        this.frame.registerObserver("modul", this.modul);
        
        Thread modulThread = new Thread(this.modul);
        modulThread.setName("modul");
        this.threads.put("modul", modulThread);
        
        Thread frameThread = new Thread(this.frame);
        frameThread.setName("frame");
        this.threads.put("frame", frameThread);
    }
    
    public void start() {
        this.threads.get("modul").start();
        this.threads.get("frame").start();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Thread main = new Thread(new Controller(), "controller");
        main.start();
    }

    @Override
    public void run() {
        this.start();
    }
}
