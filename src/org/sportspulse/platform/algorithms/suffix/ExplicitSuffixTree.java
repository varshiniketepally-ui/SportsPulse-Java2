package org.sportspulse.platform.algorithms.suffix;

import java.util.*;

/**
 * Compressed Suffix Tree data structure.
 * Supports efficient substring queries and occurrence lookups in O(M + K) time.
 */
public class ExplicitSuffixTree {

    public static class Node {
        final Map<Character, Edge> edges = new TreeMap<>();
        int suffixIndex = -1; // Set for leaf nodes

        boolean isLeaf() {
            return suffixIndex != -1;
        }
    }

    public static class Edge {
        final int start;
        int end; // Exclusive
        final Node target;

        public Edge(int start, int end, Node target) {
            this.start = start;
            this.end = end;
            this.target = target;
        }

        public int length() {
            return end - start;
        }
    }

    private final String text;
    private final Node root;

    public ExplicitSuffixTree(String text) {
        this.text = text != null ? text : "";
        this.root = new Node();
        buildTree();
    }

    private void buildTree() {
        int n = text.length();
        for (int i = 0; i < n; i++) {
            insertSuffix(i);
        }
    }

    private void insertSuffix(int suffixStart) {
        Node curr = root;
        int i = suffixStart;
        int n = text.length();

        while (i < n) {
            char ch = text.charAt(i);
            Edge edge = curr.edges.get(ch);

            if (edge == null) {
                // Create new leaf edge
                Node leaf = new Node();
                leaf.suffixIndex = suffixStart;
                curr.edges.put(ch, new Edge(i, n, leaf));
                return;
            }

            // Traverse existing edge
            int edgeLen = edge.length();
            int matchLen = 0;

            while (matchLen < edgeLen && i + matchLen < n &&
                    text.charAt(edge.start + matchLen) == text.charAt(i + matchLen)) {
                matchLen++;
            }

            if (matchLen == edgeLen) {
                // Fully matched this edge, continue at target node
                curr = edge.target;
                i += edgeLen;
            } else {
                // Partial match: split edge
                Node split = new Node();
                Edge newSplitEdge = new Edge(edge.start, edge.start + matchLen, split);

                // Re-wire old edge target to branch off split node
                char oldChar = text.charAt(edge.start + matchLen);
                split.edges.put(oldChar, new Edge(edge.start + matchLen, edge.end, edge.target));

                // Add new leaf branching off split node
                if (i + matchLen < n) {
                    char newChar = text.charAt(i + matchLen);
                    Node leaf = new Node();
                    leaf.suffixIndex = suffixStart;
                    split.edges.put(newChar, new Edge(i + matchLen, n, leaf));
                } else {
                    split.suffixIndex = suffixStart;
                }

                curr.edges.put(ch, newSplitEdge);
                return;
            }
        }

        curr.suffixIndex = suffixStart;
    }

    public boolean containsSubstring(String query) {
        return !findOccurrences(query).isEmpty();
    }

    public List<Integer> findOccurrences(String query) {
        List<Integer> occurrences = new ArrayList<>();
        if (query == null || query.isEmpty() || text.isEmpty()) {
            return occurrences;
        }

        Node curr = root;
        int i = 0;
        int m = query.length();

        while (i < m) {
            char ch = query.charAt(i);
            Edge edge = curr.edges.get(ch);
            if (edge == null) {
                return occurrences; // No match
            }

            int edgeLen = edge.length();
            int k = 0;
            while (k < edgeLen && i < m) {
                if (text.charAt(edge.start + k) != query.charAt(i)) {
                    return occurrences; // Mismatch on edge
                }
                k++;
                i++;
            }

            if (i < m) {
                curr = edge.target;
            } else {
                // Pattern fully matched, collect all leaves in subtree
                collectLeafIndices(edge.target, occurrences);
                return occurrences;
            }
        }

        collectLeafIndices(curr, occurrences);
        return occurrences;
    }

    private void collectLeafIndices(Node node, List<Integer> occurrences) {
        if (node.isLeaf()) {
            occurrences.add(node.suffixIndex);
        }
        for (Edge edge : node.edges.values()) {
            collectLeafIndices(edge.target, occurrences);
        }
    }

    public String getText() {
        return text;
    }
}
