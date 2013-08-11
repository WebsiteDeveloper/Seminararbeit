/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mss;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Menu;
import de.matthiasmann.twl.MenuAction;
import de.matthiasmann.twl.MenuElement;
import de.matthiasmann.twl.MenuManager;
import de.matthiasmann.twl.MenuSpacer;
import de.matthiasmann.twl.TreeTable;
import de.matthiasmann.twl.Widget;
import org.lwjgl.opengl.Display;

/**
 *
 * @author Bernhard Sirlinger
 */
public class Widgets extends Widget {

    private Button close;
    public boolean quit;
    private TreeTable table;
    private Menu menu;
    
    public Widgets() {/*
        this.close = new Button("Close");
        this.add(this.close);
        
        this.table = new TreeTable();
        this.add(this.table);
        
        this.menu = new Menu("Titlebar");
        MenuAction datei = new MenuAction("Datei", null);
        datei.setTheme("button");
        this.menu.add(datei);
        this.menu.add(new MenuAction("Projekt", null));
        this.menu.setEnabled(true);
        this.menu.createMenuBar(this);
        this.menu.setTheme("panel");*/
    }

    @Override
    protected void layout() {
        /*
        this.close.setPosition(0, 60);
        this.close.setSize(this.close.computeTextWidth() + 20, this.close.computeTextHeight());
        this.close.setTheme("button");
        
        this.table.setPosition(10, 10);
        this.table.setSize(100, 500);*/
    }
}
