package org.sportspulse.platform.console;

import org.sportspulse.platform.algorithms.dp.*;
import org.sportspulse.platform.algorithms.flow.*;
import org.sportspulse.platform.algorithms.npc.*;
import org.sportspulse.platform.algorithms.parallel.*;
import org.sportspulse.platform.algorithms.randomized.*;
import org.sportspulse.platform.algorithms.strings.*;
import org.sportspulse.platform.algorithms.suffix.*;
import org.sportspulse.platform.domain.model.AthleteProfile;
import org.sportspulse.platform.domain.model.MatchDossier;
import org.sportspulse.platform.domain.repository.DataStore;

import java.math.BigInteger;
import java.util.*;

/**
 * Interactive Console Controller providing deep algorithmic exploration
 * for M1 (String Matching), M2 (Suffix Structures), M3 (Dynamic Programming),
 * M4 (Network Flow), M5 (NP-Completeness), and M6 (Randomized & Parallel).
 */
public class AnalyticsAlgorithmsController {

    // --- M1: STRING ENGINE ---
    public static void showStringEngineMenu() {
        DataStore ds = DataStore.getInstance();
        boolean back = false;

        while (!back) {
            TerminalIO.printHeader("M1: SPORTS TEXT SEARCH & PATTERN MATCHING ENGINE");
            TerminalIO.println("  [1] Search Phrase Across Scouting Dossiers (Algorithm Comparison)");
            TerminalIO.println("  [2] Naive Pattern Matching (with Comparison Counter)");
            TerminalIO.println("  [3] Knuth-Morris-Pratt (KMP) Searcher");
            TerminalIO.println("  [4] Z-Algorithm Pattern Occurrence & Periodicity");
            TerminalIO.println("  [5] Rabin-Karp Rolling Hash (Collision Diagnostic)");
            TerminalIO.println("  [6] Multi-Keyword Live Match Event Detector (Aho-Corasick)");
            TerminalIO.println("  [0] Return to Main Dashboard");

            int choice = TerminalIO.readInt("Select Option", 0, 6);
            switch (choice) {
                case 1 -> compareAllStringMatchers(ds);
                case 2 -> runNaiveSearch(ds);
                case 3 -> runKmpSearch(ds);
                case 4 -> runZSearch(ds);
                case 5 -> runRabinKarpSearch(ds);
                case 6 -> runAhoCorasick(ds);
                case 0 -> back = true;
            }
        }
    }

    private static String getCorpusText(DataStore ds) {
        StringBuilder sb = new StringBuilder();
        for (MatchDossier d : ds.getAllDossiers()) {
            sb.append(d.getFullContent()).append(" ");
        }
        for (var m : ds.getAllFixtures()) {
            sb.append(m.getCommentaryStream()).append(" ");
        }
        return sb.toString();
    }

    private static void compareAllStringMatchers(DataStore ds) {
        TerminalIO.printSubHeader("Side-by-Side String Algorithm Evaluation");
        String text = getCorpusText(ds);
        String pattern = TerminalIO.readNonEmptyString("Enter search pattern (e.g. 'tactical' or 'pressing')");

        var naive = BruteForcePatternMatcher.search(text, pattern);
        var kmp = KnuthMorrisPrattSearcher.search(text, pattern);
        var z = ZAlgorithmMatcher.search(text, pattern);
        var rk = RabinKarpRollingHasher.search(text, pattern);

        String[] headers = new String[]{"Algorithm", "Matches", "Comparisons", "Time (µs)", "Positions"};
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{naive.getAlgorithmName(), String.valueOf(naive.getMatchCount()), String.valueOf(naive.getComparisons()), String.format("%.2f", naive.getExecutionTimeNs() / 1000.0), naive.getMatchIndices().toString()});
        rows.add(new String[]{kmp.getAlgorithmName(), String.valueOf(kmp.getMatchCount()), String.valueOf(kmp.getComparisons()), String.format("%.2f", kmp.getExecutionTimeNs() / 1000.0), kmp.getMatchIndices().toString()});
        rows.add(new String[]{z.getAlgorithmName(), String.valueOf(z.getMatchCount()), String.valueOf(z.getComparisons()), String.format("%.2f", z.getExecutionTimeNs() / 1000.0), z.getMatchIndices().toString()});
        rows.add(new String[]{rk.getAlgorithmName(), String.valueOf(rk.getMatchCount()), String.valueOf(rk.getComparisons()), String.format("%.2f", rk.getExecutionTimeNs() / 1000.0), rk.getMatchIndices().toString()});

