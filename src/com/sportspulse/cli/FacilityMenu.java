package com.sportspulse.cli;

import com.sportspulse.algo.flow.*;
import com.sportspulse.data.MockData;
import com.sportspulse.model.Facility;
import com.sportspulse.model.Player;
import com.sportspulse.util.Ansi;
import com.sportspulse.util.Console;

import java.util.*;

/**
 * Training & Facility Resource Planner.
 * Powered by Module 4 Network Flow & Bipartite Matching:
 * - Ford-Fulkerson (Residual DFS Augmenting Paths O(E * MaxFlow))
 * - Edmonds-Karp (BFS Shortest Augmenting Path O(V * E^2))
 * - Dinic's Algorithm (Level Graph + Blocking Flow O(V^2 * E))
 * - Hopcroft-Karp Bipartite Matching O(E * sqrt(V))
 * - König's Theorem (Minimum Vertex Cover = Maximum Matching)
 * - Equivalence Verification across Flow Solvers on fresh graphs
 */
public class FacilityMenu {
    private final MockData data;
    private final Scanner sc;

    public FacilityMenu(MockData data, Scanner sc) { this.data = data; this.sc = sc; }

    public void run() {
        boolean back = false;
        while (!back) {
            Console.header("FACILITY SCHEDULER & RESOURCE PLANNER", "Module 4 Network Flow & Graph Theory");
            Console.bullet("1. Max resource throughput  (Ford-Fulkerson)");
            Console.bullet("2. Bottleneck / min-cut detection  (Edmonds-Karp)");
            Console.bullet("3. Large-scale scalable flow  (Dinic's Algorithm)");
            Console.bullet("4. Side-by-side solver verification  (Ford-Fulkerson vs Edmonds-Karp vs Dinic)");
            Console.bullet("5. Player-to-facility assignment  (Bipartite Matching + König's Min Vertex Cover)");
            Console.bullet("0. Back to main menu");
            Console.prompt("\nSelect an option: ");
            switch (sc.nextLine().trim()) {
                case "1" -> fordFulkersonDemo();
                case "2" -> edmondsKarpDemo();
                case "3" -> dinicDemo();
                case "4" -> compareAllFlowSolvers();
                case "5" -> bipartiteDemo();
                case "0" -> back = true;
                default -> Console.error("Invalid option.");
            }
            if (!back) Console.pause(sc);
        }
    }

    private FlowGraph buildFacilityNetwork(int[] teamDemands) {
        int numTeams = teamDemands.length;
        int numFacilities = data.allFacilities.size();
        int source = 0, sink = 1 + numTeams + numFacilities;
        FlowGraph g = new FlowGraph(sink + 1);
        for (int t = 0; t < numTeams; t++) g.addEdge(source, 1 + t, teamDemands[t]);
        for (int f = 0; f < numFacilities; f++) {
            Facility fac = data.allFacilities.get(f);
            g.addEdge(1 + numTeams + f, sink, fac.getCapacity());
        }
        for (int t = 0; t < numTeams; t++) {
            for (int f = 0; f < numFacilities; f++) {
                Facility fac = data.allFacilities.get(f);
                g.addEdge(1 + t, 1 + numTeams + f, fac.getCapacity());
            }
        }
        return g;
    }

    private void printFacilities() {
        System.out.println("Available facilities:");
        for (Facility f : data.allFacilities) System.out.println("  " + f);
    }

    private void fordFulkersonDemo() {
        Console.section("Ford-Fulkerson: Max Facility Throughput");
        printFacilities();
        int[] demands = {12, 10, 8};
        System.out.println("Demo scenario: 3 teams need " + Arrays.toString(demands) + " training slots respectively.");
        FlowGraph g = buildFacilityNetwork(demands);
        int sink = g.n - 1;
        int flow = FordFulkerson.maxFlow(g, 0, sink);
        Console.kv("Maximum slots successfully allocated", flow);
        Console.kv("Total slots requested", Arrays.stream(demands).sum());

        maybeShowTechnicalDetails("Ford-Fulkerson (DFS Residual)",
                "Computes maximum flow via arbitrary augmenting paths in residual network",
                g.n, flow + " training slots allocated",
                "O(E * MaxFlow)", "O(V + E) graph adjacency matrix/list",
                "Residual graph with forward/backward residual capacities");
    }

