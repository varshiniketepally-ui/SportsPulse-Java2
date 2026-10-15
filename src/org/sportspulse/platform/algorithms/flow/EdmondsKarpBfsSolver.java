package org.sportspulse.platform.algorithms.flow;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

/**
 * Edmonds-Karp Algorithm using BFS augmenting paths.
 * Guarantees shortest augmenting paths.
 * Time Complexity: O(V * E^2)
 */
public class EdmondsKarpBfsSolver {

    public static FordFulkersonDfsSolver.FlowResult solve(ResidualFlowNetwork network, int source, int sink) {
        network.resetFlows();
        long start = System.nanoTime();
        int maxFlow = 0;
        int paths = 0;
        long operations = 0;

        int n = network.getVertexCount();
        ResidualFlowNetwork.FlowEdge[] parentEdge = new ResidualFlowNetwork.FlowEdge[n];

        while (true) {
            Arrays.fill(parentEdge, null);
            Queue<Integer> q = new ArrayDeque<>();
            q.add(source);

            while (!q.isEmpty()) {
                int u = q.poll();
                operations++;
                if (u == sink) break;

                for (ResidualFlowNetwork.FlowEdge edge : network.getAdjacentEdges(u)) {
                    if (edge.to != source && parentEdge[edge.to] == null && edge.remainingCapacity() > 0) {
                        parentEdge[edge.to] = edge;
                        q.add(edge.to);
                    }
                }
            }

            if (parentEdge[sink] == null) {
                break; // No augmenting path
            }

            // Find bottleneck capacity along path
            int push = Integer.MAX_VALUE;
            for (int curr = sink; curr != source; curr = parentEdge[curr].from) {
                push = Math.min(push, parentEdge[curr].remainingCapacity());
            }

            // Augment along path
            for (int curr = sink; curr != source; curr = parentEdge[curr].from) {
                parentEdge[curr].augment(push);
            }

            maxFlow += push;
            paths++;
        }

        long time = System.nanoTime() - start;
        return new FordFulkersonDfsSolver.FlowResult("Edmonds-Karp (BFS)", maxFlow, time, paths, operations);
    }
}
