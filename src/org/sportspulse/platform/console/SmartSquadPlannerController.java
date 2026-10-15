package org.sportspulse.platform.console;

import org.sportspulse.platform.algorithms.dp.BitmaskRosterSelector;
import org.sportspulse.platform.algorithms.dp.SubsetSynergyOptimizer;
import org.sportspulse.platform.algorithms.npc.BooleanSatSolver;
import org.sportspulse.platform.algorithms.npc.VertexCoverApproximator;
import org.sportspulse.platform.domain.model.AthletePosition;
import org.sportspulse.platform.domain.model.AthleteProfile;
import org.sportspulse.platform.domain.repository.DataStore;

import java.util.*;

/**
 * Smart Squad Planner:
 * Empowers head coaches and technical directors to formulate optimal,
 * tactical lineups satisfying positional, physical, and team chemistry requirements.
 * Powered by:
 * - Bitmask Dynamic Programming
 * - DP on Subsets Tactical Synergy
 * - Propositional Boolean SAT Rule Solver
 * - Vertex Cover 2-Approximation for Player Conflicts
 */
public class SmartSquadPlannerController {

    public static void showMenu() {
        DataStore ds = DataStore.getInstance();
        boolean back = false;

        while (!back) {
            TerminalIO.printHeader("SMART SQUAD PLANNER");
            TerminalIO.println("  [1] Optimize Starting Lineup (DP on Subsets Chemistry & Readiness)");
            TerminalIO.println("  [2] Generate Valid Tactical Lineups (Bitmask DP Positional Constraints)");
            TerminalIO.println("  [3] Squad Selection Rule Compliance Checker (Boolean SAT Solver)");
            TerminalIO.println("  [4] Player Conflict Resolution & Coverage (Vertex Cover Approximation)");
            TerminalIO.println("  [0] Return to Main Dashboard");

            int choice = TerminalIO.readInt("Select Option", 0, 4);
            switch (choice) {
                case 1 -> buildSynergyLineup(ds);
                case 2 -> buildBitmaskLineups(ds);
                case 3 -> checkSquadRules();
                case 4 -> resolvePlayerConflicts();
                case 0 -> back = true;
            }
        }
    }

    private static void buildSynergyLineup(DataStore ds) {
        TerminalIO.printSubHeader("Optimal Starting Lineup via Tactical Chemistry Maximization");
        int targetSize = TerminalIO.readInt("Enter Target Starting Squad Size", 3, 7);

        List<AthleteProfile> candidates = ds.getAllAthletes();
        long start = System.nanoTime();
        var result = SubsetSynergyOptimizer.optimizeLineup(candidates, targetSize);
        long time = System.nanoTime() - start;

        TerminalIO.printSuccess("Recommended Optimal Starting Squad:");
        TerminalIO.println(String.format("  Overall Synergy Score: %.2f (Readiness: %.1f, Chemistry Bonus: +%.1f)",
                result.getTotalSynergyScore(), result.getIndividualReadinessScore(), result.getChemistryBonusScore()));

        String[] headers = new String[]{"Player ID", "Name", "Position", "Readiness", "Franchise"};
        List<String[]> rows = new ArrayList<>();
        for (AthleteProfile a : result.getSelectedSquad()) {
            rows.add(new String[]{a.getId(), a.getName(), a.getPrimaryPosition().getCode(),
                    String.format("%.1f", a.getEffectiveReadiness()), a.getFranchiseId()});
        }
        TerminalIO.printTable(headers, rows);

        askTechnicalDetails("DP on Subsets (State Mask: 2^N states)",
                String.format("Optimized %d athletes from %d candidates. DP recurrence: dp[mask | (1 << k)] = max(dp[mask] + readiness + sum(chemistry))",
                        targetSize, Math.min(candidates.size(), 16)),
                time, "O(2^N * N)");
        TerminalIO.pressEnterToContinue();
    }

