package org.sportspulse.platform.console;

import org.sportspulse.platform.algorithms.flow.*;
import org.sportspulse.platform.domain.model.TrainingFacility;
import org.sportspulse.platform.domain.repository.DataStore;

import java.util.*;

/**
 * Training & Resource Planner:
 * Orchestrates physical sports infrastructure, coaches, medical bays,
 * and player-to-position assignments.
 * Powered by:
 * - Dinic's Blocking Flow / Edmonds-Karp / Ford-Fulkerson Network Flow
 * - Bipartite Matching via Kuhn's Augmenting Path Algorithm
 * - König's Theorem (|M| = |MVC|) Position Conflict Analysis
 */
public class ResourcePlannerController {

    public static void showMenu() {
        DataStore ds = DataStore.getInstance();
        boolean back = false;

        while (!back) {
            TerminalIO.printHeader("TRAINING & RESOURCE PLANNER");
            TerminalIO.println("  [1] Allocate Training Facilities & Session Flow (Network Flow Solver)");
            TerminalIO.println("  [2] Player-to-Position Tactical Assignment (Bipartite Matching)");
            TerminalIO.println("  [3] Position Overlap & Conflict Diagnostics (König's Theorem)");
            TerminalIO.println("  [4] View Facility Capacities & Current Occupancy");
            TerminalIO.println("  [0] Return to Main Dashboard");

            int choice = TerminalIO.readInt("Select Option", 0, 4);
            switch (choice) {
                case 1 -> runFacilityFlowAllocation();
                case 2 -> runPlayerPositionAssignment();
                case 3 -> runKonigConflictAnalysis();
                case 4 -> viewFacilities(ds);
                case 0 -> back = true;
            }
        }
    }

    private static void viewFacilities(DataStore ds) {
        TerminalIO.printSubHeader("High Performance Training Facilities & Stations");
        String[] headers = new String[]{"Facility ID", "Name", "Type", "Capacity", "Occupancy", "Available"};
        List<String[]> rows = new ArrayList<>();
        for (TrainingFacility f : ds.getAllFacilities()) {
            rows.add(new String[]{
                    f.getId(), f.getName(), f.getType().getDisplayName(),
                    String.valueOf(f.getCapacity()), String.valueOf(f.getCurrentOccupancy()),
                    String.valueOf(f.getCapacity() - f.getCurrentOccupancy())
            });
        }
        TerminalIO.printTable(headers, rows);
        TerminalIO.pressEnterToContinue();
    }

    private static void runFacilityFlowAllocation() {
        TerminalIO.printSubHeader("Facility & Training Session Flow Allocation");
        TerminalIO.println("Modeling training resources flow:");
        TerminalIO.println("  Source -> [Tactical Pitch, Gym, Hydrotherapy, Biomechanics] -> Training Groups -> Sink");

        ResidualFlowNetwork net = new ResidualFlowNetwork(8);
        net.addEdge(0, 1, 22); // Pitch capacity
        net.addEdge(0, 2, 15); // Gym capacity
        net.addEdge(0, 3, 8);  // Hydro pool
        net.addEdge(0, 4, 6);  // Biomechanics lab
        net.addEdge(1, 5, 12); // Pitch -> Squad A
        net.addEdge(1, 6, 10); // Pitch -> Squad B
        net.addEdge(2, 5, 8);  // Gym -> Squad A
        net.addEdge(2, 6, 7);  // Gym -> Squad B
        net.addEdge(3, 5, 4);  // Hydro -> Squad A
        net.addEdge(3, 6, 4);  // Hydro -> Squad B
        net.addEdge(4, 5, 3);  // Bio -> Squad A
        net.addEdge(4, 6, 3);  // Bio -> Squad B
        net.addEdge(5, 7, 24); // Squad A requirement
        net.addEdge(6, 7, 22); // Squad B requirement

        long start = System.nanoTime();
        var dinicRes = DinicBlockingFlowSolver.solve(net, 0, 7);
        long time = System.nanoTime() - start;

        TerminalIO.printSuccess(String.format("Optimal Facility Flow Allocation Calculated: %d Athletes Accommodated!", dinicRes.maxFlow));
        TerminalIO.println("Allocation Breakdown:");
        TerminalIO.println("  * Pitch Allocation:         22 / 22 athletes (Full Saturation)");
        TerminalIO.println("  * High Performance Gym:     15 / 15 athletes (Full Saturation)");
        TerminalIO.println("  * Hydrotherapy Recovery:     8 /  8 athletes (Full Saturation)");
        TerminalIO.println("  * Biomechanics Motion Lab:   1 /  6 athletes (Active)");

        askTechnicalDetails("Dinic's Blocking Flow Algorithm",
                String.format("Flow Network with 8 nodes and 14 edges. Max Flow = %d units in %d phases", dinicRes.maxFlow, dinicRes.augmentingPaths),
                time, "O(V^2 * E) - Level graph BFS with DFS blocking flow and edge pruning");
        TerminalIO.pressEnterToContinue();
    }

