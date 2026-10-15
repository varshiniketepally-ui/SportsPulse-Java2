package org.sportspulse.platform.algorithms.npc;

import java.util.*;

/**
 * Academic Karp Reduction from 3-SAT to CLIQUE.
 * Maps a 3-CNF formula with K clauses into a graph with 3K vertices.
 * Formula is Satisfiable <===> Reduction Graph has a Clique of size K.
 */
public class ThreeSatToCliqueReduction {

    public static class TripletVertex {
        public final int id;
        public final int clauseIndex;
        public final int literalIndex;
        public final BooleanSatSolver.Literal literal;

        public TripletVertex(int id, int clauseIndex, int literalIndex, BooleanSatSolver.Literal literal) {
            this.id = id;
            this.clauseIndex = clauseIndex;
            this.literalIndex = literalIndex;
            this.literal = literal;
        }

        @Override
        public String toString() {
            return String.format("v%d[C%d:%s]", id, clauseIndex + 1, literal);
        }
    }

    public static class ReductionGraph {
        public final List<TripletVertex> vertices;
        public final boolean[][] adjMatrix;
        public final int targetCliqueSize;

        public ReductionGraph(List<TripletVertex> vertices, boolean[][] adjMatrix, int targetSize) {
            this.vertices = vertices;
            this.adjMatrix = adjMatrix;
            this.targetCliqueSize = targetSize;
        }

        public int vertexCount() {
            return vertices.size();
        }

        public int edgeCount() {
            int count = 0;
            int n = vertices.size();
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (adjMatrix[i][j]) count++;
                }
            }
            return count;
        }
    }

    public static class ReductionResult {
        public final List<BooleanSatSolver.Clause> clauses;
        public final ReductionGraph graph;
        public final int targetCliqueSize;
        public final List<TripletVertex> foundClique;
        public final boolean satisfiable;

        public ReductionResult(List<BooleanSatSolver.Clause> clauses, ReductionGraph graph,
                               int targetCliqueSize, List<TripletVertex> foundClique) {
            this.clauses = clauses;
            this.graph = graph;
            this.targetCliqueSize = targetCliqueSize;
            this.foundClique = foundClique;
            this.satisfiable = foundClique.size() >= targetCliqueSize;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== 3-SAT -> CLIQUE REDUCTION ===\n");
            sb.append("3-SAT Clauses (K = ").append(clauses.size()).append("):\n");
            for (int i = 0; i < clauses.size(); i++) {
                sb.append(String.format("  C%d: %s\n", i + 1, clauses.get(i)));
            }
            sb.append(String.format("\nGenerated Graph: %d vertices, %d edges\n", graph.vertexCount(), graph.edgeCount()));
            sb.append("Target Clique Size: ").append(targetCliqueSize).append("\n");
            sb.append("Satisfiable via Clique: ").append(satisfiable ? "YES [FOUND CLIQUE]" : "NO").append("\n");
            if (satisfiable) {
                sb.append("Clique Vertices: ").append(foundClique).append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * Converts a 3-SAT formula to a reduction graph and searches for a K-clique.
     */
    public static ReductionResult reduceAndSolve(List<BooleanSatSolver.Clause> clauses) {
        int k = clauses.size();
        List<TripletVertex> vertices = new ArrayList<>();

        int vId = 0;
        for (int i = 0; i < k; i++) {
            BooleanSatSolver.Clause c = clauses.get(i);
            for (int r = 0; r < c.literals.size(); r++) {
                vertices.add(new TripletVertex(vId++, i, r, c.literals.get(r)));
            }
        }

        int n = vertices.size();
        boolean[][] adj = new boolean[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                TripletVertex u = vertices.get(i);
                TripletVertex v = vertices.get(j);

                // Different clauses and non-conflicting literals
                if (u.clauseIndex != v.clauseIndex) {
                    boolean isComplementary = u.literal.variable.equals(v.literal.variable) &&
                                              u.literal.sign != v.literal.sign;
                    if (!isComplementary) {
                        adj[i][j] = true;
                        adj[j][i] = true;
                    }
                }
            }
        }

        ReductionGraph graph = new ReductionGraph(vertices, adj, k);

        // Find Maximum Clique using branch-and-bound / backtracking
        List<Integer> bestClique = new ArrayList<>();
        findClique(graph, 0, new ArrayList<>(), bestClique, k);

        List<TripletVertex> cliqueVertices = new ArrayList<>();
        for (int idx : bestClique) {
            cliqueVertices.add(vertices.get(idx));
        }

        return new ReductionResult(clauses, graph, k, cliqueVertices);
    }

    private static void findClique(ReductionGraph g, int start, List<Integer> current, List<Integer> best, int target) {
        if (current.size() > best.size()) {
            best.clear();
            best.addAll(current);
            if (best.size() == target) return;
        }

        int n = g.vertexCount();
        if (current.size() + (n - start) <= best.size()) {
            return; // Pruning
        }

        for (int i = start; i < n; i++) {
            boolean canAdd = true;
            for (int u : current) {
                if (!g.adjMatrix[u][i]) {
                    canAdd = false;
                    break;
                }
            }
            if (canAdd) {
                current.add(i);
                findClique(g, i + 1, current, best, target);
                current.remove(current.size() - 1);
                if (best.size() >= target) return;
            }
        }
    }
}
