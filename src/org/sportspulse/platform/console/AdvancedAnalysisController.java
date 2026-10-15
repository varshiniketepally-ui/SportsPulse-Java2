package org.sportspulse.platform.console;

import org.sportspulse.platform.algorithms.dp.MatrixChainAnalyticsOptimizer;
import org.sportspulse.platform.algorithms.npc.BooleanSatSolver;
import org.sportspulse.platform.algorithms.npc.NpHardReductionsPipeline;
import org.sportspulse.platform.algorithms.parallel.BrentTheoremWorkSpanModeler;
import org.sportspulse.platform.algorithms.randomized.MillerRabinPrimalityVerifier;
import org.sportspulse.platform.domain.repository.DataStore;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Advanced Analysis & Algorithm Lab (Faculty / Viva Mode):
 * Dedicated academic inspection hub providing rigorous viva-level evidence for:
 * - Matrix Chain Multiplication DP pipeline optimization
 * - Brent's Theorem Work-Span parallel scalability analysis
 * - Miller-Rabin Monte Carlo primality testing
 * - Karp NP-Hard Reductions (3-SAT -> CLIQUE -> IS -> VC)
 * - Custom OpenAddressingHashTable collision & probe diagnostics
 * - Asymptotic complexity mappings across all M1-M6 modules
 */
public class AdvancedAnalysisController {

    public static void showMenu() {
        DataStore ds = DataStore.getInstance();
        boolean back = false;

        while (!back) {
            TerminalIO.printHeader("ADVANCED ANALYSIS & ALGORITHM LAB (VIVA MODE)");
            TerminalIO.println("Faculty & Academic Algorithm Inspection Center:");
            TerminalIO.println("  [1] Analytics Pipeline Optimizer (Matrix Chain Multiplication DP)");
            TerminalIO.println("  [2] Parallel Hardware Work-Span Modeler (Brent's Scheduling Theorem)");
            TerminalIO.println("  [3] Probabilistic Primality Verifier (Miller-Rabin Monte Carlo Algorithm)");
            TerminalIO.println("  [4] Karp NP-Completeness Reduction Chain (3-SAT -> CLIQUE -> IS -> VC)");
            TerminalIO.println("  [5] Custom Hash Table Diagnostics & Probe Telemetry (OpenAddressingHashTable)");
            TerminalIO.println("  [6] Academic Syllabus Complexity Mapping (Modules M1 to M6)");
            TerminalIO.println("  [0] Return to Main Dashboard");

            int choice = TerminalIO.readInt("Select Option", 0, 6);
            switch (choice) {
                case 1 -> runMatrixChainPipeline();
                case 2 -> runBrentModeler();
                case 3 -> runMillerRabinDemo();
                case 4 -> runKarpReductions();
                case 5 -> showHashTableTelemetry(ds);
                case 6 -> showComplexityCheatSheet();
                case 0 -> back = true;
            }
        }
    }

    private static void runMatrixChainPipeline() {
        TerminalIO.printSubHeader("Matrix Chain Multiplication Analytics Optimizer");
        var res = MatrixChainAnalyticsOptimizer.optimizeDefaultSportsPipeline();
        TerminalIO.println(res.toString());
        TerminalIO.println("\nMathematical Recurrence:");
        TerminalIO.println("  m[i, j] = min_{i <= k < j} { m[i, k] + m[k+1, j] + p_{i-1} * p_k * p_j }");
        TerminalIO.println("  Time Complexity: O(K^3) | Space Complexity: O(K^2)");
        TerminalIO.pressEnterToContinue();
    }

    private static void runBrentModeler() {
        TerminalIO.printSubHeader("Brent's Scheduling Theorem Work-Span Modeler");
        long work = TerminalIO.readInt("Total Computational Work W (operations)", 1000, 10000000);
        long span = TerminalIO.readInt("Critical Path Span S (sequential steps)", 1, (int) Math.min(work, 10000));
        int p = TerminalIO.readInt("Hardware Cores / Processors P", 1, 128);

        var report = BrentTheoremWorkSpanModeler.analyze(work, span, p);
        TerminalIO.println("\n" + report);
        TerminalIO.pressEnterToContinue();
    }

    private static void runMillerRabinDemo() {
        TerminalIO.printSubHeader("Miller-Rabin Probabilistic Primality Verification");
        String raw = TerminalIO.readString("Enter integer to test (or enter for default: 1000000007)");
        BigInteger n = raw.isEmpty() ? BigInteger.valueOf(1_000_000_007L) : new BigInteger(raw);
        int k = TerminalIO.readInt("Witness Verification Rounds", 5, 50);

        var report = MillerRabinPrimalityVerifier.testPrimality(n, k);
        TerminalIO.println("\n" + report);
        TerminalIO.pressEnterToContinue();
    }

    private static void runKarpReductions() {
        TerminalIO.printSubHeader("End-to-End Karp Reductions Pipeline");
        TerminalIO.println("Input: 3-CNF Squad Propositional Formula:");
        List<BooleanSatSolver.Clause> clauses = Arrays.asList(
                new BooleanSatSolver.Clause(new BooleanSatSolver.Literal("x1", true), new BooleanSatSolver.Literal("x2", true), new BooleanSatSolver.Literal("x3", true)),
                new BooleanSatSolver.Clause(new BooleanSatSolver.Literal("x1", false), new BooleanSatSolver.Literal("x2", false), new BooleanSatSolver.Literal("x3", true)),
                new BooleanSatSolver.Clause(new BooleanSatSolver.Literal("x1", true), new BooleanSatSolver.Literal("x2", false), new BooleanSatSolver.Literal("x3", false))
        );

        var pipeline = NpHardReductionsPipeline.executePipeline(clauses);
        TerminalIO.println("\n" + pipeline);
        TerminalIO.pressEnterToContinue();
    }

