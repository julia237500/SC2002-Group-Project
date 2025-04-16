package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVFileReader {
    public static List<List<String>> readFile(String filePath) throws IOException{
        List<List<String>> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("(?<!\\\\),");
                lines.add(Arrays.asList(values));
            }
        }
        return lines;
    }
}
