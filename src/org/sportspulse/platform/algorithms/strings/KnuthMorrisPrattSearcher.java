package org.sportspulse.platform.algorithms.strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Knuth-Morris-Pratt (KMP) Pattern Matcher.
 * Uses the Pi (longest proper prefix which is also suffix) failure function.
 * Time Complexity: O(N + M) worst-case.
 * Space Complexity: O(M) auxiliary for Pi table.
 */
public class KnuthMorrisPrattSearcher {

    public static int[] computePrefixFunction(String pattern) {
        int m = pattern.length();
        int[] pi = new int[m];
        int k = 0;

        for (int i = 1; i < m; i++) {
            while (k > 0 && pattern.charAt(k) != pattern.charAt(i)) {
                k = pi[k - 1];
            }
            if (pattern.charAt(k) == pattern.charAt(i)) {
                k++;
            }
            pi[i] = k;
        }
        return pi;
    }

    public static StringSearchResult search(String text, String pattern) {
        long start = System.nanoTime();
        List<Integer> matches = new ArrayList<>();
        long comparisons = 0;

        if (text == null || pattern == null || pattern.isEmpty() || pattern.length() > text.length()) {
            long time = System.nanoTime() - start;
            return new StringSearchResult("Knuth-Morris-Pratt (KMP)", pattern, text != null ? text.length() : 0, matches, comparisons, time, 0);
        }

        int[] pi = computePrefixFunction(pattern);
        int n = text.length();
        int m = pattern.length();
        int q = 0; // characters matched

        for (int i = 0; i < n; i++) {
            while (q > 0 && pattern.charAt(q) != text.charAt(i)) {
                comparisons++;
                q = pi[q - 1];
            }
            comparisons++;
            if (pattern.charAt(q) == text.charAt(i)) {
                q++;
            }
            if (q == m) {
                matches.add(i - m + 1);
                q = pi[q - 1];
            }
        }

        long time = System.nanoTime() - start;
        return new StringSearchResult("Knuth-Morris-Pratt (KMP)", pattern, n, matches, comparisons, time, 0);
    }
}
