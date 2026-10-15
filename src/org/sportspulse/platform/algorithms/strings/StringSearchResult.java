package org.sportspulse.platform.algorithms.strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates diagnostic and statistical results of a string search execution.
 */
public class StringSearchResult {

    private final String algorithmName;
    private final String pattern;
    private final int textLength;
    private final List<Integer> matchIndices;
    private final long comparisons;
    private final long executionTimeNs;
    private final int spuriousHits; // Relevant for Rabin-Karp

    public StringSearchResult(String algorithmName, String pattern, int textLength,
                              List<Integer> matchIndices, long comparisons,
                              long executionTimeNs, int spuriousHits) {
        this.algorithmName = algorithmName;
        this.pattern = pattern;
        this.textLength = textLength;
        this.matchIndices = matchIndices != null ? matchIndices : new ArrayList<>();
        this.comparisons = comparisons;
        this.executionTimeNs = executionTimeNs;
        this.spuriousHits = spuriousHits;
    }

    public String getAlgorithmName() { return algorithmName; }
    public String getPattern() { return pattern; }
    public int getTextLength() { return textLength; }
    public List<Integer> getMatchIndices() { return matchIndices; }
    public int getMatchCount() { return matchIndices.size(); }
    public long getComparisons() { return comparisons; }
    public long getExecutionTimeNs() { return executionTimeNs; }
    public int getSpuriousHits() { return spuriousHits; }

    @Override
    public String toString() {
        return String.format("[%s] Pattern: '%s' | Text Len: %d | Matches: %d %s | Comparisons: %d | Time: %.3f µs%s",
                algorithmName, pattern, textLength, getMatchCount(), matchIndices,
                comparisons, executionTimeNs / 1000.0,
                spuriousHits > 0 ? " | Hash Collisions: " + spuriousHits : "");
    }
}
