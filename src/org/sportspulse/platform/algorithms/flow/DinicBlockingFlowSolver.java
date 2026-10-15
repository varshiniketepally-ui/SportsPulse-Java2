package org.sportspulse.platform.algorithms.flow;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

/**
 * Dinic's Algorithm for Maximum Flow.
 * Uses BFS Level Graphs + DFS Blocking Flows with dead-end elimination (ptr array).
 * Time Complexity: O(V^2 * E), and O(E * sqrt(V)) for unit networks.
 */
public class DinicBlockingFlowSolver {

    public static FordFulkersonDfsSolver.FlowResult solve(ResidualFlowNetwork network, int source, int sink) {
        network.resetFlows();
        long start = System.nanoTime();
        int maxFlow = 0;
        int phases = 0;
        long operations = 0;

        int n = network.getVertexCount();
        int[] level = new int[n];
        int[] ptr = new int[n];

        while (bfs(network, source, sink, level)) {
            phases++;
            Arrays.fill(ptr, 0);

            while (true) {
                operations++;
                int pushed = dfs(network, source, sink, Integer.MAX_VALUE, level, ptr);
                if (pushed == 0) {
                    break;
                }
                maxFlow += pushed;
            }
        }

        long time = System.nanoTime() - start;
        return new FordFulkersonDfsSolver.FlowResult("Dinic's Blocking Flow", maxFlow, time, phases, operations);
    }

    private static boolean bfs(ResidualFlowNetwork network, int source, int sink, int[] level) {
        Arrays.fill(level, -1);
        level[source] = 0;
        Queue<Integer> q = new ArrayDeque<>();
        q.add(source);

        while (!q.isEmpty()) {
            int u = q.poll();
            for (ResidualFlowNetwork.FlowEdge edge : network.getAdjacentEdges(u)) {
                if (edge.remainingCapacity() > 0 && level[edge.to] == -1) {
                    level[edge.to] = level[u] + 1;
                    q.add(edge.to);
                }
            }
        }
        return level[sink] != -1;
    }

    private static int dfs(ResidualFlowNetwork network, int u, int sink, int flow, int[] level, int[] ptr) {
        if (u == sink || flow == 0) return flow;

        var edges = network.getAdjacentEdges(u);
        for (; ptr[u] < edges.size(); ptr[u]++) {
            ResidualFlowNetwork.FlowEdge edge = edges.get(ptr[u]);
            if (level[edge.to] == level[u] + 1 && edge.remainingCapacity() > 0) {
                int pushed = dfs(network, edge.to, sink, Math.min(flow, edge.remainingCapacity()), level, ptr);
                if (pushed > 0) {
                    edge.augment(pushed);
                    return pushed;
                }
            }
        }
        return 0;
    }
}
