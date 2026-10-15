package org.sportspulse.platform.algorithms.flow;

import java.util.ArrayList;
import java.util.List;

/**
 * Residual Flow Network graph representation.
 * Supports forward capacities and reverse residual edges.
 */
public class ResidualFlowNetwork {

    public static class FlowEdge {
        public final int from;
        public final int to;
        public final int capacity;
        public int flow;
        public FlowEdge residual;

        public FlowEdge(int from, int to, int capacity) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
            this.flow = 0;
        }

        public int remainingCapacity() {
            return capacity - flow;
        }

        public void augment(int bottleneck) {
            this.flow += bottleneck;
            this.residual.flow -= bottleneck;
        }
    }

    private final int vertexCount;
    private final List<List<FlowEdge>> adj;
    private final List<FlowEdge> allEdges;

    public ResidualFlowNetwork(int vertexCount) {
        this.vertexCount = vertexCount;
        this.adj = new ArrayList<>(vertexCount);
        this.allEdges = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, int capacity) {
        FlowEdge forward = new FlowEdge(from, to, capacity);
        FlowEdge backward = new FlowEdge(to, from, 0); // Residual reverse edge
        forward.residual = backward;
        backward.residual = forward;

        adj.get(from).add(forward);
        adj.get(to).add(backward);
        allEdges.add(forward);
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public List<FlowEdge> getAdjacentEdges(int u) {
        return adj.get(u);
    }

    public List<FlowEdge> getAllEdges() {
        return allEdges;
    }

    public void resetFlows() {
        for (FlowEdge edge : allEdges) {
            edge.flow = 0;
            edge.residual.flow = 0;
        }
    }
}
