package org.sportspulse.platform.algorithms.suffix;

/**
 * Kasai's Algorithm for Longest Common Prefix (LCP) Array construction in O(N) time.
 * Identifies shared tactical phrases and common text between sports reports.
 */
public class KasaiLcpEvaluator {

    public static class CommonPhraseResult {
        private final String documentAName;
        private final String documentBName;
        private final String longestCommonPhrase;
        private final int lcpLength;
        private final int docAOffset;
        private final int docBOffset;

        public CommonPhraseResult(String documentAName, String documentBName,
                                  String longestCommonPhrase, int lcpLength,
                                  int docAOffset, int docBOffset) {
            this.documentAName = documentAName;
            this.documentBName = documentBName;
            this.longestCommonPhrase = longestCommonPhrase;
            this.lcpLength = lcpLength;
            this.docAOffset = docAOffset;
            this.docBOffset = docBOffset;
        }

        public String getDocumentAName() { return documentAName; }
        public String getDocumentBName() { return documentBName; }
        public String getLongestCommonPhrase() { return longestCommonPhrase; }
        public int getLcpLength() { return lcpLength; }
        public int getDocAOffset() { return docAOffset; }
        public int getDocBOffset() { return docBOffset; }

        @Override
        public String toString() {
            return String.format("Report A: %s\nReport B: %s\nLongest Common Phrase: \"%s\"\nLCP Length: %d characters (Offsets: %d, %d)",
                    documentAName, documentBName, longestCommonPhrase, lcpLength, docAOffset, docBOffset);
        }
    }

    /**
     * Constructs the LCP array using Kasai's Algorithm in O(N) time.
     * lcp[i] = length of LCP between suffixes sa[i-1] and sa[i]. (lcp[0] = 0).
     */
    public static int[] computeLCP(String text, int[] sa) {
        int n = text.length();
        if (n == 0 || sa == null || sa.length == 0) return new int[0];

        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[sa[i]] = i;
        }

        int[] lcp = new int[n];
        int h = 0;

        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = sa[rank[i] - 1];
                while (i + h < n && j + h < n && text.charAt(i + h) == text.charAt(j + h)) {
                    h++;
                }
                lcp[rank[i]] = h;
                if (h > 0) {
                    h--;
                }
            }
        }
        return lcp;
    }

    /**
     * Evaluates the longest common tactical phrase between two sports scouting reports.
     */
    public static CommonPhraseResult findLongestCommonPhrase(String docAName, String docA,
                                                             String docBName, String docB) {
        if (docA == null || docB == null || docA.isEmpty() || docB.isEmpty()) {
            return new CommonPhraseResult(docAName, docBName, "", 0, -1, -1);
        }

        char delim = '#';
        String combined = docA + delim + docB;
        int lenA = docA.length();

        StandardSuffixArray ssa = new StandardSuffixArray(combined);
        int[] sa = ssa.getSuffixArray();
        int[] lcp = computeLCP(combined, sa);

        int maxLcp = 0;
        int bestSaIdx = -1;

        for (int i = 1; i < combined.length(); i++) {
            int p1 = sa[i - 1];
            int p2 = sa[i];

            // Verify one suffix is from docA and one from docB
            boolean p1InA = p1 < lenA;
            boolean p2InA = p2 < lenA;

            if (p1InA != p2InA) {
                // Ensure the match doesn't cross the delimiter
                int candidateLen = Math.min(lcp[i], p1InA ? lenA - p1 : combined.length() - p1);
                candidateLen = Math.min(candidateLen, p2InA ? lenA - p2 : combined.length() - p2);

                if (candidateLen > maxLcp) {
                    maxLcp = candidateLen;
                    bestSaIdx = i;
                }
            }
        }

        if (maxLcp > 0 && bestSaIdx != -1) {
            int p1 = sa[bestSaIdx - 1];
            int p2 = sa[bestSaIdx];
            String commonPhrase = combined.substring(p1, p1 + maxLcp);
            int offsetA = p1 < lenA ? p1 : p2;
            int offsetB = p1 < lenA ? (p2 - lenA - 1) : (p1 - lenA - 1);
            return new CommonPhraseResult(docAName, docBName, commonPhrase, maxLcp, offsetA, offsetB);
        }

        return new CommonPhraseResult(docAName, docBName, "(No common phrase found)", 0, -1, -1);
    }
}