    private static void buildBitmaskLineups(DataStore ds) {
        TerminalIO.printSubHeader("Constrained Tactical Squad Selection");
        TerminalIO.println("Configure Lineup Requirements:");
        int reqGk = TerminalIO.readInt("Required Goalkeepers", 1, 2);
        int reqDef = TerminalIO.readInt("Required Defenders", 1, 3);
        int reqMid = TerminalIO.readInt("Required Midfielders", 1, 3);
        int reqFwd = TerminalIO.readInt("Required Forwards", 1, 3);
        int total = reqGk + reqDef + reqMid + reqFwd;

        double minFit = TerminalIO.readDouble("Minimum Fitness Score Threshold", 60.0, 95.0);
        int maxLow = TerminalIO.readInt("Maximum Allowed Low-Fitness Players", 0, 2);

        var constraint = new BitmaskRosterSelector.SquadConstraint(total, reqGk, reqDef, reqMid, reqFwd, 0, minFit, maxLow);
        List<AthleteProfile> pool = ds.getAllAthletes();

        long start = System.nanoTime();
        var validLineups = BitmaskRosterSelector.findValidRosters(pool, constraint);
        long time = System.nanoTime() - start;

        if (validLineups.isEmpty()) {
            TerminalIO.printWarning("No valid squads satisfy all constraints simultaneously.");
        } else {
            TerminalIO.printSuccess(String.format("Found %d valid lineup configurations! Showing Top 3:", validLineups.size()));
            for (int i = 0; i < Math.min(3, validLineups.size()); i++) {
                var r = validLineups.get(i);
                TerminalIO.println(TerminalIO.color(TerminalIO.BOLD + TerminalIO.YELLOW,
                        String.format("\n--- Tactical Option #%d (Total Rating: %.1f, Avg Fitness: %.1f) ---",
                                i + 1, r.getTotalRating(), r.getAverageFitness())));
                for (AthleteProfile a : r.getAthletes()) {
                    TerminalIO.println(String.format("    [%s] %s | %s (Rating: %.1f, Fit: %.1f)",
                            a.getId(), a.getName(), a.getPrimaryPosition().getCode(), a.getOverallRating(), a.getFitnessScore()));
                }
            }
        }

        askTechnicalDetails("Bitmask DP State Space Search",
                String.format("Evaluated 2^%d = %d combinations for size %d", Math.min(pool.size(), 20), 1 << Math.min(pool.size(), 20), total),
                time, "O(2^N * N)");
        TerminalIO.pressEnterToContinue();
    }

    private static void checkSquadRules() {
        TerminalIO.printSubHeader("Squad Selection Natural Rule Compliance Checker");
        TerminalIO.println("Current Team Selection Rules:");
        TerminalIO.println("  Rule 1: Either play Sterling OR play DeVries (At least one must start).");
        TerminalIO.println("  Rule 2: If Sterling is rested, Modric MUST start.");
        TerminalIO.println("  Rule 3: If both DeVries and Modric start, Rudiger must be in defense.");

        // (Sterling OR DeVries) AND (~Sterling OR Modric) AND (~DeVries OR ~Modric OR Rudiger)
        var c1 = new BooleanSatSolver.Clause(new BooleanSatSolver.Literal("PlaySterling", true), new BooleanSatSolver.Literal("PlayDeVries", true));
        var c2 = new BooleanSatSolver.Clause(new BooleanSatSolver.Literal("PlaySterling", false), new BooleanSatSolver.Literal("PlayModric", true));
        var c3 = new BooleanSatSolver.Clause(new BooleanSatSolver.Literal("PlayDeVries", false), new BooleanSatSolver.Literal("PlayModric", false), new BooleanSatSolver.Literal("PlayRudiger", true));

        long start = System.nanoTime();
        var satRes = BooleanSatSolver.solve(Arrays.asList(c1, c2, c3));
        long time = System.nanoTime() - start;

        if (satRes.satisfiable) {
            TerminalIO.printSuccess("VALID SQUAD SELECTION CONFIGURATION FOUND!");
            TerminalIO.println("Compliant Starting Assignment:");
            satRes.model.forEach((varName, value) -> {
                TerminalIO.println(String.format("  * %-15s : %s", varName, value ? "STARTS [YES]" : "BENCH [NO]"));
            });
        } else {
            TerminalIO.printError("UNSATISFIABLE CONFLICT: No valid lineup can satisfy all selection rules simultaneously.");
        }

        askTechnicalDetails("DPLL Boolean SAT Solver (Unit Propagation & Backtracking)",
                "3 Tactical Propositional Clauses with 4 Variables", time, "O(2^V) worst-case exponential, polynomial on unit clauses");
        TerminalIO.pressEnterToContinue();
    }

