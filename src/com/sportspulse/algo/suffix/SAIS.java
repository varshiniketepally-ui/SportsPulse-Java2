package com.sportspulse.algo.suffix;

import java.util.Arrays;

/**
 * SA-IS (Suffix Array by Induced Sorting) Algorithm.
 * Linear-time O(N) Suffix Array construction algorithm by G. Nong, S. Zhang, and W. H. Chan (2009).
 * Guarantees O(N) worst-case time complexity and O(N) workspace.
 * Used in SportsPulse for ultra-fast indexing of match dossiers, player scouting reports, and commentary archives.
 */
public final class SAIS {

    private final String text;
    private final int[] sa;
    private final long constructionTimeNs;

    public SAIS(String text) {
        this.text = text != null ? text : "";
        long start = System.nanoTime();
        this.sa = buildSaIs(this.text);
        this.constructionTimeNs = System.nanoTime() - start;
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

    private static int[] buildSaIs(String s) {
        int n = s.length();
        if (n == 0) return new int[0];
        if (n == 1) return new int[]{0};

        // Convert string to integer array with 0 as unique sentinel at the end
        int[] sInt = new int[n + 1];
        int maxChar = 0;
        for (int i = 0; i < n; i++) {
            sInt[i] = s.charAt(i) + 1; // shift so sentinel 0 is strictly smaller
            if (sInt[i] > maxChar) maxChar = sInt[i];
        }
        sInt[n] = 0; // Sentinel

        int[] fullSa = sais(sInt, maxChar + 1);

        // Strip sentinel (which is always fullSa[0] = n)
        int[] result = new int[n];
        System.arraycopy(fullSa, 1, result, 0, n);
        return result;
    }

    private static int[] sais(int[] s, int alphabetSize) {
        int n = s.length;
        if (n == 1) return new int[]{0};

        // Type array: true = S-type, false = L-type
        boolean[] isS = new boolean[n];
        isS[n - 1] = true;
        for (int i = n - 2; i >= 0; i--) {
            if (s[i] < s[i + 1]) {
                isS[i] = true;
            } else if (s[i] > s[i + 1]) {
                isS[i] = false;
            } else {
                isS[i] = isS[i + 1];
            }
        }

        // Count bucket sizes
        int[] bucketCounts = new int[alphabetSize];
        for (int c : s) {
            bucketCounts[c]++;
        }

        // Find LMS characters
        int[] lmsMap = new int[n];
        Arrays.fill(lmsMap, -1);
        int m = 0;
        for (int i = 1; i < n; i++) {
            if (isS[i] && !isS[i - 1]) {
                lmsMap[i] = m++;
            }
        }

        int[] lms = new int[m];
        int idx = 0;
        for (int i = 1; i < n; i++) {
            if (isS[i] && !isS[i - 1]) {
                lms[idx++] = i;
            }
        }

        int[] sa = new int[n];
        Arrays.fill(sa, -1);

        // Step 1: Induced sort LMS into SA to sort LMS substrings
        int[] bucketTails = getBucketTails(bucketCounts, alphabetSize);
        for (int i = 0; i < m; i++) {
            int p = lms[i];
            sa[bucketTails[s[p]]--] = p;
        }

        induceSortL(sa, s, isS, bucketCounts, alphabetSize);
        induceSortS(sa, s, isS, bucketCounts, alphabetSize);

        // Step 2: Name sorted LMS substrings
        int[] sortedLms = new int[m];
        int lmsIdx = 0;
        for (int x : sa) {
            if (x > 0 && lmsMap[x] >= 0) {
                sortedLms[lmsIdx++] = x;
            }
        }

        int[] s1 = new int[m];
        int name = 0;
        s1[lmsMap[sortedLms[0]]] = 0;

        for (int i = 1; i < m; i++) {
            int prev = sortedLms[i - 1];
            int curr = sortedLms[i];
            boolean diff = false;

            for (int d = 0; ; d++) {
                if (s[prev + d] != s[curr + d] || isS[prev + d] != isS[curr + d]) {
                    diff = true;
                    break;
                } else if (d > 0 && (lmsMap[prev + d] >= 0 || lmsMap[curr + d] >= 0)) {
                    break;
                }
            }

            if (diff) name++;
            s1[lmsMap[curr]] = name;
        }

        // Step 3: Recurse or construct SA1
        int[] sa1;
        if (name + 1 < m) {
            sa1 = sais(s1, name + 1);
        } else {
            sa1 = new int[m];
            for (int i = 0; i < m; i++) {
                sa1[s1[i]] = i;
            }
        }

        // Step 4: Induce sort full SA from correct SA1
        Arrays.fill(sa, -1);
        bucketTails = getBucketTails(bucketCounts, alphabetSize);
        for (int i = m - 1; i >= 0; i--) {
            int p = lms[sa1[i]];
            sa[bucketTails[s[p]]--] = p;
        }

        induceSortL(sa, s, isS, bucketCounts, alphabetSize);
        induceSortS(sa, s, isS, bucketCounts, alphabetSize);

        return sa;
    }

    private static void induceSortL(int[] sa, int[] s, boolean[] isS, int[] bucketCounts, int alphabetSize) {
        int[] bucketHeads = getBucketHeads(bucketCounts, alphabetSize);
        for (int i = 0; i < sa.length; i++) {
            int j = sa[i] - 1;
            if (j >= 0 && !isS[j]) {
                sa[bucketHeads[s[j]]++] = j;
            }
        }
    }

    private static void induceSortS(int[] sa, int[] s, boolean[] isS, int[] bucketCounts, int alphabetSize) {
        int[] bucketTails = getBucketTails(bucketCounts, alphabetSize);
        for (int i = sa.length - 1; i >= 0; i--) {
            int j = sa[i] - 1;
            if (j >= 0 && isS[j]) {
                sa[bucketTails[s[j]]--] = j;
            }
        }
    }

    private static int[] getBucketHeads(int[] bucketCounts, int alphabetSize) {
        int[] heads = new int[alphabetSize];
        int sum = 0;
        for (int i = 0; i < alphabetSize; i++) {
            heads[i] = sum;
            sum += bucketCounts[i];
        }
        return heads;
    }

    private static int[] getBucketTails(int[] bucketCounts, int alphabetSize) {
        int[] tails = new int[alphabetSize];
        int sum = 0;
        for (int i = 0; i < alphabetSize; i++) {
            sum += bucketCounts[i];
            tails[i] = sum - 1;
        }
        return tails;
    }
}
