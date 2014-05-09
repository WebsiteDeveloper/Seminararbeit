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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 *
 * @author Bernhard Sirlinger
 */
public class DataFileSaver extends Thread {
    private final double deltaT;
    private final File dataFile;
    private final String filePath;

    public DataFileSaver(String filePath, double deltaT, File dataFile) {
        this.deltaT = deltaT;
        this.dataFile = dataFile;
        this.filePath = filePath;
    }
    
    private void write() {
        InputStream in = null;
        try {
            File file = new File(this.filePath);
            
            in = new BufferedInputStream(new FileInputStream(this.dataFile));
            JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
            
            
            
            Charset charset = Charset.forName("UTF-8");
            try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), charset)) {
                int i = 0;
                String temp;
                Planet tmp;
                Gson gson = new Gson();
                
                reader.setLenient(false);
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.beginArray();
                    temp = String.format("%-10.6f", this.deltaT * i);
                    while (reader.hasNext()) {
                        try {
                            tmp = gson.fromJson(reader, Planet.class);
                            temp += " " + tmp.getDataString(" ");
                        } catch (JsonSyntaxException ex) {
                            System.err.println(ex.getMessage());
                        }
                    }
                    temp += "\n";
                    writer.write(temp);
                    writer.flush();
                    reader.endArray();
                    i++;
                }
                reader.endArray();
                reader.close();
            } catch (IOException x) {
                x.printStackTrace();
                System.err.format("IOException: %s%n", x);//TODO: Look for Fix for EOFException on reader.hasNext()
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
        } finally {
            try {
                if(in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    @Override
    public void run() {
        this.write();
    }
}
