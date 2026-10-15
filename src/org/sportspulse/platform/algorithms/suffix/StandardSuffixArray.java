package org.sportspulse.platform.algorithms.suffix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Standard Suffix Array implementation with Prefix Doubling (O(N log^2 N)).
 * Supports binary search substring queries in O(M log N) time.
 */
public class StandardSuffixArray {

    private final String text;
    private final int[] sa;
    private final long constructionTimeNs;

    public StandardSuffixArray(String text) {
        this.text = text != null ? text : "";
        long start = System.nanoTime();
        this.sa = buildSuffixArray(this.text);
        this.constructionTimeNs = System.nanoTime() - start;
    }

    private static int[] buildSuffixArray(String s) {
        int n = s.length();
        if (n == 0) return new int[0];

        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;

        int[] rank = new int[n];
        for (int i = 0; i < n; i++) rank[i] = s.charAt(i);

        int[] nextRank = new int[n];

        for (int k = 1; k < n; k *= 2) {
            final int offset = k;
            final int[] currRank = rank;

            Arrays.sort(order, (i, j) -> {
                if (currRank[i] != currRank[j]) {
                    return Integer.compare(currRank[i], currRank[j]);
                }
                int ri = (i + offset < n) ? currRank[i + offset] : -1;
                int rj = (j + offset < n) ? currRank[j + offset] : -1;
                return Integer.compare(ri, rj);
            });

            nextRank[order[0]] = 0;
            for (int i = 1; i < n; i++) {
                int prev = order[i - 1];
                int curr = order[i];
                boolean sameFirst = currRank[prev] == currRank[curr];
                int rPrev = (prev + offset < n) ? currRank[prev + offset] : -1;
                int rCurr = (curr + offset < n) ? currRank[curr + offset] : -1;
                boolean sameSecond = rPrev == rCurr;

                nextRank[curr] = (sameFirst && sameSecond) ? nextRank[prev] : nextRank[prev] + 1;
            }

            System.arraycopy(nextRank, 0, rank, 0, n);
            if (rank[order[n - 1]] == n - 1) {
                break; // All ranks unique
            }
        }

        int[] result = new int[n];
        for (int i = 0; i < n; i++) result[i] = order[i];
        return result;
    }

    public int[] getSuffixArray() {
        return sa;
    }

    public long getConstructionTimeNs() {
        return constructionTimeNs;
    }

    public String getText() {
        return text;
    }

    /**
     * Binary search to find all occurrences of pattern in text.
     */
    public List<Integer> searchSubstring(String pattern) {
        List<Integer> occurrences = new ArrayList<>();
        if (pattern == null || pattern.isEmpty() || sa.length == 0) {
            return occurrences;
        }

        int n = text.length();
        int m = pattern.length();

        // Find lower bound
        int low = 0, high = n - 1;
        int first = -1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int suffixStart = sa[mid];
            int cmp = comparePrefix(pattern, text, suffixStart);
            if (cmp <= 0) {
                if (cmp == 0) first = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        if (first == -1) return occurrences;

        // Find upper bound
        low = first;
        high = n - 1;
        int last = first;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int suffixStart = sa[mid];
            int cmp = comparePrefix(pattern, text, suffixStart);
            if (cmp == 0) {
                last = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        for (int i = first; i <= last; i++) {
            occurrences.add(sa[i]);
        }
        return occurrences;
    }

    private static int comparePrefix(String pattern, String text, int suffixStart) {
        int m = pattern.length();
        int maxLen = Math.min(m, text.length() - suffixStart);
        for (int i = 0; i < maxLen; i++) {
            char cp = pattern.charAt(i);
            char ct = text.charAt(suffixStart + i);
            if (cp != ct) {
                return Character.compare(cp, ct);
            }
        }
        if (maxLen < m) {
            return 1; // Pattern is longer than suffix
        }
        return 0; // Match prefix
    }
}
