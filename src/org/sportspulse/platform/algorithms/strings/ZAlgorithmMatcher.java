package org.sportspulse.platform.algorithms.strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Z-Algorithm (Z-Function) Pattern Matcher and Text Repetition Analyzer.
 * Computes Z-array in linear time O(N).
 * Time Complexity: O(N + M) worst-case.
 * Space Complexity: O(N + M) auxiliary.
 */
public class ZAlgorithmMatcher {

    /**
     * Computes the Z-array for string S.
     * Z[i] is the length of the longest common prefix between S and S[i..len-1].
     */
    public static int[] computeZArray(String s) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0, r = 0;

        for (int i = 1; i < n; i++) {
            if (i <= r) {
                z[i] = Math.min(r - i + 1, z[i - l]);
            }
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }
            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }
        }
        return z;
    }

    public static StringSearchResult search(String text, String pattern) {
        long start = System.nanoTime();
        List<Integer> matches = new ArrayList<>();
        long comparisons = 0;

        if (text == null || pattern == null || pattern.isEmpty() || pattern.length() > text.length()) {
            long time = System.nanoTime() - start;
            return new StringSearchResult("Z-Algorithm", pattern, text != null ? text.length() : 0, matches, comparisons, time, 0);
        }

        // Delimiter that is not present in pattern or text
        char delim = '$';
        String concat = pattern + delim + text;
        int m = pattern.length();
        int totalLen = concat.length();
        int[] z = new int[totalLen];
        int l = 0, r = 0;

        for (int i = 1; i < totalLen; i++) {
            if (i <= r) {
                z[i] = Math.min(r - i + 1, z[i - l]);
            }
            while (i + z[i] < totalLen) {
                comparisons++;
                if (concat.charAt(z[i]) == concat.charAt(i + z[i])) {
                    z[i]++;
                } else {
                    break;
                }
            }
            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }

            if (i > m && z[i] == m) {
                // Match index in text
                matches.add(i - m - 1);
            }
        }

        long time = System.nanoTime() - start;
        return new StringSearchResult("Z-Algorithm", pattern, text.length(), matches, comparisons, time, 0);
    }

    /**
     * Identifies the most frequently repeated phrase or period in commentary.
     */
    public static int findLongestPrefixSuffixPeriod(String text) {
        if (text == null || text.isEmpty()) return 0;
        int[] z = computeZArray(text);
        int maxLps = 0;
        for (int i = 1; i < text.length(); i++) {
            if (i + z[i] == text.length()) {
                maxLps = Math.max(maxLps, z[i]);
            }
        }
        return maxLps;
    }
}