        TerminalIO.printTable(headers, rows);
        TerminalIO.pressEnterToContinue();
    }

    private static void runNaiveSearch(DataStore ds) {
        String text = getCorpusText(ds);
        String pat = TerminalIO.readNonEmptyString("Pattern to search");
        var res = BruteForcePatternMatcher.search(text, pat);
        TerminalIO.println("\n" + res);
        TerminalIO.pressEnterToContinue();
    }

    private static void runKmpSearch(DataStore ds) {
        String text = getCorpusText(ds);
        String pat = TerminalIO.readNonEmptyString("Pattern to search");
        var res = KnuthMorrisPrattSearcher.search(text, pat);
        TerminalIO.println("\n" + res);
        TerminalIO.pressEnterToContinue();
    }

    private static void runZSearch(DataStore ds) {
        String text = getCorpusText(ds);
        String pat = TerminalIO.readNonEmptyString("Pattern to search");
        var res = ZAlgorithmMatcher.search(text, pat);
        TerminalIO.println("\n" + res);
        TerminalIO.pressEnterToContinue();
    }

    private static void runRabinKarpSearch(DataStore ds) {
        String text = getCorpusText(ds);
        String pat = TerminalIO.readNonEmptyString("Pattern to search");
        var res = RabinKarpRollingHasher.search(text, pat);
        TerminalIO.println("\n" + res);
        TerminalIO.pressEnterToContinue();
    }

    private static void runAhoCorasick(DataStore ds) {
        String text = getCorpusText(ds);
        String rawKeywords = TerminalIO.readString("Enter space-separated keywords (or press enter for default: goal tackle foul corner assist)");
        if (rawKeywords.isEmpty()) {
            rawKeywords = "goal tackle foul corner assist pressing stamina counter-attack";
        }
        List<String> keywords = Arrays.asList(rawKeywords.split("\\s+"));
        AhoCorasickEventAutomaton ac = new AhoCorasickEventAutomaton(keywords);
        var hits = ac.search(text);

        TerminalIO.println(String.format("\nAho-Corasick found %d keyword hits:", hits.size()));
        String[] headers = new String[]{"Keyword", "Position", "Snippet"};
        List<String[]> rows = new ArrayList<>();
        for (var h : hits) {
            rows.add(new String[]{h.getKeyword().toUpperCase(), "[" + h.getStartIndex() + ".." + h.getEndIndex() + "]", h.getSurroundingSnippet()});
        }
        TerminalIO.printTable(headers, rows);
        TerminalIO.pressEnterToContinue();
    }

    // --- M2: SUFFIX INTELLIGENCE ---
    public static void showSuffixIntelligenceMenu() {
        DataStore ds = DataStore.getInstance();
        boolean back = false;

        while (!back) {
            TerminalIO.printHeader("M2: SPORTS DOCUMENT INTELLIGENCE & SUFFIX STRUCTURES");
            TerminalIO.println("  [1] Index Match Dossier with Suffix Array (Binary Substring Search)");
            TerminalIO.println("  [2] Compare Suffix Array Construction: Standard vs SA-IS (O(N) Linear)");
            TerminalIO.println("  [3] Cross-Report Similarity: Longest Common Tactical Phrase (Kasai LCP)");
            TerminalIO.println("  [4] Explicit Suffix Tree Substring Queries & Edge Traversals");
            TerminalIO.println("  [5] Suffix Automaton (DAWG Distinct Substrings & Repeated Buzzwords)");
            TerminalIO.println("  [0] Return to Main Dashboard");

            int choice = TerminalIO.readInt("Select Option", 0, 5);
            switch (choice) {
                case 1 -> runSuffixArraySearch(ds);
                case 2 -> compareSaIs(ds);
                case 3 -> runKasaiReportSimilarity(ds);
                case 4 -> runSuffixTree(ds);
                case 5 -> runSuffixAutomaton(ds);
                case 0 -> back = true;
            }
        }
    }

    private static void runSuffixArraySearch(DataStore ds) {
        TerminalIO.printSubHeader("Suffix Array Substring Query");
        String text = ds.getDossier("DOS-01").getFullContent();
        StandardSuffixArray ssa = new StandardSuffixArray(text);
        String query = TerminalIO.readNonEmptyString("Query substring (e.g. 'pressing' or 'counter')");

        List<Integer> matches = ssa.searchSubstring(query);
        TerminalIO.println(String.format("Found %d occurrences of '%s' at offsets: %s", matches.size(), query, matches));
        TerminalIO.pressEnterToContinue();
    }

    private static void compareSaIs(DataStore ds) {
        TerminalIO.printSubHeader("Suffix Array vs SA-IS Benchmark Comparison");
        String text = ds.getDossier("DOS-01").getFullContent();
        // Repeat text to make it substantial for benchmarking
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append(text).append(" ");
        String benchmarkText = sb.toString();

        StandardSuffixArray ssa = new StandardSuffixArray(benchmarkText);
        InducedSortingSuffixArray sais = new InducedSortingSuffixArray(benchmarkText);

        boolean equivalent = Arrays.equals(ssa.getSuffixArray(), sais.getSuffixArray());
        TerminalIO.println(String.format("Equivalence Check: %s", equivalent ? "IDENTICAL SUFFIX ARRAYS [PASS]" : "MISMATCH"));
        TerminalIO.println(String.format("Standard Suffix Array Construction: %.2f µs", ssa.getConstructionTimeNs() / 1000.0));
        TerminalIO.println(String.format("SA-IS Linear Time O(N) Construction: %.2f µs", sais.getConstructionTimeNs() / 1000.0));
        TerminalIO.println(String.format("Speedup: %.2fx faster", (double) ssa.getConstructionTimeNs() / Math.max(1, sais.getConstructionTimeNs())));
        TerminalIO.pressEnterToContinue();
    }

    private static void runKasaiReportSimilarity(DataStore ds) {
        TerminalIO.printSubHeader("Common Tactical Phrase Detection via Kasai LCP");
        MatchDossier d1 = ds.getDossier("DOS-01");
        MatchDossier d2 = ds.getDossier("DOS-02");

        var result = KasaiLcpEvaluator.findLongestCommonPhrase(d1.getTitle(), d1.getFullContent(), d2.getTitle(), d2.getFullContent());
        TerminalIO.println(result.toString());
        TerminalIO.pressEnterToContinue();
    }

    private static void runSuffixTree(DataStore ds) {
        TerminalIO.printSubHeader("Explicit Suffix Tree Substring Queries");
        String text = ds.getDossier("DOS-03").getFullContent();
        ExplicitSuffixTree tree = new ExplicitSuffixTree(text);

        String query = TerminalIO.readNonEmptyString("Query substring (e.g. 'spatial' or 'pressing')");
        boolean exists = tree.containsSubstring(query);
        List<Integer> occs = tree.findOccurrences(query);

        TerminalIO.println(String.format("Query: '%s' | Exists: %s | Occurrences: %d at offsets %s",
                query, exists ? "YES" : "NO", occs.size(), occs));
        TerminalIO.pressEnterToContinue();
    }

    private static void runSuffixAutomaton(DataStore ds) {
        TerminalIO.printSubHeader("Suffix Automaton (DAWG) Analysis");
        String text = ds.getDossier("DOS-01").getFullContent();
        DirectedAcyclicWordGraph dawg = new DirectedAcyclicWordGraph(text);

        TerminalIO.println("Automaton Stats:");
        TerminalIO.println("  Total States:            " + dawg.getStateCount());
        TerminalIO.println("  Distinct Substrings:     " + dawg.countDistinctSubstrings());
        TerminalIO.println("  Longest Repeated Phrase: \"" + dawg.findLongestRepeatedSubstring() + "\"");
        TerminalIO.pressEnterToContinue();
    }

    // --- M3: DYNAMIC PROGRAMMING ---
    public static void showDpMenu() {
        DataStore ds = DataStore.getInstance();
        boolean back = false;

        while (!back) {
            TerminalIO.printHeader("M3: SQUAD OPTIMIZATION & DYNAMIC PROGRAMMING");
            TerminalIO.println("  [1] Athlete & Team Name Typo Correction (Levenshtein vs Damerau)");
            TerminalIO.println("  [2] Constrained Squad Lineup Generation (Bitmask DP)");
            TerminalIO.println("  [3] Maximum Tactical Synergy Lineup (DP on Subsets)");
            TerminalIO.println("  [4] Analytics Pipeline Optimizer (Matrix Chain Multiplication)");
            TerminalIO.println("  [0] Return to Main Dashboard");

            int choice = TerminalIO.readInt("Select Option", 0, 4);
            switch (choice) {
                case 1 -> runTypoCorrection(ds);
                case 2 -> runBitmaskSquad(ds);
                case 3 -> runSubsetSynergy(ds);
                case 4 -> runMatrixChain();
                case 0 -> back = true;
            }
        }
    }

    private static void runTypoCorrection(DataStore ds) {
        TerminalIO.printSubHeader("Name Typo Correction & Distance Comparison");
        String query = TerminalIO.readNonEmptyString("Enter misspelled name (e.g. 'Ronalod', 'Mbappo', 'Keviin')");

        List<String> names = new ArrayList<>();
        for (AthleteProfile a : ds.getAllAthletes()) {
            names.add(a.getName());
            String[] tokens = a.getName().split("\\s+");
            for (String t : tokens) {
                if (t.length() > 2 && !names.contains(t)) {
                    names.add(t);
                }
            }
        }
        for (var f : ds.getAllFranchises()) {
            names.add(f.getName());
            String[] tokens = f.getName().split("\\s+");
            for (String t : tokens) {
                if (t.length() > 2 && !names.contains(t)) {
                    names.add(t);
                }
            }
        }

        String closest = EditDistanceEngine.findClosestEntity(query, names);
        if (closest != null) {
            var comp = EditDistanceEngine.compare(query, closest);
            TerminalIO.println("\n" + comp);
        } else {
            TerminalIO.printError("No candidate names available.");
        }
        TerminalIO.pressEnterToContinue();
    }

    private static void runBitmaskSquad(DataStore ds) {
        TerminalIO.printSubHeader("Bitmask DP Constrained Squad Selection");
        List<AthleteProfile> candidates = ds.getAllAthletes();
        TerminalIO.println("Evaluating all 2^N combinations for 5-a-side roster:");
        TerminalIO.println("  Rules: 1 GK, 1 DEF, 2 MID, 1 FWD, Max 1 low-fitness player (< 85)");

        var constraint = BitmaskRosterSelector.SquadConstraint.defaultFiveAside();
        var rosters = BitmaskRosterSelector.findValidRosters(candidates, constraint);

        TerminalIO.println(String.format("Found %d valid tactical lineups.\nTop 3 Lineups:", rosters.size()));
        for (int i = 0; i < Math.min(3, rosters.size()); i++) {
            var r = rosters.get(i);
            TerminalIO.println(String.format("  Lineup #%d: Rating: %.1f | Avg Fit: %.1f | Low-Fit: %d",
                    i + 1, r.getTotalRating(), r.getAverageFitness(), r.getLowFitnessCount()));
            for (AthleteProfile a : r.getAthletes()) {
                TerminalIO.println(String.format("     - [%s] %s (%s, Rating: %.1f)", a.getId(), a.getName(), a.getPrimaryPosition().getCode(), a.getOverallRating()));
            }
        }
        TerminalIO.pressEnterToContinue();
    }

    private static void runSubsetSynergy(DataStore ds) {
        TerminalIO.printSubHeader("DP on Subsets: Tactical Chemistry Maximization");
        List<AthleteProfile> pool = ds.getAllAthletes();
        int target = TerminalIO.readInt("Target Starting Squad Size", 3, 7);

        var result = SubsetSynergyOptimizer.optimizeLineup(pool, target);
        TerminalIO.println("\n" + result);
        TerminalIO.println("Optimized Starting Squad Members:");
        for (AthleteProfile a : result.getSelectedSquad()) {
            TerminalIO.println(String.format("  * [%s] %s | %s | Readiness: %.1f | Team: %s",
                    a.getId(), a.getName(), a.getPrimaryPosition().getCode(), a.getEffectiveReadiness(), a.getFranchiseId()));
        }
        TerminalIO.pressEnterToContinue();
    }

    private static void runMatrixChain() {
        TerminalIO.printSubHeader("Analytics Pipeline Matrix Chain Multiplication");
        var res = MatrixChainAnalyticsOptimizer.optimizeDefaultSportsPipeline();
        TerminalIO.println(res.toString());
        TerminalIO.pressEnterToContinue();
    }

    // --- M4: NETWORK FLOW ---
    public static void showNetworkFlowMenu() {
        boolean back = false;
        while (!back) {
            TerminalIO.printHeader("M4: SPORTS RESOURCE ALLOCATION & NETWORK FLOW");
            TerminalIO.println("  [1] Resource Allocation Flow Network (Ford-Fulkerson vs Edmonds-Karp vs Dinic)");
            TerminalIO.println("  [2] Player-to-Position Assignment (Bipartite Matching)");
            TerminalIO.println("  [3] Position Conflict Analysis (König's Theorem Verification)");
            TerminalIO.println("  [0] Return to Main Dashboard");

            int choice = TerminalIO.readInt("Select Option", 0, 3);
            switch (choice) {
                case 1 -> runFlowComparison();
                case 2 -> runBipartiteAssignment();
                case 3 -> runKonigAnalysis();
                case 0 -> back = true;
            }
        }
    }

    private static void runFlowComparison() {
        TerminalIO.printSubHeader("Resource Flow Network Solvers Comparison");
        // Models: Source -> Training Facilities -> Fitness Stations -> Athletes -> Sink
        ResidualFlowNetwork net1 = createSportsResourceNetwork();
        ResidualFlowNetwork net2 = createSportsResourceNetwork();
        ResidualFlowNetwork net3 = createSportsResourceNetwork();

        var ff = FordFulkersonDfsSolver.solve(net1, 0, 7);
        var ek = EdmondsKarpBfsSolver.solve(net2, 0, 7);
        var dinic = DinicBlockingFlowSolver.solve(net3, 0, 7);

        String[] headers = new String[]{"Algorithm", "Max Flow", "Augmentations/Phases", "Operations", "Time (µs)"};
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{ff.algorithm, String.valueOf(ff.maxFlow), String.valueOf(ff.augmentingPaths), String.valueOf(ff.operations), String.format("%.2f", ff.executionTimeNs / 1000.0)});
        rows.add(new String[]{ek.algorithm, String.valueOf(ek.maxFlow), String.valueOf(ek.augmentingPaths), String.valueOf(ek.operations), String.format("%.2f", ek.executionTimeNs / 1000.0)});
        rows.add(new String[]{dinic.algorithm, String.valueOf(dinic.maxFlow), String.valueOf(dinic.augmentingPaths), String.valueOf(dinic.operations), String.format("%.2f", dinic.executionTimeNs / 1000.0)});

        TerminalIO.printTable(headers, rows);
        TerminalIO.pressEnterToContinue();
    }

    private static ResidualFlowNetwork createSportsResourceNetwork() {
        ResidualFlowNetwork net = new ResidualFlowNetwork(8);
        // 0: Source, 1: Pitch, 2: Gym, 3: HydroPool, 4: GroupA, 5: GroupB, 6: GroupC, 7: Sink
        net.addEdge(0, 1, 20);
        net.addEdge(0, 2, 15);
        net.addEdge(0, 3, 10);
        net.addEdge(1, 4, 12);
        net.addEdge(1, 5, 8);
        net.addEdge(2, 4, 6);
        net.addEdge(2, 6, 9);
        net.addEdge(3, 5, 5);
        net.addEdge(3, 6, 5);
        net.addEdge(4, 7, 15);
        net.addEdge(5, 7, 12);
        net.addEdge(6, 7, 14);
        return net;
    }

    private static void runBipartiteAssignment() {
        TerminalIO.printSubHeader("Player-to-Position Assignment Engine");
        List<String> players = Arrays.asList("Leo Sterling", "Kevin De Vries", "Virgil Van Horn", "Gabriel Martinelli", "Rodri Hernandez");
        List<String> positions = Arrays.asList("Forward", "Midfielder (Central)", "Midfielder (Defensive)", "Defender (Centre-Back)", "Winger (Left)");

        Map<String, List<String>> graph = new HashMap<>();
        graph.put("Leo Sterling", Arrays.asList("Forward", "Winger (Left)"));
        graph.put("Kevin De Vries", Arrays.asList("Midfielder (Central)", "Forward"));
        graph.put("Virgil Van Horn", Collections.singletonList("Defender (Centre-Back)"));
        graph.put("Gabriel Martinelli", Arrays.asList("Winger (Left)", "Forward"));
        graph.put("Rodri Hernandez", Arrays.asList("Midfielder (Defensive)", "Defender (Centre-Back)"));

        var result = BipartiteAssignmentEngine.solve(players, positions, graph);
        TerminalIO.println(result.toString());
        TerminalIO.pressEnterToContinue();
    }

    private static void runKonigAnalysis() {
        TerminalIO.printSubHeader("Position Conflict Analysis via König's Theorem");
        List<String> players = Arrays.asList("Player1", "Player2", "Player3", "Player4");
        List<String> slots = Arrays.asList("SlotA", "SlotB", "SlotC", "SlotD");

        Map<String, List<String>> adj = new HashMap<>();
        adj.put("Player1", Arrays.asList("SlotA", "SlotB"));
        adj.put("Player2", Collections.singletonList("SlotA"));
        adj.put("Player3", Arrays.asList("SlotB", "SlotC"));
        adj.put("Player4", Collections.singletonList("SlotC"));

        var report = KonigTheoremAnalyzer.analyze(players, slots, adj);
        TerminalIO.println(report.toString());
        TerminalIO.pressEnterToContinue();
    }

    // --- M5: NP CONSTRAINT LAB ---
    public static void showNpLabMenu() {
        boolean back = false;
        while (!back) {
            TerminalIO.printHeader("M5: TEAM SELECTION CONSTRAINT LAB (NP-COMPLETENESS)");
            TerminalIO.println("  [1] Boolean SAT Squad Rule Solver (DPLL Backtracker)");
            TerminalIO.println("  [2] 3-SAT to CLIQUE Karp Reduction Gadget");
            TerminalIO.println("  [3] End-to-End Reduction Pipeline (3-SAT -> CLIQUE -> IS -> VC)");
            TerminalIO.println("  [4] Player Conflict Coverage: 2-Approximation vs Exact Optimal");
            TerminalIO.println("  [0] Return to Main Dashboard");

            int choice = TerminalIO.readInt("Select Option", 0, 4);
            switch (choice) {
                case 1 -> runSatSolver();
                case 2 -> runThreeSatToClique();
                case 3 -> runReductionPipeline();
                case 4 -> runVertexCoverApprox();
                case 0 -> back = true;
            }
        }
    }

    private static void runSatSolver() {
        TerminalIO.printSubHeader("Boolean SAT Squad Selection Solver");
        TerminalIO.println("Rule 1: (PlaySterling OR PlayDeVries)");
        TerminalIO.println("Rule 2: (~PlaySterling OR PlayModric)");
        TerminalIO.println("Rule 3: (~PlayDeVries OR ~PlayModric OR PlayRudiger)");

        var c1 = new BooleanSatSolver.Clause(new BooleanSatSolver.Literal("PlaySterling", true), new BooleanSatSolver.Literal("PlayDeVries", true));
        var c2 = new BooleanSatSolver.Clause(new BooleanSatSolver.Literal("PlaySterling", false), new BooleanSatSolver.Literal("PlayModric", true));
        var c3 = new BooleanSatSolver.Clause(new BooleanSatSolver.Literal("PlayDeVries", false), new BooleanSatSolver.Literal("PlayModric", false), new BooleanSatSolver.Literal("PlayRudiger", true));

        var res = BooleanSatSolver.solve(Arrays.asList(c1, c2, c3));
        TerminalIO.println("\n" + res);
        TerminalIO.pressEnterToContinue();
    }

    private static void runThreeSatToClique() {
        TerminalIO.printSubHeader("3-SAT to CLIQUE Karp Reduction");
        List<BooleanSatSolver.Clause> clauses = Arrays.asList(
                new BooleanSatSolver.Clause(new BooleanSatSolver.Literal("x1", true), new BooleanSatSolver.Literal("x2", true), new BooleanSatSolver.Literal("x3", true)),
                new BooleanSatSolver.Clause(new BooleanSatSolver.Literal("x1", false), new BooleanSatSolver.Literal("x2", false), new BooleanSatSolver.Literal("x3", true)),
                new BooleanSatSolver.Clause(new BooleanSatSolver.Literal("x1", true), new BooleanSatSolver.Literal("x2", false), new BooleanSatSolver.Literal("x3", false))
        );

        var res = ThreeSatToCliqueReduction.reduceAndSolve(clauses);
        TerminalIO.println(res.toString());
        TerminalIO.pressEnterToContinue();
    }

    private static void runReductionPipeline() {
        TerminalIO.printSubHeader("3-SAT -> CLIQUE -> INDEPENDENT SET -> VERTEX COVER Pipeline");
        List<BooleanSatSolver.Clause> clauses = Arrays.asList(
                new BooleanSatSolver.Clause(new BooleanSatSolver.Literal("a", true), new BooleanSatSolver.Literal("b", true), new BooleanSatSolver.Literal("c", true)),
                new BooleanSatSolver.Clause(new BooleanSatSolver.Literal("a", false), new BooleanSatSolver.Literal("b", true), new BooleanSatSolver.Literal("c", false))
        );

        var pipelineRes = NpHardReductionsPipeline.executePipeline(clauses);
        TerminalIO.println(pipelineRes.toString());
        TerminalIO.pressEnterToContinue();
    }

    private static void runVertexCoverApprox() {
        TerminalIO.printSubHeader("Player Conflict Coverage: 2-Approximation vs Exact Optimal");
        List<String> players = Arrays.asList("Sterling", "DeVries", "Rudiger", "Mbappo", "Kane", "Rice");
        List<VertexCoverApproximator.Edge> conflicts = Arrays.asList(
                new VertexCoverApproximator.Edge("Sterling", "Rudiger"),
                new VertexCoverApproximator.Edge("DeVries", "Rudiger"),
                new VertexCoverApproximator.Edge("Rudiger", "Mbappo"),
                new VertexCoverApproximator.Edge("Kane", "Rice"),
                new VertexCoverApproximator.Edge("Mbappo", "Kane")
        );

        var comp = VertexCoverApproximator.compare(players, conflicts);
        TerminalIO.println(comp.toString());
        TerminalIO.pressEnterToContinue();
    }

    // --- M6: RANDOMIZED & PARALLEL ---
    public static void showRandomizedParallelMenu() {
        DataStore ds = DataStore.getInstance();
        boolean back = false;

        while (!back) {
            TerminalIO.printHeader("M6: RANDOMIZED & PARALLEL ALGORITHMS");
            TerminalIO.println("  [1] Player Ranking via Stochastic QuickSort (Randomized Pivot)");
            TerminalIO.println("  [2] Continuous Match-Event Stream Reservoir Sampling");
            TerminalIO.println("  [3] Miller-Rabin Probabilistic Primality Demonstration");
            TerminalIO.println("  [4] Blelloch Work-Efficient Parallel Prefix Scan");
            TerminalIO.println("  [5] Multi-Threaded ForkJoin Parallel Reduce vs Sequential");
            TerminalIO.println("  [6] Brent's Scheduling Theorem Interactive Calculator");
            TerminalIO.println("  [0] Return to Main Dashboard");

            int choice = TerminalIO.readInt("Select Option", 0, 6);
            switch (choice) {
                case 1 -> runStochasticSort(ds);
                case 2 -> runReservoirSampling(ds);
                case 3 -> runMillerRabin();
                case 4 -> runBlellochScan();
                case 5 -> runForkJoinReduce();
                case 6 -> runBrentCalculator();
                case 0 -> back = true;
            }
        }
    }

    private static void runStochasticSort(DataStore ds) {
        TerminalIO.printSubHeader("Stochastic QuickSort Player Ranking");
        List<AthleteProfile> athletes = new ArrayList<>(ds.getAllAthletes());

        TerminalIO.println("Sort Athletes By: 1.Rating  2.Fitness  3.Goals  4.Readiness");
        int crit = TerminalIO.readInt("Select Criterion", 1, 4);

        Comparator<AthleteProfile> comp = switch (crit) {
            case 1 -> (a, b) -> Double.compare(b.getOverallRating(), a.getOverallRating());
            case 2 -> (a, b) -> Double.compare(b.getFitnessScore(), a.getFitnessScore());
            case 3 -> (a, b) -> Integer.compare(b.getGoals(), a.getGoals());
            default -> (a, b) -> Double.compare(b.getEffectiveReadiness(), a.getEffectiveReadiness());
        };

        var metrics = StochasticQuickSort.sort(athletes, comp);
        TerminalIO.println(String.format("Sorted %d athletes in %.3f µs (%d comparisons, %d swaps)",
                athletes.size(), metrics.executionTimeNs / 1000.0, metrics.comparisons, metrics.swaps));

        AthleteConsoleController.listAthletes(athletes);
    }

    private static void runReservoirSampling(DataStore ds) {
        TerminalIO.printSubHeader("Match-Event Stream Reservoir Sampling");
        int k = TerminalIO.readInt("Sample Reservoir Size (K)", 3, 10);
        StreamReservoirSampler<String> sampler = new StreamReservoirSampler<>(k);

        // Feed events from fixtures
        for (var f : ds.getAllFixtures()) {
            for (var inc : f.getIncidents()) {
                sampler.feed(inc.toString());
            }
        }

        TerminalIO.println(String.format("\nIngested %d live match events. Maintained sample of %d events:",
                sampler.getTotalItemsObserved(), sampler.getSample().size()));
        TerminalIO.println(String.format("Theoretical Inclusion Probability per Event: %.2f%%\n",
                sampler.getInclusionProbability() * 100.0));

        for (int i = 0; i < sampler.getSample().size(); i++) {
            TerminalIO.println(String.format("  Sample [%d]: %s", i + 1, sampler.getSample().get(i)));
        }
        TerminalIO.pressEnterToContinue();
    }

    private static void runMillerRabin() {
        TerminalIO.printSubHeader("Miller-Rabin Probabilistic Primality Verifier");
        String raw = TerminalIO.readString("Enter integer to test (or enter to test default 1000000007)");
        BigInteger num = raw.isEmpty() ? BigInteger.valueOf(1_000_000_007L) : new BigInteger(raw);
        int rounds = TerminalIO.readInt("Witness Verification Rounds", 5, 50);

        var rep = MillerRabinPrimalityVerifier.testPrimality(num, rounds);
        TerminalIO.println("\n" + rep);
        TerminalIO.pressEnterToContinue();
    }

    private static void runBlellochScan() {
        TerminalIO.printSubHeader("Blelloch Work-Efficient Parallel Prefix Scan");
        long[] telemetry = new long[]{12, 25, 18, 34, 40, 15, 22, 28};
        var res = BlellochPrefixScanner.scan(telemetry);
        TerminalIO.println("\n" + res);
        TerminalIO.pressEnterToContinue();
    }

    private static void runForkJoinReduce() {
        TerminalIO.printSubHeader("Multi-Threaded ForkJoin Parallel Reduction");
        int count = TerminalIO.readInt("Dataset Size (elements)", 10000, 500000);
        long[] data = new long[count];
        for (int i = 0; i < count; i++) data[i] = (i % 25) + 1;

        var comp = ForkJoinParallelReducer.compare(data);
        TerminalIO.println("\n" + comp);
        TerminalIO.pressEnterToContinue();
    }

    private static void runBrentCalculator() {
        TerminalIO.printSubHeader("Brent's Scheduling Theorem Calculator");
        long work = TerminalIO.readInt("Total Serial Work W (operations)", 1000, 10000000);
        long span = TerminalIO.readInt("Critical Path Span S (sequential steps)", 1, (int) Math.min(work, 10000));
        int p = TerminalIO.readInt("Number of Processors P", 1, 128);

        var report = BrentTheoremWorkSpanModeler.analyze(work, span, p);
        TerminalIO.println("\n" + report);
        TerminalIO.pressEnterToContinue();
    }
}
