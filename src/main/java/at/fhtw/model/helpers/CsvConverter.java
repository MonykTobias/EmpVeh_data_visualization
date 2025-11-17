package at.fhtw.model.helpers;

import at.fhtw.model.InputData;
import at.fhtw.model.InputTable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

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
            return mapper.writer(schema)
                    .writeValueAsString(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T deserialize(String csvContent) {
        try {
            CsvMapper csvMapper = new CsvMapper();
            CsvSchema schema = CsvSchema.emptySchema().withHeader(); // read headers from first line

            if (type.equals(InputTable.class)) {
                // Read rows as InputData list
                MappingIterator<InputData> it = csvMapper
                        .readerFor(InputData.class)
                        .with(schema)
                        .readValues(new StringReader(csvContent));

                List<InputData> list = it.readAll();
                InputTable table = new InputTable(list);
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
