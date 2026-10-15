package org.sportspulse.platform.algorithms.strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Rabin-Karp Rolling Hash String Search Engine.
 * Uses a polynomial rolling hash with a large prime modulus.
 * Correctly detects and reports spurious hash collisions.
 * Average Time Complexity: O(N + M).
 * Worst-case Time Complexity: O(N * M) (pathological hash collisions).
 * Space Complexity: O(1) auxiliary.
 */
public class RabinKarpRollingHasher {

    private static final int BASE = 256;
    private static final long MODULUS = 1_000_000_007L;

    public static StringSearchResult search(String text, String pattern) {
        long start = System.nanoTime();
        List<Integer> matches = new ArrayList<>();
        long comparisons = 0;
        int spuriousHits = 0;

        if (text == null || pattern == null || pattern.isEmpty() || pattern.length() > text.length()) {
            long time = System.nanoTime() - start;
            return new StringSearchResult("Rabin-Karp Rolling Hash", pattern, text != null ? text.length() : 0, matches, comparisons, time, 0);
        }

        int n = text.length();
        int m = pattern.length();

        // Calculate h = BASE^(m-1) % MODULUS
        long h = 1;
        for (int i = 0; i < m - 1; i++) {
            h = (h * BASE) % MODULUS;
        }

        // Calculate initial hash values
        long pHash = 0;
        long tHash = 0;
        for (int i = 0; i < m; i++) {
            pHash = (BASE * pHash + pattern.charAt(i)) % MODULUS;
            tHash = (BASE * tHash + text.charAt(i)) % MODULUS;
        }

        // Slide the pattern over text
        for (int i = 0; i <= n - m; i++) {
            if (pHash == tHash) {
                // Potential match: perform exact verification
                boolean exactMatch = true;
                for (int j = 0; j < m; j++) {
                    comparisons++;
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        exactMatch = false;
                        spuriousHits++;
                        break;
                    }
                }
                if (exactMatch) {
                    matches.add(i);
                }
            }

            // Compute hash for next window
            if (i < n - m) {
                tHash = (BASE * (tHash - text.charAt(i) * h) + text.charAt(i + m)) % MODULUS;
                if (tHash < 0) {
                    tHash += MODULUS;
                }
            }
        }

        long time = System.nanoTime() - start;
        return new StringSearchResult("Rabin-Karp Rolling Hash", pattern, n, matches, comparisons, time, spuriousHits);
    }
}
