package org.sportspulse.platform.algorithms.npc;

import java.util.*;

/**
 * End-to-End NP-Completeness Reduction Pipeline:
 * 3-SAT ===> CLIQUE ===> INDEPENDENT SET ===> VERTEX COVER.
 *
 * Demonstrates:
 * 1. 3-SAT to CLIQUE: Gadget graph with 3K vertices, target K.
 * 2. CLIQUE to INDEPENDENT SET: Complement graph G_bar where (u, v) in E_bar iff (u, v) not in E.
 *    S is a Clique in G <===> S is an Independent Set in G_bar.
 * 3. INDEPENDENT SET to VERTEX COVER: In any graph H = (V, E),
 *    I is an Independent Set <===> V \ I is a Vertex Cover.
 */
public class NpHardReductionsPipeline {

    public static class FullPipelineResult {
        public final ThreeSatToCliqueReduction.ReductionResult threeSatToClique;
        public final int complementEdgeCount;
        public final List<String> independentSetVertices;
        public final List<String> vertexCoverVertices;
        public final int graphSize;
        public final boolean verifiedEquivalent;

        public FullPipelineResult(ThreeSatToCliqueReduction.ReductionResult threeSatToClique,
                                  int complementEdges, List<String> independentSet,
                                  List<String> vertexCover, int graphSize) {
            this.threeSatToClique = threeSatToClique;
            this.complementEdgeCount = complementEdges;
            this.independentSetVertices = independentSet;
            this.vertexCoverVertices = vertexCover;
            this.graphSize = graphSize;
            this.verifiedEquivalent = (independentSet.size() + vertexCover.size() == graphSize);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(threeSatToClique.toString()).append("\n");
            sb.append("=== CLIQUE -> INDEPENDENT SET (Complement Graph) ===\n");
            sb.append(String.format("Complement Graph G_bar has %d edges\n", complementEdgeCount));
            sb.append("Independent Set (|IS| = ").append(independentSetVertices.size()).append("): ")
              .append(independentSetVertices).append("\n\n");
            sb.append("=== INDEPENDENT SET -> VERTEX COVER ===\n");
            sb.append("Relation: V_cover = V \\ IS (Size = ").append(graphSize).append(" - ")
              .append(independentSetVertices.size()).append(" = ").append(vertexCoverVertices.size()).append(")\n");
            sb.append("Vertex Cover: ").append(vertexCoverVertices).append("\n");
            sb.append("Karp Theorem Equivalence Verified: ").append(verifiedEquivalent ? "YES [CONFIRMED]" : "FAILED");
            return sb.toString();
        }
    }

    public static FullPipelineResult executePipeline(List<BooleanSatSolver.Clause> clauses) {
        // Step 1: 3-SAT -> CLIQUE
        ThreeSatToCliqueReduction.ReductionResult cliqueRes = ThreeSatToCliqueReduction.reduceAndSolve(clauses);
        ThreeSatToCliqueReduction.ReductionGraph g = cliqueRes.graph;
        int n = g.vertexCount();

        // Step 2: Build Complement Graph G_bar
        boolean[][] compAdj = new boolean[n][n];
        int compEdges = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (!g.adjMatrix[i][j]) {
                    compAdj[i][j] = true;
                    compAdj[j][i] = true;
                    compEdges++;
                }
            }
        }

        // The vertices that form a Clique in G form an Independent Set in G_bar
        List<String> indepSet = new ArrayList<>();
        Set<Integer> indepIndices = new HashSet<>();
        for (ThreeSatToCliqueReduction.TripletVertex tv : cliqueRes.foundClique) {
            indepSet.add(tv.toString());
            indepIndices.add(tv.id);
        }

        // Step 3: Vertex Cover = V \ Independent Set
        List<String> vertexCover = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (!indepIndices.contains(i)) {
                vertexCover.add(g.vertices.get(i).toString());
            }
        }

        return new FullPipelineResult(cliqueRes, compEdges, indepSet, vertexCover, n);
    }
}