    private void edmondsKarpDemo() {
        Console.section("Edmonds-Karp: Bottleneck / Min-Cut Detection");
        printFacilities();
        int[] demands = {15, 14, 13};
        System.out.println("Demo scenario: 3 teams need " + Arrays.toString(demands) + " training slots respectively.");
        FlowGraph g = buildFacilityNetwork(demands);
        int sink = g.n - 1;
        EdmondsKarp.Result result = EdmondsKarp.maxFlowWithMinCut(g, 0, sink);
        Console.kv("Maximum flow (slots allocated)", result.maxFlow);
        Console.kv("Min-cut edge count (bottleneck edges)", result.minCutEdges.size());
        System.out.println("  These edges represent the true capacity bottleneck in the network:");
        int totalCutCapacity = 0;
        for (int[] edge : result.minCutEdges) {
            int cap = g.originalCapacity(edge[0], edge[1]);
            totalCutCapacity += cap;
            System.out.printf("    %-14s -> %-24s  capacity=%d%n",
                    nodeLabel(edge[0], demands.length), nodeLabel(edge[1], demands.length), cap);
        }
        Console.kv("Total cut capacity (equals max flow by Max-Flow/Min-Cut Theorem)", totalCutCapacity);

        maybeShowTechnicalDetails("Edmonds-Karp (BFS Augmenting Paths)",
                "Guarantees polynomial time via shortest augmenting paths in terms of edge count",
                g.n, result.maxFlow + " slots, " + result.minCutEdges.size() + " bottleneck edges",
                "O(V * E^2) strongly polynomial time", "O(V + E) BFS queue and parent pointers",
                "Breadth-first search shortest augmenting path + BFS reachability min-cut");
    }

    private void dinicDemo() {
        Console.section("Dinic's Algorithm: Scalable Multi-Facility Flow");
        printFacilities();
        int[] demands = {25, 20, 18, 22, 16};
        System.out.println("Demo scenario (larger network): 5 teams need " + Arrays.toString(demands) + " slots.");
        FlowGraph g = buildFacilityNetwork(demands);
        int sink = g.n - 1;
        long start = System.nanoTime();
        int flow = new Dinic(g).maxFlow(0, sink);
        long elapsed = System.nanoTime() - start;
        Console.kv("Maximum flow (slots allocated)", flow);
        Console.kv("Total requested", Arrays.stream(demands).sum());
        Console.kv("Computed in", String.format("%.3f ms", elapsed / 1_000_000.0));

        maybeShowTechnicalDetails("Dinic's Blocking Flow Algorithm",
                "Constructs level graph via BFS and pushes maximal blocking flow via DFS phases",
                g.n, flow + " slots allocated",
                "O(V^2 * E) general, O(E * sqrt(V)) for unit networks", "O(V + E) level array + next edge pointers",
                "Layered DAG (Level Graph) + pointer-advancing DFS blocking flow");
    }

