package dev.hv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CSVParser {

    private String nextLine;
    private BufferedReader bufferedReader;
    private FileReader fileReader;

    public CSVParser(String filePath) throws IOException {
        String absolutePath = new File(filePath).getAbsolutePath();
        fileReader = new FileReader(absolutePath, StandardCharsets.UTF_8);
        bufferedReader = new BufferedReader(fileReader);
    }

    public boolean hasNext() {
        try {
            nextLine = bufferedReader.readLine();
        } catch (IOException e) {
            nextLine = null;
            return false;
        }
        if (nextLine == null) {
            try {
                fileReader.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return nextLine != null;
    }

    public String next() {
        return nextLine;
    }
}
