/*
 * The MIT License
 *
 * Copyright 2015 Bernhard Sirlinger.
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
package mss.gui;

import java.awt.Color;
import java.nio.FloatBuffer;
import mss.util.Util;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL40;

/**
 *
 * @author Bernhard Sirlinger
 */
public class GLButton {
    private float width;
    private float height;
    private Color[] colors;
    private ButtonState state;
    private int buffer;
    
    public GLButton(float width, float height) {
        this.width = width;
        this.height = height;
        this.colors = new Color[ButtonState.values().length];
        this.state = ButtonState.ACTIVE;
    }

    public GLButton(float width, float height, Color[] colors) {
        if(colors.length != ButtonState.values().length) {
            throw new UnsupportedOperationException("Color count does not match count of possible ButtonStates");
        }
        
        this.width = width;
        this.height = height;
        this.colors = colors;
        this.state = ButtonState.ACTIVE;
        
        initBuffer();
    }
    
    public GLButton(float width, float height, Color[] colors, ButtonState state) {
        if(colors.length != ButtonState.values().length) {
            throw new UnsupportedOperationException("Color count does not match count of possible ButtonStates");
        }
        
        this.width = width;
        this.height = height;
        this.colors = colors;
        this.state = state;
        
        initBuffer();
    }
    
    private void initBuffer() {
        this.buffer = Util.createVBOID();
        
        
        float data[] = {0, 0, 0, 0, -this.height, 0, this.width, -this.height, 0, this.width, 0, 0};
        FloatBuffer vbo = FloatBuffer.wrap(data);
        
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.buffer);
        GL15.glBufferData(this.buffer, vbo, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.buffer);
        GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, true, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.buffer);
    }
    
    public void draw() {
        GL11.glDrawArrays(this.buffer, 0, 4);
    }
    
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Color[] getColors() {
        return colors;
    }

    public void setColors(Color[] colors) {
        this.colors = colors;
    }

    public ButtonState getState() {
        return state;
    }

    public void setState(ButtonState state) {
        this.state = state;
    }
}
