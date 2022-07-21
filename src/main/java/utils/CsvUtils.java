package utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class CsvUtils {
    
    public static CSVReader getReader(InputStream in) {
        return new CSVReader(new InputStreamReader(in));
    }
        
    public static CSVWriter getWriter(OutputStreamWriter  streamWriter) {
        return new CSVWriter(streamWriter, ',',Character.MIN_VALUE,'"', System.lineSeparator());
    }
}
