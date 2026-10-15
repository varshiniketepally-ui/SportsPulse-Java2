package org.sportspulse.platform.algorithms.flow;

import java.util.List;

/**
 * Ford-Fulkerson Algorithm using DFS augmenting paths.
 * Time Complexity: O(E * max_flow)
 */
public class FordFulkersonDfsSolver {

    public static class FlowResult {
        public final String algorithm;
        public final int maxFlow;
        public final long executionTimeNs;
        public final int augmentingPaths;
        public final long operations;

        public FlowResult(String algorithm, int maxFlow, long time, int paths, long ops) {
            this.algorithm = algorithm;
            this.maxFlow = maxFlow;
            this.executionTimeNs = time;
            this.augmentingPaths = paths;
            this.operations = ops;
        }

        @Override
        public String toString() {
            return String.format("[%s] Max Flow: %d | Time: %.3f µs | Paths: %d | Ops: %d",
                    algorithm, maxFlow, executionTimeNs / 1000.0, augmentingPaths, operations);
        }
    }

    public static FlowResult solve(ResidualFlowNetwork network, int source, int sink) {
        network.resetFlows();
        long start = System.nanoTime();
        int maxFlow = 0;
        int paths = 0;
        long operations = 0;

        int n = network.getVertexCount();
        boolean[] visited = new boolean[n];

        while (true) {
            java.util.Arrays.fill(visited, false);
            int pushed = dfs(network, source, sink, Integer.MAX_VALUE, visited);
            operations += visitedCount(visited);

            if (pushed == 0) {
                break;
            }
            maxFlow += pushed;
            paths++;
        }

        long time = System.nanoTime() - start;
        return new FlowResult("Ford-Fulkerson (DFS)", maxFlow, time, paths, operations);
    }

    private static int dfs(ResidualFlowNetwork network, int u, int sink, int flow, boolean[] visited) {
        if (u == sink) return flow;
        visited[u] = true;

        for (ResidualFlowNetwork.FlowEdge edge : network.getAdjacentEdges(u)) {
            if (!visited[edge.to] && edge.remainingCapacity() > 0) {
                int pushed = dfs(network, edge.to, sink, Math.min(flow, edge.remainingCapacity()), visited);
                if (pushed > 0) {
                    edge.augment(pushed);
                    return pushed;
                }
            }
        }
        return 0;
    }

    private static int visitedCount(boolean[] v) {
        int count = 0;
        for (boolean b : v) if (b) count++;
        return count;
    }
}
