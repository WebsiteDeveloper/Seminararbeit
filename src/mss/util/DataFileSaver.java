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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 *
 * @author Bernhard Sirlinger
 */
public class DataFileSaver extends Thread {
    private final double deltaT;
    private final ArrayList<ArrayList<Planet>> results;
    private final String filePath;

    public DataFileSaver(String filePath, double deltaT, ArrayList<ArrayList<Planet>> results) {
        this.deltaT = deltaT;
        this.results = results;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        File file = new File(this.filePath); // The file to save to.

        Charset charset = Charset.forName("UTF-8");
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), charset)) {
            int size = this.results.size();
            String temp;
            for(int i = 0; i < size; i++) {
                temp = "" + this.deltaT * i;
                for(int j = 0; j < this.results.get(i).size(); j++) {
                    Planet tmp = this.results.get(i).get(j);
                    
                    temp += " " + tmp.getDataString(" ");
                }
                temp += "\n";
                writer.write(temp);
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }
}
