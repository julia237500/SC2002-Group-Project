package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Utility class for writing CSV files safely.
 * This class writes data to a temporary file before replacing the original file
 * to prevent data corruption in case of write errors.
 */
public class CSVFileWriter {
    private static final String TEMP_FILE_PATH = "./data/temp.csv";

    /**
     * Writes a list of CSV lines to the specified file path.
     * Each line is a list of strings representing individual columns.
     * The data is written to a temporary file first, which is then renamed to the target path.
     *
     * @param path  the final file path to write the CSV data to
     * @param lines a list of CSV lines, where each line is a list of strings
     * @throws IOException if an I/O error occurs during writing or moving the file
     */
    public static void writeFile(String path, List<List<String>> lines) throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_FILE_PATH));
        for (List<String> line:lines){
            String s = joinLine(line);
            writer.write(s);
            writer.newLine();
        }
        writer.close();

        Files.move(Paths.get(TEMP_FILE_PATH), Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Joins a list of strings into a single CSV-formatted line.
     * Assumes the values do not contain commas, quotes, or newlines.
     *
     * @param line a list of string values to join
     * @return a comma-separated line in CSV format
     */
    private static String joinLine(List<String> line){
        StringBuilder sb = new StringBuilder();

        for(String data:line){
            sb.append(data);
            sb.append(',');
        }

        // Remove the trailing comma
        sb.setLength(sb.length() - 1);

        return sb.toString();
    }
}
