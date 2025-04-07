package org.gym;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class JsonDataIO<T> implements DataIO<T> {
    private final ObjectMapper mapper;
    private final Class<T[]> typeArray;

    public JsonDataIO(Class<T[]> typeArray) {
        this.mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.typeArray = typeArray;
    }
    public JsonDataIO(Class<T[]> typeArray, ObjectMapper mapper) {
        this.mapper = mapper;
        this.typeArray = typeArray;
    }

    @Override
    public void exportData(List<T> data, File file, Comparator<T> sorter) throws IOException {
        data.sort(sorter);
        mapper.writeValue(file, data);
    }

    @Override
    public void exportData(List<T> data, File file) throws IOException {
        mapper.writeValue(file, data);
    }

    @Override
    public List<T> importData(File file) throws IOException {
        T[] array = mapper.readValue(file, typeArray);
        return List.of(array);
    }
}
