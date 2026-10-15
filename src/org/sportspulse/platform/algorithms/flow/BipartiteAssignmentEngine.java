package org.sportspulse.platform.algorithms.flow;

import java.util.*;

/**
 * Bipartite Matching Engine for Sports Player-to-Position and Resource Assignment.
 * Implemented using augmenting path algorithm (Kuhn's algorithm / maximum flow).
 * Time Complexity: O(V * E)
 */
public class BipartiteAssignmentEngine {

    public static class AssignmentResult {
        private final Map<String, String> assignments; // Left -> Right
        private final List<String> unassignedLeft;
        private final List<String> unfilledRight;

        public AssignmentResult(Map<String, String> assignments, List<String> unassignedLeft, List<String> unfilledRight) {
            this.assignments = assignments;
            this.unassignedLeft = unassignedLeft;
            this.unfilledRight = unfilledRight;
        }

        public Map<String, String> getAssignments() { return assignments; }
        public List<String> getUnassignedLeft() { return unassignedLeft; }
        public List<String> getUnfilledRight() { return unfilledRight; }
        public int getMatchingSize() { return assignments.size(); }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Maximum Matching Size: ").append(assignments.size()).append("\n");
            assignments.forEach((k, v) -> sb.append(String.format("  * %s -> %s\n", k, v)));
            if (!unassignedLeft.isEmpty()) {
                sb.append("Unassigned: ").append(unassignedLeft).append("\n");
            }
            return sb.toString();
        }
    }

    public static AssignmentResult solve(List<String> leftEntities, List<String> rightEntities,
                                         Map<String, List<String>> eligibilityGraph) {
        int nL = leftEntities.size();
        int nR = rightEntities.size();

        Map<String, Integer> leftIndex = new HashMap<>();
        for (int i = 0; i < nL; i++) leftIndex.put(leftEntities.get(i), i);

        Map<String, Integer> rightIndex = new HashMap<>();
        for (int i = 0; i < nR; i++) rightIndex.put(rightEntities.get(i), i);

        List<List<Integer>> adj = new ArrayList<>(nL);
        for (int i = 0; i < nL; i++) {
            adj.add(new ArrayList<>());
            String uName = leftEntities.get(i);
            List<String> allowed = eligibilityGraph.getOrDefault(uName, Collections.emptyList());
            for (String vName : allowed) {
                if (rightIndex.containsKey(vName)) {
                    adj.get(i).add(rightIndex.get(vName));
                }
            }
        }

        int[] matchR = new int[nR];
        Arrays.fill(matchR, -1);
        boolean[] visited = new boolean[nR];

        for (int u = 0; u < nL; u++) {
            Arrays.fill(visited, false);
            kuhn(u, adj, matchR, visited);
        }

        Map<String, String> matchedPairs = new LinkedHashMap<>();
        int[] matchL = new int[nL];
        Arrays.fill(matchL, -1);

        for (int r = 0; r < nR; r++) {
            if (matchR[r] != -1) {
                int l = matchR[r];
                matchL[l] = r;
                matchedPairs.put(leftEntities.get(l), rightEntities.get(r));
            }
        }

        List<String> unassigned = new ArrayList<>();
        for (int l = 0; l < nL; l++) {
            if (matchL[l] == -1) {
                unassigned.add(leftEntities.get(l));
            }
        }

        List<String> unfilled = new ArrayList<>();
        for (int r = 0; r < nR; r++) {
            if (matchR[r] == -1) {
                unfilled.add(rightEntities.get(r));
            }
        }

        return new AssignmentResult(matchedPairs, unassigned, unfilled);
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
