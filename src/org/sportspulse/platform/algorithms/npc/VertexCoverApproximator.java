package org.sportspulse.platform.algorithms.npc;

import java.util.*;

/**
 * Vertex Cover 2-Approximation Algorithm vs Exact Optimal Solver.
 * Applied to Player Conflict Coverage in sports rosters.
 *
 * Approximation: Repeatedly picks an uncovered edge (u, v) and adds BOTH endpoints to the cover.
 * Guarantees |C_approx| <= 2 * |C_opt|.
 * Exact: Branch-and-bound recursive search for minimum vertex cover.
 */
public class VertexCoverApproximator {

    public static class Edge {
        public final String u;
        public final String v;

        public Edge(String u, String v) {
            this.u = u;
            this.v = v;
        }

        @Override
        public String toString() {
            return "(" + u + " <-> " + v + ")";
        }
    }

    public static class ConflictCoverComparison {
        public final Set<String> approximateCover;
        public final Set<String> optimalCover;
        public final int approxSize;
        public final int optimalSize;
        public final double approximationRatio;
        public final long approxTimeNs;
        public final long optimalTimeNs;
        public final List<Edge> conflictEdges;

        public ConflictCoverComparison(Set<String> approx, Set<String> optimal,
                                       long approxTime, long optTime, List<Edge> edges) {
            this.approximateCover = approx;
            this.optimalCover = optimal;
            this.approxSize = approx.size();
            this.optimalSize = optimal.size();
            this.approximationRatio = optimalSize > 0 ? (double) approxSize / optimalSize : 1.0;
            this.approxTimeNs = approxTime;
            this.optimalTimeNs = optTime;
            this.conflictEdges = edges;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== PLAYER CONFLICT VERTEX COVER ANALYSIS ===\n");
            sb.append(String.format("Total Conflict Edges: %d\n\n", conflictEdges.size()));
            sb.append(String.format("2-Approximation Cover (|C_approx| = %d, Time: %.3f µs):\n  %s\n\n",
                    approxSize, approxTimeNs / 1000.0, approximateCover));
            sb.append(String.format("Exact Optimal Cover  (|C_opt|    = %d, Time: %.3f µs):\n  %s\n\n",
                    optimalSize, optimalTimeNs / 1000.0, optimalCover));
            sb.append(String.format("Approximation Ratio: %.2fx (Theoretical Upper Bound: <= 2.0x)\n", approximationRatio));
            return sb.toString();
        }
    }

    /**
     * Runs 2-Approximation using maximal matching.
     */
    public static Set<String> solve2Approximation(List<String> vertices, List<Edge> edges) {
        Set<String> cover = new LinkedHashSet<>();
        Set<Edge> remainingEdges = new HashSet<>(edges);

        for (Edge e : edges) {
            if (remainingEdges.contains(e)) {
                // Add both endpoints
                cover.add(e.u);
                cover.add(e.v);

                // Remove all incident edges
                remainingEdges.removeIf(edge -> edge.u.equals(e.u) || edge.v.equals(e.u) ||
                                                edge.u.equals(e.v) || edge.v.equals(e.v));
            }
        }
        return cover;
    }

    /**
     * Solves exact minimum vertex cover using branch-and-bound.
     */
    public static Set<String> solveExactOptimal(List<String> vertices, List<Edge> edges) {
        Set<String> bestCover = new HashSet<>(vertices);
        branchAndBound(new HashSet<>(edges), new HashSet<>(), bestCover);
        return bestCover;
    }

    private static void branchAndBound(Set<Edge> remainingEdges, Set<String> currentCover, Set<String> bestCover) {
        if (currentCover.size() >= bestCover.size()) {
            return; // Prune branch if already worse than best found
        }
        if (remainingEdges.isEmpty()) {
            bestCover.clear();
            bestCover.addAll(currentCover);
            return;
        }

        // Pick edge with max incident degree to branch efficiently
        Edge e = remainingEdges.iterator().next();

        // Branch 1: Include u in cover
        Set<String> coverWithU = new HashSet<>(currentCover);
        coverWithU.add(e.u);
        Set<Edge> edgesWithoutU = new HashSet<>(remainingEdges);
        edgesWithoutU.removeIf(edge -> edge.u.equals(e.u) || edge.v.equals(e.u));
        branchAndBound(edgesWithoutU, coverWithU, bestCover);

        // Branch 2: Include v in cover
        Set<String> coverWithV = new HashSet<>(currentCover);
        coverWithV.add(e.v);
        Set<Edge> edgesWithoutV = new HashSet<>(remainingEdges);
        edgesWithoutV.removeIf(edge -> edge.u.equals(e.v) || edge.v.equals(e.v));
        branchAndBound(edgesWithoutV, coverWithV, bestCover);
    }

    public static ConflictCoverComparison compare(List<String> vertices, List<Edge> edges) {
        long t1 = System.nanoTime();
        Set<String> approx = solve2Approximation(vertices, edges);
        long approxTime = System.nanoTime() - t1;

        long t2 = System.nanoTime();
        Set<String> opt = solveExactOptimal(vertices, edges);
        long optTime = System.nanoTime() - t2;

        return new ConflictCoverComparison(approx, opt, approxTime, optTime, edges);
    }
}
