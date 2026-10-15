package org.sportspulse.platform.algorithms.flow;

import java.util.*;

/**
 * König's Theorem Analyzer:
 * In any bipartite graph, the size of a Maximum Matching equals the size of a Minimum Vertex Cover.
 * (|M| = |V_cover|).
 * Derives the exact Minimum Vertex Cover using alternating BFS reachability:
 * V_cover = (L \ Z) U (R ∩ Z).
 */
public class KonigTheoremAnalyzer {

    public static class KonigResult {
        private final Map<String, String> maximumMatching;
        private final Set<String> minVertexCover;
        private final Set<String> coverLeft;
        private final Set<String> coverRight;
        private final int matchingSize;
        private final int vertexCoverSize;
        private final boolean theoremHolds;
        private final String explanation;

        public KonigResult(Map<String, String> matching, Set<String> coverLeft, Set<String> coverRight) {
            this.maximumMatching = matching;
            this.coverLeft = coverLeft;
            this.coverRight = coverRight;
            this.minVertexCover = new LinkedHashSet<>(coverLeft);
            this.minVertexCover.addAll(coverRight);
            this.matchingSize = matching.size();
            this.vertexCoverSize = minVertexCover.size();
            this.theoremHolds = (matchingSize == vertexCoverSize);
            this.explanation = String.format(
                    "König's Theorem states that in any bipartite graph, |Max Matching| == |Min Vertex Cover|.\n" +
                    "Here: |M| = %d and |MVC| = %d (Identity %s).\n" +
                    "The cover is constructed by alternating BFS from unmatched Left vertices: C = (L \\ Z) ∪ (R ∩ Z).",
                    matchingSize, vertexCoverSize, theoremHolds ? "HOLDS [VERIFIED]" : "VIOLATED");
        }

        public Map<String, String> getMaximumMatching() { return maximumMatching; }
        public Set<String> getMinVertexCover() { return minVertexCover; }
        public Set<String> getCoverLeft() { return coverLeft; }
        public Set<String> getCoverRight() { return coverRight; }
        public int getMatchingSize() { return matchingSize; }
        public int getVertexCoverSize() { return vertexCoverSize; }
        public boolean isTheoremHolds() { return theoremHolds; }
        public String getExplanation() { return explanation; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== KÖNIG'S THEOREM ANALYSIS ===\n");
            sb.append(explanation).append("\n\n");
            sb.append("Maximum Matching (|M| = ").append(matchingSize).append("):\n");
            maximumMatching.forEach((u, v) -> sb.append(String.format("   %s <---> %s\n", u, v)));
            sb.append("\nMinimum Vertex Cover (|MVC| = ").append(vertexCoverSize).append("):\n");
            sb.append("  From Left Set: ").append(coverLeft).append("\n");
            sb.append("  From Right Set: ").append(coverRight).append("\n");
            return sb.toString();
        }
    }

    public static KonigResult analyze(List<String> leftNodes, List<String> rightNodes,
                                      Map<String, List<String>> adjList) {
        int nL = leftNodes.size();
        int nR = rightNodes.size();

        Map<String, Integer> leftIdx = new HashMap<>();
        for (int i = 0; i < nL; i++) leftIdx.put(leftNodes.get(i), i);

        Map<String, Integer> rightIdx = new HashMap<>();
        for (int i = 0; i < nR; i++) rightIdx.put(rightNodes.get(i), i);

        List<List<Integer>> adj = new ArrayList<>(nL);
        for (int i = 0; i < nL; i++) {
            adj.add(new ArrayList<>());
            List<String> neighbors = adjList.getOrDefault(leftNodes.get(i), Collections.emptyList());
            for (String rName : neighbors) {
                if (rightIdx.containsKey(rName)) {
                    adj.get(i).add(rightIdx.get(rName));
                }
            }
        }

        // Compute Maximum Matching
        int[] matchR = new int[nR];
        Arrays.fill(matchR, -1);
        int[] matchL = new int[nL];
        Arrays.fill(matchL, -1);
        boolean[] visited = new boolean[nR];

        for (int u = 0; u < nL; u++) {
            Arrays.fill(visited, false);
            kuhn(u, adj, matchR, visited);
        }

        Map<String, String> matchingPairs = new LinkedHashMap<>();
        for (int r = 0; r < nR; r++) {
            if (matchR[r] != -1) {
                int l = matchR[r];
                matchL[l] = r;
                matchingPairs.put(leftNodes.get(l), rightNodes.get(r));
            }
        }

        // Find Z: alternating path reachability starting from unmatched Left nodes
        boolean[] visitedL = new boolean[nL];
        boolean[] visitedR = new boolean[nR];
        Queue<Integer> queueL = new ArrayDeque<>();

        for (int l = 0; l < nL; l++) {
            if (matchL[l] == -1) {
                visitedL[l] = true;
                queueL.add(l);
            }
        }

        while (!queueL.isEmpty()) {
            int u = queueL.poll();
            for (int v : adj.get(u)) {
                // Non-matching edge u -> v
                if (matchL[u] != v && !visitedR[v]) {
                    visitedR[v] = true;
                    // Matching edge v -> matchR[v]
                    if (matchR[v] != -1 && !visitedL[matchR[v]]) {
                        visitedL[matchR[v]] = true;
                        queueL.add(matchR[v]);
                    }
                }
            }
        }

        // Vertex Cover: C = (L \ Z) U (R ∩ Z)
        Set<String> coverL = new LinkedHashSet<>();
        Set<String> coverR = new LinkedHashSet<>();

        for (int l = 0; l < nL; l++) {
            if (!visitedL[l]) {
                coverL.add(leftNodes.get(l));
            }
        }

        for (int r = 0; r < nR; r++) {
            if (visitedR[r]) {
                coverR.add(rightNodes.get(r));
            }
        }

        return new KonigResult(matchingPairs, coverL, coverR);
    }

    private static boolean kuhn(int u, List<List<Integer>> adj, int[] matchR, boolean[] visited) {
        for (int v : adj.get(u)) {
            if (!visited[v]) {
                visited[v] = true;
                if (matchR[v] < 0 || kuhn(matchR[v], adj, matchR, visited)) {
                    matchR[v] = u;
                    return true;
                }
            }
        }
        return false;
    }
}