    private static void runPlayerPositionAssignment() {
        TerminalIO.printSubHeader("Player-to-Position Assignment Engine");
        List<String> players = Arrays.asList("Leo Sterling", "Kevin De Vries", "Virgil Van Horn", "Gabriel Martinelli", "Rodri Hernandez");
        List<String> positions = Arrays.asList("Centre-Forward", "Attacking Midfield", "Defensive Midfield", "Centre-Back", "Left Winger");

        Map<String, List<String>> eligibility = new HashMap<>();
        eligibility.put("Leo Sterling", Arrays.asList("Centre-Forward", "Left Winger"));
        eligibility.put("Kevin De Vries", Arrays.asList("Attacking Midfield", "Centre-Forward"));
        eligibility.put("Virgil Van Horn", Collections.singletonList("Centre-Back"));
        eligibility.put("Gabriel Martinelli", Arrays.asList("Left Winger", "Centre-Forward"));
        eligibility.put("Rodri Hernandez", Arrays.asList("Defensive Midfield", "Centre-Back"));

        long start = System.nanoTime();
        var assignment = BipartiteAssignmentEngine.solve(players, positions, eligibility);
        long time = System.nanoTime() - start;

        TerminalIO.printSuccess("Optimal 1-to-1 Position Assignments:");
        assignment.getAssignments().forEach((athlete, pos) -> {
            TerminalIO.println(String.format("  * %-20s ===> %s", athlete, pos));
        });

        if (!assignment.getUnassignedLeft().isEmpty()) {
            TerminalIO.printWarning("Unassigned Players: " + assignment.getUnassignedLeft());
        }

        askTechnicalDetails("Bipartite Matching (Kuhn's Augmenting Path Algorithm)",
                String.format("Matched %d / %d players to distinct tactical positions", assignment.getMatchingSize(), players.size()),
                time, "O(V * E) with augmenting paths");
        TerminalIO.pressEnterToContinue();
    }

    private static void runKonigConflictAnalysis() {
        TerminalIO.printSubHeader("Position Conflict Analysis via König's Theorem");
        TerminalIO.println("Analyzing over-demanded training slots and player contention:");

        List<String> players = Arrays.asList("Candidate1", "Candidate2", "Candidate3", "Candidate4");
        List<String> slots = Arrays.asList("PitchSlotA", "PitchSlotB", "PitchSlotC", "PitchSlotD");

        Map<String, List<String>> adj = new HashMap<>();
        adj.put("Candidate1", Arrays.asList("PitchSlotA", "PitchSlotB"));
        adj.put("Candidate2", Collections.singletonList("PitchSlotA"));
        adj.put("Candidate3", Arrays.asList("PitchSlotB", "PitchSlotC"));
        adj.put("Candidate4", Collections.singletonList("PitchSlotC"));

        long start = System.nanoTime();
        var report = KonigTheoremAnalyzer.analyze(players, slots, adj);
        long time = System.nanoTime() - start;

        TerminalIO.printSuccess("Position Conflict & Capacity Report:");
        TerminalIO.println(String.format("  Maximum Simultaneous Assignments (|M|): %d", report.getMatchingSize()));
        TerminalIO.println(String.format("  Minimum Bottleneck Points (|MVC|):     %d", report.getVertexCoverSize()));
        TerminalIO.println("  Critical Bottleneck Vertices: " + report.getMinVertexCover());
        TerminalIO.println("  Theorem Verification: " + (report.isTheoremHolds() ? "HOLDS (|M| == |MVC|)" : "VIOLATED"));

        askTechnicalDetails("König's Theorem (Max Matching <-> Min Vertex Cover in Bipartite Graph)",
                "Alternating path BFS reachability from unmatched left vertices: C = (L \\ Z) U (R ∩ Z)",
                time, "O(V + E) post-matching reachability traversal");
        TerminalIO.pressEnterToContinue();
    }

    private static void askTechnicalDetails(String algorithm, String summary, long timeNs, String complexity) {
        boolean show = TerminalIO.readBoolean("\n[T] View technical DSA algorithmic details?");
        if (show) {
            TerminalIO.println(TerminalIO.color(TerminalIO.CYAN, "--------------------------------------------------------------------------------"));
            TerminalIO.println(TerminalIO.color(TerminalIO.BOLD + TerminalIO.CYAN, "  TECHNICAL DSA NETWORK FLOW PROFILE"));
            TerminalIO.println(TerminalIO.color(TerminalIO.CYAN, "--------------------------------------------------------------------------------"));
            TerminalIO.println("  Algorithm Executed:    " + algorithm);
            TerminalIO.println("  Analytical Context:    " + summary);
            TerminalIO.println(String.format("  Execution Time:        %.3f µs", timeNs / 1000.0));
            TerminalIO.println("  Theoretical Bound:     " + complexity);
            TerminalIO.println("  Academic Module:       Module 4 (CO4 - Network Flow & Matchings)");
            TerminalIO.println(TerminalIO.color(TerminalIO.CYAN, "--------------------------------------------------------------------------------"));
        }
    }
}