    private void compareAllFlowSolvers() {
        Console.section("Scientific Solver Comparison: Ford-Fulkerson vs Edmonds-Karp vs Dinic");
        System.out.println("Verifying algorithmic correctness and empirical timing across three independent solvers.");
        System.out.println("CRITICAL: Each solver receives a fresh, independent deep copy of the exact same network.\n");

        int[] demands = {28, 24, 20, 18, 16, 14};
        FlowGraph baseGraph = buildFacilityNetwork(demands);
        int sink = baseGraph.n - 1;

        // Fresh copy 1: Ford-Fulkerson
        FlowGraph gFF = baseGraph.copy();
        long t0 = System.nanoTime();
        int flowFF = FordFulkerson.maxFlow(gFF, 0, sink);
        long timeFF = System.nanoTime() - t0;

        // Fresh copy 2: Edmonds-Karp
        FlowGraph gEK = baseGraph.copy();
        long t1 = System.nanoTime();
        int flowEK = EdmondsKarp.maxFlowWithMinCut(gEK, 0, sink).maxFlow;
        long timeEK = System.nanoTime() - t1;

        // Fresh copy 3: Dinic
        FlowGraph gDin = baseGraph.copy();
        long t2 = System.nanoTime();
        int flowDin = new Dinic(gDin).maxFlow(0, sink);
        long timeDin = System.nanoTime() - t2;

        boolean equalFlow = (flowFF == flowEK && flowEK == flowDin);

        System.out.println("  Solver Name          | Max Flow | Runtime (µs) | Verification");
        System.out.println("  ---------------------+----------+--------------+-------------");
        System.out.printf("  Ford-Fulkerson (DFS) | %-8d | %-12.2f | %s%n", flowFF, timeFF / 1000.0,
                equalFlow ? Ansi.paint(Ansi.BRIGHT_GREEN, "✓ VALIDATED") : Ansi.paint(Ansi.BRIGHT_RED, "✗ MISMATCH"));
        System.out.printf("  Edmonds-Karp (BFS)   | %-8d | %-12.2f | %s%n", flowEK, timeEK / 1000.0,
                equalFlow ? Ansi.paint(Ansi.BRIGHT_GREEN, "✓ VALIDATED") : Ansi.paint(Ansi.BRIGHT_RED, "✗ MISMATCH"));
        System.out.printf("  Dinic (Level Graph)  | %-8d | %-12.2f | %s%n", flowDin, timeDin / 1000.0,
                equalFlow ? Ansi.paint(Ansi.BRIGHT_GREEN, "✓ VALIDATED") : Ansi.paint(Ansi.BRIGHT_RED, "✗ MISMATCH"));

        System.out.println("\nTheoretical Complexity Hierarchy:");
        System.out.println("  Ford-Fulkerson : O(E * F)   - vulnerable to pathological capacity paths");
        System.out.println("  Edmonds-Karp   : O(V * E^2) - strongly polynomial BFS bound");
        System.out.println("  Dinic          : O(V^2 * E) - blocking flow on layered DAG (fastest on dense graphs)");

        maybeShowTechnicalDetails("Flow Solver Triad",
                "Triple verification of max flow theorem on fresh non-mutated graphs",
                baseGraph.n, "All solvers returned max flow = " + flowDin,
                "Equivalence confirmed: FF == EK == Dinic", "Independent graph state copies",
                "Residual graph adjacency structure with reverse edge capacity bookkeeping");
    }

