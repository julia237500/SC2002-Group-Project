package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class CSVFileWriter {
    private static final String TEMP_FILE_PATH = "./data/temp.csv";

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

    private static String joinLine(List<String> line){
        StringBuilder sb = new StringBuilder();

        for(String data:line){
            sb.append(data);
            sb.append(',');
        }

        sb.setLength(sb.length() - 1);

        return sb.toString();
    }
}
