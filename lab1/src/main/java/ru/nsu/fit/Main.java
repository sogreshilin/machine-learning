package ru.nsu.fit;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import lombok.SneakyThrows;
import org.apache.commons.math3.linear.RealVector;
import ru.nsu.fit.algorithm.KMeans;
import ru.nsu.fit.normalizer.Normalizer;
import ru.nsu.fit.parser.ParsedRecord;
import ru.nsu.fit.parser.Parser;

public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        Parser parser = new Parser();
        Normalizer normalizer = new Normalizer();
        KMeans kMeans = new KMeans();
        List<ParsedRecord> parsedRecords = parser.parse(createInputStream("data/water-treatment.data"));
        List<RealVector> observations = normalizer.normalize(parsedRecords);
        List<List<RealVector>> clusters = kMeans.apply(observations, 3);
        clusters.stream().map(List::size).forEach(System.out::println);
    }

    @SneakyThrows
    private static InputStream createInputStream(String filepath) {
        File initialFile = new File(filepath);
        return new FileInputStream(initialFile);
    }
}
