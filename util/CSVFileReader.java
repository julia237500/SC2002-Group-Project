package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for reading CSV files into memory.
 * Each line in the file is read and split into a list of strings, with
 * commas as delimiters. Escaped commas (preceded by backslashes) are not split.
 */
public class CSVFileReader {

    /**
     * Reads a CSV file and returns its contents as a list of rows.
     * Each row is a list of strings, where each string represents a column value.
     * 
     * @param filePath the path to the CSV file
     * @return a list of rows, where each row is a list of string values
     * @throws IOException if an I/O error occurs reading from the file
     */
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
