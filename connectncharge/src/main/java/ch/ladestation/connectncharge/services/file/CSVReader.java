package ch.ladestation.connectncharge.services.file;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class CSVReader {

    public static final String LEDSEGMENTS_CSV = "/LEDSegments.csv";
    static final String SEMICOLON_DELIMITER = ";";

    /**
     * hide utility constructor
     */
    private CSVReader() {
    }

    /**
     * reads LEDSegments.csv and returns a list of lists of integers.
     * <p>
     * The outer list corresponds to rows. the inner list to collumns.
     *
     * @return the parsed LEDSegments.csv file
     */
    public static List<List<String>> readCSV() {
        List<List<String>> records = new ArrayList<>();
        InputStream csv = CSVReader.class.getResourceAsStream(LEDSEGMENTS_CSV);
        Objects.requireNonNull(csv, "error, " + LEDSEGMENTS_CSV + " file was null");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(csv, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(SEMICOLON_DELIMITER);
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            throw new RuntimeException("error when trying to read " + LEDSEGMENTS_CSV + ":" + e.getMessage());
        }
        return records;
    }
}
