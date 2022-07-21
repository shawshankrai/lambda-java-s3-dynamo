package utils;

import java.io.InputStream;
import java.io.InputStreamReader;

import com.opencsv.CSVReader;

public class CsvUtils {
    
    public static CSVReader getReader(InputStream in) {
        return new CSVReader(new InputStreamReader(in));
    }
        
}
