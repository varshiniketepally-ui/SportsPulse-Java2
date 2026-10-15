package org.sportspulse.platform.algorithms.randomized;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Randomized QuickSort with Stochastic Pivot Selection.
 * Avoids O(N^2) worst-case on pre-sorted sports datasets.
 * Average Time Complexity: O(N log N) with high probability.
 * Auxiliary Space: O(log N) stack frames.
 */
public class StochasticQuickSort {

    public static class SortMetrics {
        public long comparisons = 0;
        public long swaps = 0;
        public long executionTimeNs = 0;
    }

    private static final Random RANDOM = new Random();

    public static <T> SortMetrics sort(List<T> list, Comparator<? super T> comparator) {
        SortMetrics metrics = new SortMetrics();
        if (list == null || list.size() <= 1) {
            return metrics;
        }

        long start = System.nanoTime();
        quickSort(list, 0, list.size() - 1, comparator, metrics, RANDOM);
        metrics.executionTimeNs = System.nanoTime() - start;
        return metrics;
    }

    public static <T> SortMetrics sortDeterministic(List<T> list, Comparator<? super T> comparator, long seed) {
        SortMetrics metrics = new SortMetrics();
        if (list == null || list.size() <= 1) {
            return metrics;
        }

        Random seededRandom = new Random(seed);
        long start = System.nanoTime();
        quickSort(list, 0, list.size() - 1, comparator, metrics, seededRandom);
        metrics.executionTimeNs = System.nanoTime() - start;
        return metrics;
    }

    private static <T> void quickSort(List<T> list, int low, int high, Comparator<? super T> comp,
                                      SortMetrics metrics, Random rng) {
        if (low < high) {
            int pIdx = partition(list, low, high, comp, metrics, rng);
            quickSort(list, low, pIdx - 1, comp, metrics, rng);
            quickSort(list, pIdx + 1, high, comp, metrics, rng);
        }
    }

    private static <T> int partition(List<T> list, int low, int high, Comparator<? super T> comp,
                                     SortMetrics metrics, Random rng) {
        // Stochastic Pivot Selection: pick uniform random index in [low, high]
        int randPivot = low + rng.nextInt(high - low + 1);
        swap(list, randPivot, high, metrics);

        T pivot = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            metrics.comparisons++;
            if (comp.compare(list.get(j), pivot) <= 0) {
                i++;
                swap(list, i, j, metrics);
            }
        }

        swap(list, i + 1, high, metrics);
        return i + 1;
    }

    private static <T> void swap(List<T> list, int i, int j) {
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    private static <T> void swap(List<T> list, int i, int j, SortMetrics metrics) {
        if (i != j) {
            metrics.swaps++;
            swap(list, i, j);
        }
    }
}
