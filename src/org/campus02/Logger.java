package org.campus02;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {

    String pathToFile;

    public Logger(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public void writeLogEntry(String entry) throws IOException {
        // erstellt einen PrintWriter
        // Mit dem Wert true werden alle neuen Daten angefügt und die Datei nicht überschrieben
        try (PrintWriter pw = new PrintWriter(new FileWriter(pathToFile, true))) {
            pw.println(entry);
            pw.flush();
        }
    }
}
