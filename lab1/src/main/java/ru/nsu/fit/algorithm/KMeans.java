package ru.nsu.fit.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.RealVector;

public class KMeans {

    private static final Collector<?, ?, ?> SHUFFLER = Collectors.collectingAndThen(
        Collectors.toCollection(ArrayList::new),
        list -> {
            Collections.shuffle(list);
            return list;
        }
    );

    public List<List<RealVector>> apply(List<RealVector> observations, int clusterCount) {
        if (clusterCount <= 0) {
            throw new RuntimeException("Cluster count must be greater than 0, but was " + clusterCount);
        }

        int observationsCount = observations.size();
        if (clusterCount > observationsCount) {
            throw new RuntimeException("Cluster count must be less than observations count");
        }

        List<RealVector> nextClusterCenters = ListUtils.partition(
            observations.stream().collect(toShuffledList()),
            observationsCount / clusterCount + (observationsCount % clusterCount == 0 ? 0 : 1)
        )
            .stream()
            .map(list -> list.stream().reduce(RealVector::add).map(vector -> vector.mapDivide(list.size())).orElseThrow())
            .collect(Collectors.toList());


        while (true) {
            // Начальный шаг: инициализация кластеров
            List<RealVector> clusterCenters = nextClusterCenters;

            // Распределение векторов по кластерам
            List<List<RealVector>> clusters = new ArrayList<>(clusterCount);
            for (int i = 0; i < clusterCount; ++i) {
                clusters.add(new ArrayList<>());
            }

            for (RealVector observation : observations) {
                int clusterIndex = IntStream.range(0, clusterCount)
                    .mapToObj(i -> Pair.of(i, clusterCenters.get(i)))
                    .map(pair -> Pair.of(pair.getLeft(), pair.getRight().getDistance(observation)))
                    .min(Comparator.comparing(Pair::getRight))
                    .map(Pair::getLeft)
                    .orElseThrow();

                clusters.get(clusterIndex).add(observation);
            }

            // Пересчет центров кластеров
            nextClusterCenters = new ArrayList<>(clusterCount);
            for (int i = 0; i < clusterCount; ++i) {
                List<RealVector> cluster = clusters.get(i);
                if (cluster.size() == 0) {
                    nextClusterCenters.add(clusterCenters.get(i));
                } else {
                    nextClusterCenters.add(
                        cluster.stream()
                            .reduce(RealVector::add)
                            .map(vector -> vector.mapDivide(cluster.size()))
                            .orElseThrow()
                    );
                }
            }

            boolean isFinished = true;
            for (int i = 0; i < clusterCount; ++i) {
                if (!clusterCenters.get(i).equals(nextClusterCenters.get(i))) {
                    isFinished = false;
                    break;
                }
            }

            if (isFinished) {
                return clusters;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Collector<T, ?, List<T>> toShuffledList() {
        return (Collector<T, ?, List<T>>) SHUFFLER;
    }
}