    private static void resolvePlayerConflicts() {
        TerminalIO.printSubHeader("Player Conflict Coverage & Roster Resolution");
        TerminalIO.println("Analyzing squad tactical friction & dressing room conflicts:");
        List<String> players = Arrays.asList("Sterling", "DeVries", "Rudiger", "Mbappo", "Kane", "Rice");
        List<VertexCoverApproximator.Edge> conflicts = Arrays.asList(
                new VertexCoverApproximator.Edge("Sterling", "Rudiger"),
                new VertexCoverApproximator.Edge("DeVries", "Rudiger"),
                new VertexCoverApproximator.Edge("Rudiger", "Mbappo"),
                new VertexCoverApproximator.Edge("Kane", "Rice"),
                new VertexCoverApproximator.Edge("Mbappo", "Kane")
        );

        for (var e : conflicts) {
            TerminalIO.println("  * Conflict: " + e.u + " <---> " + e.v);
        }

        long start = System.nanoTime();
        var comp = VertexCoverApproximator.compare(players, conflicts);
        long time = System.nanoTime() - start;

        TerminalIO.printSuccess("Conflict Resolution Plan:");
        TerminalIO.println("  Players to Rest/Substitute (Exact Optimal):      " + comp.optimalCover + " (Size: " + comp.optimalSize + ")");
        TerminalIO.println("  Fast Heuristic Rest Assignment (2-Approx):       " + comp.approximateCover + " (Size: " + comp.approxSize + ")");
        TerminalIO.println(String.format("  Heuristic Approximation Ratio:                  %.2fx (Theoretical Upper Bound: <= 2.0x)", comp.approximationRatio));

        askTechnicalDetails("Vertex Cover 2-Approximation (Maximal Matching) vs Exact Optimal",
                String.format("%d conflict edges resolved with %d player substitutions", conflicts.size(), comp.optimalSize),
                time, "Approximation: O(V + E) Linear | Exact: O(2^V) Branch-and-Bound");
        TerminalIO.pressEnterToContinue();
    }

    private static void askTechnicalDetails(String algorithm, String summary, long timeNs, String complexity) {
        boolean show = TerminalIO.readBoolean("\n[T] View technical DSA algorithmic details?");
        if (show) {
            TerminalIO.println(TerminalIO.color(TerminalIO.CYAN, "--------------------------------------------------------------------------------"));
            TerminalIO.println(TerminalIO.color(TerminalIO.BOLD + TerminalIO.CYAN, "  TECHNICAL DSA OPTIMIZATION PROFILE"));
            TerminalIO.println(TerminalIO.color(TerminalIO.CYAN, "--------------------------------------------------------------------------------"));
            TerminalIO.println("  Algorithm Executed:    " + algorithm);
            TerminalIO.println("  Analytical Context:    " + summary);
            TerminalIO.println(String.format("  Execution Time:        %.3f µs", timeNs / 1000.0));
            TerminalIO.println("  Theoretical Bound:     " + complexity);
            TerminalIO.println("  Academic Module:       Module 3 & Module 5 (Advanced DP & NP-Hard Solvers)");
            TerminalIO.println(TerminalIO.color(TerminalIO.CYAN, "--------------------------------------------------------------------------------"));
        }
    }
}
