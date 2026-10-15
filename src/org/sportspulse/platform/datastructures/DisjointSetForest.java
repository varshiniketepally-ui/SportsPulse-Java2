package org.sportspulse.platform.datastructures;

/**
 * Disjoint Set Union (DSU) / Union-Find with path compression and rank heuristic.
 */
public class DisjointSetForest {

    private final int[] parent;
    private final int[] rank;
    private int count;

    public DisjointSetForest(int n) {
        this.parent = new int[n];
        this.rank = new int[n];
        this.count = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public int find(int p) {
        if (p < 0 || p >= parent.length) {
            throw new IndexOutOfBoundsException();
        }
        int root = p;
        while (root != parent[root]) {
            root = parent[root];
        }
        // Path compression
        int curr = p;
        while (curr != root) {
            int next = parent[curr];
            parent[curr] = root;
            curr = next;
        }
        return root;
    }

    public boolean union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) {
            return false;
        }
        if (rank[rootP] < rank[rootQ]) {
            parent[rootP] = rootQ;
        } else if (rank[rootP] > rank[rootQ]) {
            parent[rootQ] = rootP;
        } else {
            parent[rootQ] = rootP;
            rank[rootP]++;
        }
        count--;
        return true;
    }

    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    public int getComponentCount() {
        return count;
    }

    public int size() {
        return parent.length;
    }
}
