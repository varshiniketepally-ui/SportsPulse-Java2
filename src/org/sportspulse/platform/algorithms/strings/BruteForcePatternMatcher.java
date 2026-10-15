package org.sportspulse.platform.algorithms.strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Naive (Brute-Force) Pattern Matcher.
 * Slides pattern over text character-by-character.
 * Time Complexity: O(N * M) worst-case.
 * Space Complexity: O(1) auxiliary.
 */
public class BruteForcePatternMatcher {

    public static StringSearchResult search(String text, String pattern) {
        long start = System.nanoTime();
        List<Integer> matches = new ArrayList<>();
        long comparisons = 0;

        if (text == null || pattern == null || pattern.isEmpty() || pattern.length() > text.length()) {
            long time = System.nanoTime() - start;
            return new StringSearchResult("Naive Brute-Force", pattern, text != null ? text.length() : 0, matches, comparisons, time, 0);
        }

        int n = text.length();
        int m = pattern.length();

        for (int i = 0; i <= n - m; i++) {
            boolean matched = true;
            for (int j = 0; j < m; j++) {
                comparisons++;
                if (text.charAt(i + j) != pattern.charAt(j)) {
                    matched = false;
                    break;
                }
            }
            if (matched) {
                matches.add(i);
            }
        }

        long time = System.nanoTime() - start;
        return new StringSearchResult("Naive Brute-Force", pattern, n, matches, comparisons, time, 0);
    }
}
