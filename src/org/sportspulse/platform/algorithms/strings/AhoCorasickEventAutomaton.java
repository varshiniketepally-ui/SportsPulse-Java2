package org.sportspulse.platform.algorithms.strings;

import java.util.*;

/**
 * Aho-Corasick Multi-Keyword Pattern Matching Automaton.
 * Constructing the Trie + Failure Links via BFS.
 * Scans continuous text streams in O(N + total matches) time.
 * Perfect for Live Sports Match Event detection across commentary streams.
 */
public class AhoCorasickEventAutomaton {

    public static class MatchHit {
        private final String keyword;
        private final int startIndex;
        private final int endIndex;
        private final String surroundingSnippet;

        public MatchHit(String keyword, int startIndex, int endIndex, String surroundingSnippet) {
            this.keyword = keyword;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.surroundingSnippet = surroundingSnippet;
        }

        public String getKeyword() { return keyword; }
        public int getStartIndex() { return startIndex; }
        public int getEndIndex() { return endIndex; }
        public String getSurroundingSnippet() { return surroundingSnippet; }

        @Override
        public String toString() {
            return String.format("Found '%s' at [%d..%d]: \"%s\"",
                    keyword, startIndex, endIndex, surroundingSnippet);
        }
    }

    private static class Node {
        final Map<Character, Node> next = new HashMap<>();
        Node fail;
        Node dict; // Dictionary suffix link for fast emission
        final List<String> outputs = new ArrayList<>();
    }

    private final Node root;
    private boolean compiled;
    private final List<String> keywords;

    public AhoCorasickEventAutomaton() {
        this.root = new Node();
        this.compiled = false;
        this.keywords = new ArrayList<>();
    }

    public AhoCorasickEventAutomaton(List<String> keywords) {
        this();
        for (String kw : keywords) {
            addPattern(kw);
        }
        buildAutomaton();
    }

    public void addPattern(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return;
        String kw = keyword.toLowerCase().trim();
        keywords.add(kw);
        compiled = false;

        Node curr = root;
        for (int i = 0; i < kw.length(); i++) {
            char c = kw.charAt(i);
            curr = curr.next.computeIfAbsent(c, k -> new Node());
        }
        curr.outputs.add(kw);
    }

    public void buildAutomaton() {
        Queue<Node> queue = new ArrayDeque<>();

        // Level 1 nodes fail to root
        for (Node child : root.next.values()) {
            child.fail = root;
            queue.add(child);
        }

        while (!queue.isEmpty()) {
            Node curr = queue.poll();

            for (Map.Entry<Character, Node> entry : curr.next.entrySet()) {
                char ch = entry.getKey();
                Node child = entry.getValue();

                // Find failure state for child
                Node f = curr.fail;
                while (f != null && !f.next.containsKey(ch)) {
                    f = f.fail;
                }
                child.fail = (f != null) ? f.next.get(ch) : root;

                // Dict link: nearest ancestor with non-empty outputs
                if (child.fail != null) {
                    if (!child.fail.outputs.isEmpty()) {
                        child.dict = child.fail;
                    } else {
                        child.dict = child.fail.dict;
                    }
                }

                queue.add(child);
            }
        }
        compiled = true;
    }

    /**
     * Scans text stream and reports all matching keywords and their context.
     */
    public List<MatchHit> search(String text) {
        if (!compiled) {
            buildAutomaton();
        }

        List<MatchHit> results = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return results;
        }

        String lowerText = text.toLowerCase();
        Node curr = root;

        for (int i = 0; i < lowerText.length(); i++) {
            char c = lowerText.charAt(i);

            // Follow failure transitions
            while (curr != root && !curr.next.containsKey(c)) {
                curr = curr.fail;
            }

            if (curr.next.containsKey(c)) {
                curr = curr.next.get(c);
            } else {
                curr = root;
            }

            // Emit direct outputs
            for (String kw : curr.outputs) {
                int start = i - kw.length() + 1;
                results.add(new MatchHit(kw, start, i, extractSnippet(text, start, i)));
            }

            // Emit dictionary link outputs
            Node d = curr.dict;
            while (d != null) {
                for (String kw : d.outputs) {
                    int start = i - kw.length() + 1;
                    results.add(new MatchHit(kw, start, i, extractSnippet(text, start, i)));
                }
                d = d.dict;
            }
        }

        return results;
    }

    private String extractSnippet(String text, int start, int end) {
        int window = 20;
        int left = Math.max(0, start - window);
        int right = Math.min(text.length(), end + 1 + window);
        String prefix = left > 0 ? "..." : "";
        String suffix = right < text.length() ? "..." : "";
        return prefix + text.substring(left, right).replace('\n', ' ') + suffix;
    }

    public List<String> getKeywords() {
        return Collections.unmodifiableList(keywords);
    }
}
