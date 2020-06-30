package ru.nsu.fit.normalizer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;
import ru.nsu.fit.parser.ParsedRecord;

public class Normalizer {
    public List<RealVector> normalize(List<ParsedRecord> records) {
        if (records.size() == 0) {
            return List.of();
        }
        int dimension = records.get(0).getValues().size();
        List<Pair<Double, Integer>> sumCountPairs = new ArrayList<>(dimension);
        for (int i = 0; i < dimension; ++i) {
            sumCountPairs.add(Pair.of(0.0, 0));
        }

        for (ParsedRecord record : records) {
            for (int i = 0; i < dimension; ++i) {
                Double value = record.getValues().get(i);
                if (value != null) {
                    Pair<Double, Integer> pair = sumCountPairs.get(i);
                    sumCountPairs.set(i, Pair.of(pair.getLeft() + value, pair.getRight() + 1));
                }
            }
        }

        List<Double> averageValues = sumCountPairs.stream()
            .map(pair -> pair.getLeft() / pair.getRight())
            .collect(Collectors.toList());

        List<RealVector> rv = new ArrayList<>(records.size());
        for (ParsedRecord record : records) {
            List<Double> values = record.getValues();
            List<Double> normalizedValues = new ArrayList<>(values.size());
            for (int i = 0; i < dimension; ++i) {
                Double value = values.get(i);
                normalizedValues.add(value != null ? value : averageValues.get(i));
            }
            rv.add(MatrixUtils.createRealVector(normalizedValues.stream().mapToDouble(v -> v).toArray()));
        }
        return rv;
    }
}
