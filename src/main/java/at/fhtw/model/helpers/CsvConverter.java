package at.fhtw.model.helpers;

import at.fhtw.model.Expression;
import at.fhtw.model.InputData;
import at.fhtw.model.InputDataTable;
import at.fhtw.model.Validation;
import at.fhtw.model.ValidationTable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class CsvConverter<T> extends BaseConverter<T> {
    CsvSchema schema;

    public CsvConverter(Class<T> clazz) {
        super(clazz);
        mapper = new CsvMapper();
        schema = ((CsvMapper)mapper)
                .schemaFor(type)
                .withHeader();

    }

    @Override
    public String serialize(T t) {
        try {
            CsvMapper csvMapper = (CsvMapper) mapper;
            if (t instanceof ValidationTable) {
                ValidationTable table = (ValidationTable) t;
                Collection<Validation> values = table.getValidationTable().values();
                CsvSchema validationSchema = csvMapper.schemaFor(Validation.class).withHeader();
                return csvMapper.writer(validationSchema).writeValueAsString(values);
            }
            if (t instanceof InputDataTable) {
                InputDataTable table = (InputDataTable) t;
                List<InputData> values = table.getInputTable();
                CsvSchema inputDataSchema = csvMapper.schemaFor(InputData.class).withHeader();
                return csvMapper.writer(inputDataSchema).writeValueAsString(values);
            }
            // Fallback for other types - this might still fail for complex objects
            return mapper.writer(schema).writeValueAsString(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T deserialize(String csvContent) {
        try {
            CsvMapper csvMapper = new CsvMapper();
            CsvSchema schema = CsvSchema.emptySchema().withHeader(); // read headers from first line

            if (type.equals(InputDataTable.class)) {
                // Preprocess CSV content to handle NULL expressions and empty values
                String preprocessedContent = preprocessInputDataCsv(csvContent);

                // Read rows as InputData list
                MappingIterator<InputData> it = csvMapper
                        .readerFor(InputData.class)
                        .with(schema)
                        .readValues(new StringReader(preprocessedContent));

                List<InputData> list = it.readAll();
                InputDataTable table = new InputDataTable(list);
                return type.cast(table);
            } else if (type.equals(ValidationTable.class)) {
                // Handle empty CSV content for new files
                if (csvContent == null || csvContent.trim().isEmpty()) {
                    return type.cast(new ValidationTable());
                }
                // Read rows as Validation list
                MappingIterator<Validation> it = csvMapper
                        .readerFor(Validation.class)
                        .with(schema)
                        .readValues(new StringReader(csvContent));

                List<Validation> list = it.readAll();
                Map<Integer, Validation> map = new HashMap<>();
                for (Validation validation : list) {
                    map.put(validation.getId(), validation);
                }
                ValidationTable table = new ValidationTable(map);
                return type.cast(table);
            } else {
                // Fallback for other classes
                return csvMapper.readerFor(type)
                        .with(schema)
                        .readValue(new StringReader(csvContent));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Preprocesses CSV content for InputData to handle special cases:
     * - Rows with empty expression_best are set to NULL
     * - Empty numeric values are replaced with 0.0
     * - Removes extra columns beyond the 12 expected fields
     * - Auto-assigns row IDs if first column is empty
     */
    private String preprocessInputDataCsv(String csvContent) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(csvContent));
        StringBuilder result = new StringBuilder();

        String headerLine = reader.readLine();
        if (headerLine == null) {
            return csvContent;
        }

        // Process header to limit to exactly 12 columns and fix empty first column
        String processedHeader = processHeaderLine(headerLine);
        result.append(processedHeader).append("\n");

        String line;
        int rowId = 0;
        while ((line = reader.readLine()) != null) {
            String processedLine = processInputDataLine(line, rowId);
            result.append(processedLine).append("\n");
            rowId++;
        }

        return result.toString();
    }

    /**
     * Processes the header line to limit to exactly 12 columns and replace empty first column with "id"
     */
    private String processHeaderLine(String line) {
        String[] parts = line.split(",", -1);
        String[] limitedParts = new String[12];

        for (int i = 0; i < 12; i++) {
            if (i < parts.length) {
                limitedParts[i] = parts[i];
            } else {
                limitedParts[i] = "";
            }
        }

        // Replace empty first column with "id"
        if (limitedParts[0].trim().isEmpty()) {
            limitedParts[0] = "id";
        }

        return String.join(",", limitedParts);
    }

    /**
     * Processes a single CSV line for InputData.
     * Expected format: id,timestamp,expression_best,confidence,neutral,happy,surprise,anger,presence,pitch,roll,yaw
     * Handles empty expression_best by setting it to NULL and fills empty numeric values with 0.0
     * Auto-assigns ID if the first column is empty
     */
    private String processInputDataLine(String line, int autoId) {
        String[] parts = line.split(",", -1); // -1 to include trailing empty strings

        // Limit to exactly 12 columns
        String[] limitedParts = new String[12];
        for (int i = 0; i < 12; i++) {
            if (i < parts.length) {
                limitedParts[i] = parts[i];
            } else {
                limitedParts[i] = "";
            }
        }
        parts = limitedParts;

        // Column indices (0-based):
        // 0: id, 1: timestamp, 2: expression_best, 3-10: confidence values, 11: yaw

        // Auto-assign ID if first column is empty
        if (parts[0].trim().isEmpty()) {
            parts[0] = String.valueOf(autoId);
        }

        // Check if expression_best (index 2) is empty or null
        if (parts[2].trim().isEmpty() || parts[2].trim().equalsIgnoreCase("null")) {
            parts[2] = "NULL";
        }

        // Fill empty numeric values with 0.0 (columns 3-11)
        for (int i = 3; i < 12; i++) {
            if (parts[i].trim().isEmpty()) {
                parts[i] = "0.0";
            }
        }

        return String.join(",", parts);
    }
}
