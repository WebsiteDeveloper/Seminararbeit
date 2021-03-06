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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;

/**
 *
 * @author Bernhard Sirlinger
 */
public class ScreenshotSaver extends Thread {
    private static int count = 0;
    private final ByteBuffer buffer;
    private final String filepath;
    private final int width;
    private final int height;

    public ScreenshotSaver(ByteBuffer buffer, String filepath, int width, int height) {
        this.buffer = buffer;
        this.filepath = filepath;
        this.width = width;
        this.height = height;
    }

    @Override
    public void run() {
        ScreenshotSaver.count++;
        File file = new File(this.filepath + File.separator + "screenshot" + ScreenshotSaver.count + ".png"); // The file to save to.
        String format = "PNG";
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                int i = (x + (width * y)) * 4;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
            }
        }

        try {
            ImageIO.write(image, format, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
