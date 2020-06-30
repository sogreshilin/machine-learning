package ru.nsu.fit.parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.SneakyThrows;

public class Parser {
    @SneakyThrows
    public List<ParsedRecord> parse(InputStream inputStream) {
        ArrayList<ParsedRecord> rv = new ArrayList<>();
        try (var reader = new BufferedReader(new InputStreamReader(inputStream))) {
            while (reader.ready()) {
                String rawString = reader.readLine();
                String[] rawValues = rawString.split(",");

                List<Double> values = Arrays.stream(rawValues).skip(1)
                    .map(this::doubleOrNull)
                    .collect(Collectors.toList());

                rv.add(new ParsedRecord().setData(rawValues[0]).setValues(values));
            }
        }
        return rv;
    }

    private Double doubleOrNull(String string) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
