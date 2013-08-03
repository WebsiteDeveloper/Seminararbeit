/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mss;

import java.util.ArrayList;
import java.util.HashMap;
import mss.integratoren.Integratoren;
import mss.integratoren.Rechenmodul;

/**
 *
 * @author Bernhard Sirlinger
 */
public class Controller implements Runnable {
    private Rechenmodul modul;
    private View frame;
    private Main mainHandler;
    private HashMap<String, Thread> threads = new HashMap<>();
    
    public Controller() {
        this.mainHandler = new Main();
        this.modul = new Rechenmodul(Integratoren.RUNGE_KUTTA_KLASSISCH, 0.1);
        this.modul.registerObserver("main", this.mainHandler);
        this.frame = new View("MSS");
        this.frame.registerObserver("main", this.mainHandler);
        this.frame.registerObserver("modul", this.modul);
        this.mainHandler.registerObserver("view", this.frame);
        this.mainHandler.registerObserver("modul", this.modul);
        
        Thread modulThread = new Thread(this.modul);
        modulThread.setName("modul");
        this.threads.put("modul", modulThread);
        
        Thread frameThread = new Thread(this.frame);
        frameThread.setName("frame");
        this.threads.put("frame", frameThread);
        
        Thread mainThread = new Thread(this.mainHandler);
        mainThread.setName("main");
        this.threads.put("main", mainThread);
    }
    
    public void start() {
        this.threads.get("modul").start();
        this.threads.get("frame").start();
        this.threads.get("main").start();
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
