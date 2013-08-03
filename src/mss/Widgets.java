/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mss;

import de.matthiasmann.twl.Button;
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
    
    public Widgets() {
        this.close = new Button("Close");
        this.add(this.close);
        
        this.table = new TreeTable();
        this.add(this.table);
    }

    @Override
    protected void layout() {
        this.close.setPosition(0, 0);
        this.close.setSize(this.close.computeTextWidth() + 20, this.close.computeTextHeight());
        this.close.setTheme("button");
        
        this.table.setPosition(10, 10);
        this.table.setSize(100, 500);
    }
}
