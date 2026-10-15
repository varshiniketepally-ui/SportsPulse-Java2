package org.sportspulse.platform.algorithms.suffix;

import java.util.*;

/**
 * Suffix Automaton (Directed Acyclic Word Graph - DAWG).
 * The smallest automaton that recognizes all suffixes of a string.
 * Constructed in linear time O(N) and linear space O(N).
 * Maximum 2N - 1 states and 3N - 4 transitions.
 * Perfect for repeated-substring analysis and distinct substring counting in sports texts.
 */
public class DirectedAcyclicWordGraph {

    public static class State {
        public int len;
        public int link;
        public Map<Character, Integer> next = new HashMap<>();
        public boolean isClone = false;
        public int firstEndPos = -1;

        public State(int len, int link) {
            this.len = len;
            this.link = link;
        }
    }

    private final String text;
    private final List<State> states = new ArrayList<>();
    private int last;

    public DirectedAcyclicWordGraph(String text) {
        this.text = text != null ? text : "";
        initAutomaton();
        for (int i = 0; i < this.text.length(); i++) {
            extend(this.text.charAt(i), i);
        }
    }

    private void initAutomaton() {
        states.clear();
        states.add(new State(0, -1)); // Root state 0
        last = 0;
    }

    private void extend(char c, int pos) {
        int cur = states.size();
        State curState = new State(states.get(last).len + 1, 0);
        curState.firstEndPos = pos;
        states.add(curState);

        int p = last;
        while (p != -1 && !states.get(p).next.containsKey(c)) {
            states.get(p).next.put(c, cur);
            p = states.get(p).link;
        }

        if (p == -1) {
            curState.link = 0;
        } else {
            int q = states.get(p).next.get(c);
            State qState = states.get(q);

            if (states.get(p).len + 1 == qState.len) {
                curState.link = q;
            } else {
                int clone = states.size();
                State cloneState = new State(states.get(p).len + 1, qState.link);
                cloneState.next = new HashMap<>(qState.next);
                cloneState.isClone = true;
                cloneState.firstEndPos = qState.firstEndPos;
                states.add(cloneState);

                while (p != -1 && states.get(p).next.get(c) == q) {
                    states.get(p).next.put(c, clone);
                    p = states.get(p).link;
                }

                qState.link = clone;
                curState.link = clone;
            }
        }
        last = cur;
    }

    public boolean containsSubstring(String query) {
        if (query == null || query.isEmpty()) return true;
        int curr = 0;
        for (int i = 0; i < query.length(); i++) {
            char c = query.charAt(i);
            if (!states.get(curr).next.containsKey(c)) {
                return false;
            }
            curr = states.get(curr).next.get(c);
        }
        return true;
    }

    /**
     * Counts the total number of distinct substrings in O(states) time.
     * Formula: sum(len[u] - len[link[u]]) for all states u > 0.
     */
    public long countDistinctSubstrings() {
        long total = 0;
        for (int i = 1; i < states.size(); i++) {
            total += (states.get(i).len - states.get(states.get(i).link).len);
        }
        return total;
    }

    /**
     * Finds the longest repeated substring in the text.
     */
    public String findLongestRepeatedSubstring() {
        int n = states.size();
        // Compute endpos occurrences for each state
        int[] occ = new int[n];
        // Topological sort states by len descending
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> Integer.compare(states.get(b).len, states.get(a).len));

        // Mark primary states
        for (int i = 0; i < n; i++) {
            if (!states.get(i).isClone && i != 0) {
                occ[i] = 1;
            }
        }

        // Propagate counts along suffix links
        for (int u : order) {
            if (u != 0 && states.get(u).link != -1) {
                occ[states.get(u).link] += occ[u];
            }
        }

        int maxLen = 0;
        int bestState = -1;
        for (int i = 1; i < n; i++) {
            if (occ[i] >= 2 && states.get(i).len > maxLen) {
                maxLen = states.get(i).len;
                bestState = i;
            }
        }

        if (bestState != -1 && maxLen > 0) {
            int endPos = states.get(bestState).firstEndPos;
            int startPos = endPos - maxLen + 1;
            return text.substring(startPos, endPos + 1);
        }

        return "";
    }

    public int getStateCount() {
        return states.size();
    }

    public String getText() {
        return text;
    }
}
