/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
    private static int count = 1;
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
        File file = new File(this.filepath + "/" + "screenshot" + ScreenshotSaver.count + ".png"); // The file to save to.
        String format = "PNG"; // Example: "PNG" or "JPG"
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
            ScreenshotSaver.count++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
