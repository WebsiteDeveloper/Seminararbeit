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
import com.google.gson.GsonBuilder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import mss.integratoren.Integratoren;

/**
 *
 * @author Bernhard Sirlinger
 */
public class ProjectFileSaver extends Thread {

    private final Project project;
    private final String filePath;

    public ProjectFileSaver(String filePath, ArrayList<Planet> planets, Integratoren integrator, double deltaT) {
        this.project = new Project(planets, integrator, deltaT);
        this.filePath = filePath;
    }

    @Override
    public void run() {
        File file = new File(this.filePath); // The file to save to.

        Charset charset = Charset.forName("UTF-8");
        String s;
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), charset)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            s = gson.toJson(project);
            writer.write(s);
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }
}