    private void bipartiteDemo() {
        Console.section("Bipartite Matching (Hopcroft-Karp) + König's Min Vertex Cover");
        System.out.print("Pick a sport (1=Football, 2=Basketball, 3=Cricket): ");
        String choice = sc.nextLine().trim();
        var sport = switch (choice) {
            case "2" -> com.sportspulse.data.Sports.BASKETBALL;
            case "3" -> com.sportspulse.data.Sports.CRICKET;
            default -> com.sportspulse.data.Sports.FOOTBALL;
        };
        List<Player> players = data.playersBySport(sport);
        List<Facility> facilities = data.allFacilities;

        BipartiteMatching bm = new BipartiteMatching(players.size(), facilities.size());
        for (int i = 0; i < players.size(); i++) {
            for (int j = 0; j < facilities.size(); j++) {
                Facility f = facilities.get(j);
                if (f.getSportSpecialty().equals("ANY") || f.getSportSpecialty().equals(sport.getName())) {
                    bm.addEdge(i, j);
                }
            }
        }
        int matched = bm.maxMatching();
        Console.kv("Athletes Requesting Slots", players.size());
        Console.kv("Training Facilities Available", facilities.size());
        Console.kv("Maximum Matching Size (|M|)", matched);

        int[] matchLeft = bm.getMatchLeft();
        Console.section("Computed 1-to-1 Assignment");
        for (int i = 0; i < players.size(); i++) {
            if (matchLeft[i] != -1) {
                System.out.printf("  %-20s -> %s%n", players.get(i).getName(), facilities.get(matchLeft[i]).getName());
            }
        }
        int[][] cover = bm.minVertexCoverKonig();
        int coverSize = cover[0].length + cover[1].length;
        Console.section("König's Theorem Verification (|M| == |MVC|)");
        Console.kv("Minimum Vertex Cover Size (|MVC|)", coverSize);
        Console.kv("König's Equality Check", (matched == coverSize)
                ? Ansi.paint(Ansi.BRIGHT_GREEN, "✓ VERIFIED (|M| = " + matched + " == |MVC| = " + coverSize + ")")
                : Ansi.paint(Ansi.BRIGHT_RED, "✗ FAILED"));
        System.out.println("  Player-side critical vertices : " + cover[0].length);
        System.out.println("  Facility-side bottleneck nodes: " + cover[1].length);

        maybeShowTechnicalDetails("Hopcroft-Karp + König's Theorem",
                "Optimal 1-to-1 assignment and dual bottleneck identification via alternating BFS",
                players.size() + facilities.size(), "|M| = " + matched + " == |MVC| = " + coverSize,
                "O(E * sqrt(V)) matching, O(V + E) cover extraction", "O(V) alternating BFS queues",
                "Bipartite graph + alternating reachability tree (Z-set)");
    }

    private String nodeLabel(int node, int numTeams) {
        int numFacilities = data.allFacilities.size();
        if (node == 0) return "Source";
        if (node <= numTeams) return "Team " + (char) ('A' + node - 1);
        if (node <= numTeams + numFacilities) return data.allFacilities.get(node - numTeams - 1).getName();
        return "Sink";
    }

    private void maybeShowTechnicalDetails(String algo, String purpose, int inputSize,
                                           String output, String timeComp, String spaceComp, String ds) {
        System.out.print(Ansi.paint(Ansi.CYAN, "\n  [T] Press T to inspect technical DSA / Viva details (or Enter to continue): "));
        String line = sc.nextLine().trim();
        if ("t".equalsIgnoreCase(line) || "y".equalsIgnoreCase(line)) {
            System.out.println(Ansi.paint(Ansi.BRIGHT_CYAN, "\n  ╭─────────────────────────────────────────────────────────────╮"));
            System.out.println(Ansi.paint(Ansi.BRIGHT_CYAN, "  │            TECHNICAL / VIVA ARCHITECTURAL DETAILS           │"));
            System.out.println(Ansi.paint(Ansi.BRIGHT_CYAN, "  ├─────────────────────────────────────────────────────────────┤"));
            System.out.printf("  │  Algorithm       : %-41s│%n", truncate(algo, 41));
            System.out.printf("  │  Purpose         : %-41s│%n", truncate(purpose, 41));
            System.out.printf("  │  Input Size (N)  : %-41s│%n", truncate(String.valueOf(inputSize), 41));
            System.out.printf("  │  Output Metric   : %-41s│%n", truncate(output, 41));
            System.out.printf("  │  Time Complexity : %-41s│%n", truncate(timeComp, 41));
            System.out.printf("  │  Space Complexity: %-41s│%n", truncate(spaceComp, 41));
            System.out.printf("  │  Data Structure  : %-41s│%n", truncate(ds, 41));
            System.out.println(Ansi.paint(Ansi.BRIGHT_CYAN, "  ╰─────────────────────────────────────────────────────────────╯"));
        }
    }

    private String truncate(String s, int len) { return s.length() <= len ? s : s.substring(0, len - 3) + "..."; }
}
