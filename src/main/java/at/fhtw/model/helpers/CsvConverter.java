package at.fhtw.model.helpers;

import at.fhtw.model.InputData;
import at.fhtw.model.InputDataTable;
import at.fhtw.model.Validation;
import at.fhtw.model.ValidationTable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                // Read rows as InputData list
                MappingIterator<InputData> it = csvMapper
                        .readerFor(InputData.class)
                        .with(schema)
                        .readValues(new StringReader(csvContent));

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
}
