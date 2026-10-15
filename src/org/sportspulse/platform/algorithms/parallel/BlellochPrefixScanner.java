package org.sportspulse.platform.algorithms.parallel;

import java.util.Arrays;

/**
 * Blelloch Work-Efficient Parallel Prefix Scan (Exclusive and Inclusive).
 * Uses two-phase binary tree traversal:
 * Phase 1: Up-Sweep (Parallel Reduction) - Work: O(N), Span: O(log N)
 * Phase 2: Down-Sweep (Prefix Distribution) - Work: O(N), Span: O(log N)
 * Total Work: O(N) (Work-Efficient, unlike Hillis-Steele O(N log N))
 * Total Span: O(log N)
 */
public class BlellochPrefixScanner {

    public static class ScanResult {
        public final long[] originalData;
        public final long[] exclusiveScan;
        public final long[] inclusiveScan;
        public final int work;
        public final int span;
        public final long executionTimeNs;

        public ScanResult(long[] orig, long[] excl, long[] incl, int work, int span, long time) {
            this.originalData = orig;
            this.exclusiveScan = excl;
            this.inclusiveScan = incl;
            this.work = work;
            this.span = span;
            this.executionTimeNs = time;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== BLELLOCH WORK-EFFICIENT PARALLEL SCAN ===\n");
            sb.append("Input:          ").append(Arrays.toString(originalData)).append("\n");
            sb.append("Inclusive Scan: ").append(Arrays.toString(inclusiveScan)).append("\n");
            sb.append("Exclusive Scan: ").append(Arrays.toString(exclusiveScan)).append("\n");
            sb.append(String.format("Theoretical Work: %d operations (O(N))\n", work));
            sb.append(String.format("Theoretical Span: %d steps (O(log N))\n", span));
            sb.append(String.format("Execution Time:   %.3f µs\n", executionTimeNs / 1000.0));
            return sb.toString();
        }
    }

    /**
     * Executes Blelloch Scan on the input array.
     */
    public static ScanResult scan(long[] input) {
        long start = System.nanoTime();
        if (input == null || input.length == 0) {
            return new ScanResult(new long[0], new long[0], new long[0], 0, 0, 0);
        }

        int origLen = input.length;
        // Pad to next power of 2
        int m = nextPowerOfTwo(origLen);
        long[] tree = new long[m];
        System.arraycopy(input, 0, tree, 0, origLen);

        int depth = 31 - Integer.numberOfLeadingZeros(m);
        int work = 0;
        int span = 2 * depth;

        // Phase 1: Up-Sweep (Reduce)
        for (int d = 0; d < depth; d++) {
            int stride = 1 << (d + 1);
            int half = 1 << d;
            for (int k = 0; k < m; k += stride) {
                tree[k + stride - 1] += tree[k + half - 1];
                work++;
            }
        }

        // Total sum stored at root
        long totalSum = tree[m - 1];
        // Set root to identity (0) for exclusive scan
        tree[m - 1] = 0;

        // Phase 2: Down-Sweep
        for (int d = depth - 1; d >= 0; d--) {
            int stride = 1 << (d + 1);
            int half = 1 << d;
            for (int k = 0; k < m; k += stride) {
                long temp = tree[k + half - 1];
                tree[k + half - 1] = tree[k + stride - 1];
                tree[k + stride - 1] += temp;
                work += 2; // Swap and add
            }
        }

        long[] exclusive = new long[origLen];
        long[] inclusive = new long[origLen];
        System.arraycopy(tree, 0, exclusive, 0, origLen);

        for (int i = 0; i < origLen; i++) {
            inclusive[i] = exclusive[i] + input[i];
        }

        long time = System.nanoTime() - start;
        return new ScanResult(input.clone(), exclusive, inclusive, work, span, time);
    }

    private static int nextPowerOfTwo(int n) {
        if (n <= 1) return 1;
        int p = 1;
        while (p < n) {
            p <<= 1;
        }
        return p;
    }
}
