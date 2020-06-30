package ru.nsu.fit.parser;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ParsedRecord {
    String data;
    List<Double> values;
}