    private static void showHashTableTelemetry(DataStore ds) {
        TerminalIO.printSubHeader("Custom OpenAddressingHashTable Diagnostic Telemetry");
        var table = ds.getAthletesTable();
        TerminalIO.println("Hash Table Internal Architecture: Open Addressing with Quadratic Probing");
        TerminalIO.println("  Formula:         index = (hash(key) + i + i^2) % capacity");
        TerminalIO.println("  Capacity:        " + table.getCapacity() + " slots");
        TerminalIO.println("  Active Entries:  " + table.size());
        TerminalIO.println(String.format("  Load Factor:     %.2f (Threshold: 0.60)", table.getLoadFactor()));
        TerminalIO.println("  Tombstones:      " + table.getTombstoneCount() + " markers");
        TerminalIO.println("  Total Collisions:" + table.getTotalCollisions());
        TerminalIO.println("  Total Probes:    " + table.getTotalProbes());
        TerminalIO.println("  Average Probes:  " + String.format("%.2f probes / operation",
                table.size() > 0 ? (double) table.getTotalProbes() / table.size() : 1.0));
        TerminalIO.pressEnterToContinue();
    }

    private static void showComplexityCheatSheet() {
        TerminalIO.printSubHeader("Academic DSA Syllabus Mapping & Asymptotic Complexities");
        String[] headers = new String[]{"Module", "Algorithm", "Time Complexity", "Aux Space"};
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"M1: Strings", "Naive Brute-Force", "O(N * M)", "O(1)"});
        rows.add(new String[]{"M1: Strings", "Knuth-Morris-Pratt (KMP)", "O(N + M)", "O(M)"});
        rows.add(new String[]{"M1: Strings", "Z-Algorithm", "O(N + M)", "O(N + M)"});
        rows.add(new String[]{"M1: Strings", "Rabin-Karp Rolling Hash", "O(N + M) avg", "O(1)"});
        rows.add(new String[]{"M1: Strings", "Aho-Corasick Automaton", "O(N + matches)", "O(M * Sigma)"});
        rows.add(new String[]{"M2: Suffix", "Standard Suffix Array", "O(N log^2 N)", "O(N)"});
        rows.add(new String[]{"M2: Suffix", "SA-IS Linear Suffix Array", "O(N) linear", "O(N)"});
        rows.add(new String[]{"M2: Suffix", "Kasai's LCP Algorithm", "O(N) linear", "O(N)"});
        rows.add(new String[]{"M2: Suffix", "Explicit Suffix Tree", "O(N^2) / O(N)", "O(N)"});
        rows.add(new String[]{"M2: Suffix", "Suffix Automaton (DAWG)", "O(N) linear", "O(N)"});
        rows.add(new String[]{"M3: DP", "Levenshtein Distance", "O(N * M)", "O(N * M)"});
        rows.add(new String[]{"M3: DP", "Damerau-Levenshtein", "O(N * M)", "O(N * M)"});
        rows.add(new String[]{"M3: DP", "Bitmask Roster DP", "O(2^N * N)", "O(2^N)"});
        rows.add(new String[]{"M3: DP", "Subset Synergy DP", "O(2^N * N)", "O(2^N)"});
        rows.add(new String[]{"M3: DP", "Matrix Chain Multiplier", "O(K^3)", "O(K^2)"});
        rows.add(new String[]{"M4: Flow", "Ford-Fulkerson (DFS)", "O(E * f_max)", "O(V)"});
        rows.add(new String[]{"M4: Flow", "Edmonds-Karp (BFS)", "O(V * E^2)", "O(V + E)"});
        rows.add(new String[]{"M4: Flow", "Dinic's Blocking Flow", "O(V^2 * E)", "O(V + E)"});
        rows.add(new String[]{"M4: Flow", "Bipartite Kuhn Matching", "O(V * E)", "O(V + E)"});
        rows.add(new String[]{"M4: Flow", "König's Theorem MVC", "O(V + E)", "O(V)"});
        rows.add(new String[]{"M5: NP", "DPLL Boolean SAT Solver", "O(2^V) worst", "O(V)"});
        rows.add(new String[]{"M5: NP", "3-SAT to CLIQUE Reduction", "O(K^2)", "O(K)"});
        rows.add(new String[]{"M5: NP", "Vertex Cover 2-Approx", "O(V + E) linear", "O(V)"});
        rows.add(new String[]{"M6: Rand", "Stochastic QuickSort", "O(N log N) avg", "O(log N)"});
        rows.add(new String[]{"M6: Rand", "Reservoir Sampling", "O(N) stream", "O(K)"});
        rows.add(new String[]{"M6: Rand", "Miller-Rabin Primality", "O(K log^3 N)", "O(1)"});
        rows.add(new String[]{"M6: Par", "Blelloch Parallel Scan", "Work O(N), Span O(log N)", "O(N)"});
        rows.add(new String[]{"M6: Par", "ForkJoin Parallel Reduce", "Work O(N), Span O(log N)", "O(P)"});
        rows.add(new String[]{"M6: Par", "Brent's Scheduling Lemma", "T(P) <= (W-S)/P + S", "O(1)"});

        TerminalIO.printTable(headers, rows);
        TerminalIO.pressEnterToContinue();
    }
}
