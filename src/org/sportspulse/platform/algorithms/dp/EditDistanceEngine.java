package org.sportspulse.platform.algorithms.dp;

import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic Programming String Alignment Engine:
 * - Standard Levenshtein Distance (Insert, Delete, Substitute)
 * - Damerau-Levenshtein Distance (including adjacent Transposition)
 * - Typo Correction and Fuzzy Sports Entity Resolution
 */
public class EditDistanceEngine {

    public static class DistanceComparison {
        private final String source;
        private final String target;
        private final int levenshteinDistance;
        private final int damerauLevenshteinDistance;
        private final boolean transpositionDetected;

        public DistanceComparison(String source, String target, int lev, int damerau) {
            this.source = source;
            this.target = target;
            this.levenshteinDistance = lev;
            this.damerauLevenshteinDistance = damerau;
            this.transpositionDetected = damerau < lev;
        }

        public String getSource() { return source; }
        public String getTarget() { return target; }
        public int getLevenshteinDistance() { return levenshteinDistance; }
        public int getDamerauLevenshteinDistance() { return damerauLevenshteinDistance; }
        public boolean isTranspositionDetected() { return transpositionDetected; }

        @Override
        public String toString() {
            return String.format("Source: '%s' -> Target: '%s' | Levenshtein: %d | Damerau-Levenshtein: %d%s",
                    source, target, levenshteinDistance, damerauLevenshteinDistance,
                    transpositionDetected ? " (Transposition Detected)" : "");
        }
    }

    /**
     * Standard Levenshtein Distance in O(N * M) time and O(N * M) space.
     */
    public static int computeLevenshtein(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";

        int n = s1.length();
        int m = s2.length();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            char c1 = s1.charAt(i - 1);
            for (int j = 1; j <= m; j++) {
                char c2 = s2.charAt(j - 1);
                int cost = (Character.toLowerCase(c1) == Character.toLowerCase(c2)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1,      // Deletion
                                 dp[i][j - 1] + 1),     // Insertion
                        dp[i - 1][j - 1] + cost         // Substitution
                );
            }
        }
        return dp[n][m];
    }

    /**
     * Damerau-Levenshtein Distance with adjacent transpositions.
     */
    public static int computeDamerauLevenshtein(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";

        int n = s1.length();
        int m = s2.length();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            char c1 = s1.charAt(i - 1);
            for (int j = 1; j <= m; j++) {
                char c2 = s2.charAt(j - 1);
                int cost = (Character.toLowerCase(c1) == Character.toLowerCase(c2)) ? 0 : 1;

                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1,
                                 dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );

                // Check for transposition
                if (i > 1 && j > 1 &&
                        Character.toLowerCase(c1) == Character.toLowerCase(s2.charAt(j - 2)) &&
                        Character.toLowerCase(s1.charAt(i - 2)) == Character.toLowerCase(c2)) {
                    dp[i][j] = Math.min(dp[i][j], dp[i - 2][j - 2] + 1);
                }
            }
        }
        return dp[n][m];
    }

    public static DistanceComparison compare(String s1, String s2) {
        int lev = computeLevenshtein(s1, s2);
        int dam = computeDamerauLevenshtein(s1, s2);
        return new DistanceComparison(s1, s2, lev, dam);
    }

    /**
     * Finds the closest entity name from a list of candidates using Damerau-Levenshtein distance.
     */
    public static String findClosestEntity(String query, List<String> candidates) {
        if (query == null || candidates == null || candidates.isEmpty()) {
            return null;
        }

        String bestMatch = null;
        int minDistance = Integer.MAX_VALUE;

        for (String candidate : candidates) {
            int d = computeDamerauLevenshtein(query, candidate);
            if (d < minDistance) {
                minDistance = d;
                bestMatch = candidate;
            }
        }
        return bestMatch;
    }
}
