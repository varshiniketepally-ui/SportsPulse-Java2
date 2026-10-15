package org.sportspulse.platform.algorithms.parallel;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Parallel Reduce using ForkJoin framework.
 * Aggregates sports telemetry: goals, passes, distances, heart rates, and workload metrics.
 * Compares Sequential vs Multi-Threaded Parallel performance.
 */
public class ForkJoinParallelReducer {

    private static final int SEQUENTIAL_THRESHOLD = 5000;
    private static final ForkJoinPool POOL = new ForkJoinPool();

    public static class ReductionComparison {
        public final long totalSum;
        public final long sequentialTimeNs;
        public final long parallelTimeNs;
        public final int parallelism;
        public final double speedup;

        public ReductionComparison(long totalSum, long seqTime, long parTime, int parallelism) {
            this.totalSum = totalSum;
            this.sequentialTimeNs = seqTime;
            this.parallelTimeNs = parTime;
            this.parallelism = parallelism;
            this.speedup = parTime > 0 ? (double) seqTime / parTime : 1.0;
        }

        @Override
        public String toString() {
            return String.format("Reduction Sum: %d\nSequential Time: %.3f µs | Parallel Time: %.3f µs (%d threads) | Speedup: %.2fx",
                    totalSum, sequentialTimeNs / 1000.0, parallelTimeNs / 1000.0, parallelism, speedup);
        }
    }

    private static class SumTask extends RecursiveTask<Long> {
        private final long[] data;
        private final int start;
        private final int end;

        public SumTask(long[] data, int start, int end) {
            this.data = data;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            int length = end - start;
            if (length <= SEQUENTIAL_THRESHOLD) {
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += data[i];
                }
                return sum;
            }

            int mid = start + (length / 2);
            SumTask leftTask = new SumTask(data, start, mid);
            SumTask rightTask = new SumTask(data, mid, end);

            leftTask.fork();
            long rightResult = rightTask.compute();
            long leftResult = leftTask.join();

            return leftResult + rightResult;
        }
    }

    public static ReductionComparison compare(long[] data) {
        if (data == null || data.length == 0) {
            return new ReductionComparison(0, 0, 0, 1);
        }

        // Sequential run
        long t1 = System.nanoTime();
        long seqSum = 0;
        for (long val : data) {
            seqSum += val;
        }
        long seqTime = System.nanoTime() - t1;

        // Parallel ForkJoin run
        long t2 = System.nanoTime();
        long parSum = POOL.invoke(new SumTask(data, 0, data.length));
        long parTime = System.nanoTime() - t2;

        return new ReductionComparison(parSum, seqTime, parTime, POOL.getParallelism());
    }

    public static long sequentialSum(long[] data) {
        long sum = 0;
        for (long v : data) sum += v;
        return sum;
    }

    public static long parallelSum(long[] data) {
        return POOL.invoke(new SumTask(data, 0, data.length));
    }
}
