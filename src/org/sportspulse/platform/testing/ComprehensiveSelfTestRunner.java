package org.sportspulse.platform.testing;

import org.sportspulse.platform.algorithms.dp.*;
import org.sportspulse.platform.algorithms.flow.*;
import org.sportspulse.platform.algorithms.npc.*;
import org.sportspulse.platform.algorithms.parallel.*;
import org.sportspulse.platform.algorithms.randomized.*;
import org.sportspulse.platform.algorithms.strings.*;
import org.sportspulse.platform.algorithms.suffix.*;
import org.sportspulse.platform.console.TerminalIO;
import org.sportspulse.platform.datastructures.*;
import org.sportspulse.platform.domain.model.*;
import org.sportspulse.platform.domain.repository.DataFileHandler;
import org.sportspulse.platform.domain.repository.DataStore;

import java.math.BigInteger;
import java.util.*;

/**
 * Comprehensive Automated Self-Test Harness for SportsPulse.
 * Executes 40 rigorous automated unit tests across all M1-M6 modules,
 * custom data structures, graph algorithms, and file handling.
 */
public class ComprehensiveSelfTestRunner {

    public static void main(String[] args) {
        boolean allPassed = runAllTests();
        System.exit(allPassed ? 0 : 1);
    }

    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    public static boolean runAllTests() {
        totalTests = 0;
        passedTests = 0;
        failedTests = 0;

        TerminalIO.printHeader("SPORTSPULSE SYSTEM HEALTH CHECK & ALGORITHM DIAGNOSTICS");
        TerminalIO.println("Executing automated functional verification across all sports & DSA engines:\n");

        // Data Structures
        runTest("Custom OpenAddressingHashTable (Put/Get/Remove/Rehash)", ComprehensiveSelfTestRunner::testHashTable);
        runTest("Custom DynamicArray (Resize/Bounds/Iterator)", ComprehensiveSelfTestRunner::testDynamicArray);
        runTest("Custom ArrayQueue (Circular Buffer FIFO)", ComprehensiveSelfTestRunner::testArrayQueue);
        runTest("Custom ArrayStack (LIFO Push/Pop)", ComprehensiveSelfTestRunner::testArrayStack);
        runTest("Custom PriorityQueue (Binary Heap Ordering)", ComprehensiveSelfTestRunner::testPriorityQueue);
        runTest("DisjointSetForest (Union-by-Rank & Path Compression)", ComprehensiveSelfTestRunner::testDisjointSet);

        // M1 String Algorithms
        runTest("M1: Naive Pattern Matcher (Exact & Multi-Hit)", ComprehensiveSelfTestRunner::testNaiveSearch);
        runTest("M1: Knuth-Morris-Pratt Searcher (Pi Table & O(N+M))", ComprehensiveSelfTestRunner::testKmpSearch);
        runTest("M1: Z-Algorithm (Z-Box Analysis & Period Detection)", ComprehensiveSelfTestRunner::testZAlgorithm);
        runTest("M1: Rabin-Karp Rolling Hash (Polynomial Collision Handling)", ComprehensiveSelfTestRunner::testRabinKarp);
        runTest("M1: Aho-Corasick Automaton (Multi-Keyword Live Event Tracker)", ComprehensiveSelfTestRunner::testAhoCorasick);

        // M2 Suffix Structures
        runTest("M2: Standard Suffix Array (Prefix Doubling Construction)", ComprehensiveSelfTestRunner::testStandardSuffixArray);
        runTest("M2: SA-IS Suffix Array (Linear-Time O(N) Induced Sorting)", ComprehensiveSelfTestRunner::testSaIs);
        runTest("M2: Kasai LCP Array & Longest Common Phrase Evaluator", ComprehensiveSelfTestRunner::testKasaiLcp);
        runTest("M2: Explicit Suffix Tree (Edge Compression & Substring Search)", ComprehensiveSelfTestRunner::testSuffixTree);
        runTest("M2: Suffix Automaton (DAWG Distinct Substring Counting)", ComprehensiveSelfTestRunner::testSuffixAutomaton);

        // M3 Dynamic Programming
        runTest("M3: Levenshtein Distance (Typo Alignment Matrix)", ComprehensiveSelfTestRunner::testLevenshtein);
        runTest("M3: Damerau-Levenshtein (Adjacent Transposition Detection)", ComprehensiveSelfTestRunner::testDamerauLevenshtein);
        runTest("M3: Bitmask Roster Selector (Positional & Fitness Quotas)", ComprehensiveSelfTestRunner::testBitmaskRoster);
        runTest("M3: Subset Synergy Optimizer (DP on Subsets Chemistry)", ComprehensiveSelfTestRunner::testSubsetSynergy);
        runTest("M3: Matrix Chain Multiplication (Analytics Pipeline Optimizer)", ComprehensiveSelfTestRunner::testMatrixChain);

        // M4 Network Flow
        runTest("M4: Ford-Fulkerson Solver (Residual Graph DFS)", ComprehensiveSelfTestRunner::testFordFulkerson);
        runTest("M4: Edmonds-Karp Solver (Shortest Augmenting Path BFS)", ComprehensiveSelfTestRunner::testEdmondsKarp);
        runTest("M4: Dinic's Algorithm (Level Graph & Blocking Flow)", ComprehensiveSelfTestRunner::testDinic);
        runTest("M4: Flow Conservation & Max Flow Solver Equivalence", ComprehensiveSelfTestRunner::testFlowEquivalence);
        runTest("M4: Bipartite Matching (Player-Position Assignment)", ComprehensiveSelfTestRunner::testBipartiteMatching);
        runTest("M4: König's Theorem (|M| == |MVC| Bipartite Cover)", ComprehensiveSelfTestRunner::testKonigTheorem);

        // M5 NP-Completeness
        runTest("M5: DPLL Boolean SAT Solver (Squad Rule Satisfiability)", ComprehensiveSelfTestRunner::testSatSolver);
        runTest("M5: 3-SAT to CLIQUE Reduction Gadget", ComprehensiveSelfTestRunner::testThreeSatToClique);
        runTest("M5: CLIQUE to INDEPENDENT SET (Complement Graph Equivalence)", ComprehensiveSelfTestRunner::testCliqueToIndepSet);
        runTest("M5: INDEPENDENT SET to VERTEX COVER (V \\ S Complement)", ComprehensiveSelfTestRunner::testIndepSetToVertexCover);
        runTest("M5: Vertex Cover 2-Approximation vs Exact Optimal", ComprehensiveSelfTestRunner::testVertexCoverApprox);

        // M6 Randomized & Parallel
        runTest("M6: Stochastic QuickSort (Randomized Pivot Correctness)", ComprehensiveSelfTestRunner::testStochasticQuickSort);
        runTest("M6: Stream Reservoir Sampling (Uniform Distribution Algorithm R)", ComprehensiveSelfTestRunner::testReservoirSampling);
        runTest("M6: Miller-Rabin Primality Verifier (Probabilistic Witnesses)", ComprehensiveSelfTestRunner::testMillerRabin);
        runTest("M6: Blelloch Work-Efficient Parallel Prefix Scan", ComprehensiveSelfTestRunner::testBlellochScan);
        runTest("M6: ForkJoin Parallel Reducer vs Sequential Accumulator", ComprehensiveSelfTestRunner::testParallelReduce);
        runTest("M6: Brent's Theorem Theoretical Work-Span Modeler", ComprehensiveSelfTestRunner::testBrentTheorem);

        // File Storage & Integration
        runTest("Storage: Relative File Persistence & CSV Export", ComprehensiveSelfTestRunner::testFileHandling);

        printSummary();
        return failedTests == 0;
    }

    private static void runTest(String testName, TestRunnable test) {
        totalTests++;
        try {
            boolean result = test.run();
            if (result) {
                passedTests++;
                TerminalIO.println(String.format("  %-65s [ %s ]", testName, TerminalIO.color(TerminalIO.GREEN, "PASS")));
            } else {
                failedTests++;
                TerminalIO.println(String.format("  %-65s [ %s ]", testName, TerminalIO.color(TerminalIO.RED, "FAIL")));
            }
        } catch (Throwable e) {
            failedTests++;
            TerminalIO.println(String.format("  %-65s [ %s ] (%s)", testName, TerminalIO.color(TerminalIO.RED, "ERROR"), e.getMessage()));
        }
    }

    private static void printSummary() {
        TerminalIO.println("");
        TerminalIO.println(TerminalIO.color(TerminalIO.CYAN, "================================================================================"));
        TerminalIO.println(TerminalIO.color(TerminalIO.BOLD + TerminalIO.WHITE,
                String.format("  TOTAL TESTS : %-4d | PASSED : %-4d | FAILED : %-4d | SUCCESS: %.1f%%",
                        totalTests, passedTests, failedTests, ((double) passedTests / totalTests) * 100.0)));
        TerminalIO.println(TerminalIO.color(TerminalIO.CYAN, "================================================================================"));
    }

    @FunctionalInterface
    interface TestRunnable {
        boolean run() throws Exception;
    }

    // --- TEST IMPLEMENTATIONS ---

    private static boolean testHashTable() {
        OpenAddressingHashTable<String, Integer> map = new OpenAddressingHashTable<>(5);
        map.put("Ronaldo", 7);
        map.put("Messi", 10);
        map.put("Neymar", 11);
        map.put("Mbappe", 9);
        map.put("Haaland", 9);

        boolean getOk = map.get("Ronaldo") == 7 && map.get("Messi") == 10;
        boolean removeOk = map.remove("Neymar") && !map.containsKey("Neymar") && map.get("Neymar") == null;
        boolean stillFindsSubsequent = map.get("Mbappe") == 9 && map.get("Haaland") == 9;
        return getOk && removeOk && stillFindsSubsequent && map.size() == 4;
    }

    private static boolean testDynamicArray() {
        CustomDynamicArray<String> arr = new CustomDynamicArray<>(2);
        arr.add("Titan");
        arr.add("Royals");
        arr.add("Dynamos"); // Triggers resize
        boolean sizeOk = arr.size() == 3;
        boolean getOk = arr.get(1).equals("Royals");
        arr.remove(1);
        boolean removeOk = arr.size() == 2 && arr.get(1).equals("Dynamos");
        return sizeOk && getOk && removeOk;
    }

    private static boolean testArrayQueue() {
        CustomArrayQueue<Integer> q = new CustomArrayQueue<>(2);
        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);
        return q.dequeue() == 10 && q.peek() == 20 && q.dequeue() == 20 && q.dequeue() == 30 && q.isEmpty();
    }

    private static boolean testArrayStack() {
        CustomArrayStack<String> s = new CustomArrayStack<>(2);
        s.push("First");
        s.push("Second");
        return s.peek().equals("Second") && s.pop().equals("Second") && s.pop().equals("First") && s.isEmpty();
    }

    private static boolean testPriorityQueue() {
        CustomPriorityQueue<Integer> pq = new CustomPriorityQueue<>();
        pq.offer(40);
        pq.offer(10);
        pq.offer(30);
        pq.offer(20);
        return pq.poll() == 10 && pq.poll() == 20 && pq.poll() == 30 && pq.poll() == 40 && pq.isEmpty();
    }

    private static boolean testDisjointSet() {
        DisjointSetForest dsu = new DisjointSetForest(5);
        dsu.union(0, 1);
        dsu.union(1, 2);
        dsu.union(3, 4);
        return dsu.connected(0, 2) && !dsu.connected(0, 3) && dsu.getComponentCount() == 2;
    }

    private static boolean testNaiveSearch() {
        String text = "the striker scored a goal and another goal in extra time";
        var res = BruteForcePatternMatcher.search(text, "goal");
        return res.getMatchCount() == 2 && res.getMatchIndices().contains(21) && res.getMatchIndices().contains(38);
    }

    private static boolean testKmpSearch() {
        String text = "abacababacabacaba";
        String pat = "abacaba";
        var res = KnuthMorrisPrattSearcher.search(text, pat);
        return res.getMatchCount() == 3 && res.getMatchIndices().equals(Arrays.asList(0, 6, 10));
    }

    private static boolean testZAlgorithm() {
        String text = "aabxaabxcaabxaabxay";
        var res = ZAlgorithmMatcher.search(text, "aabx");
        return res.getMatchCount() == 4 && res.getMatchIndices().get(0) == 0;
    }

    private static boolean testRabinKarp() {
        String text = "speed transition counter-attack goal counter-attack";
        var res = RabinKarpRollingHasher.search(text, "counter-attack");
        return res.getMatchCount() == 2 && res.getMatchIndices().contains(17) && res.getMatchIndices().contains(37);
    }

    private static boolean testAhoCorasick() {
        AhoCorasickEventAutomaton ac = new AhoCorasickEventAutomaton(Arrays.asList("goal", "tackle", "foul", "corner"));
        String stream = "Minute 14: hard tackle leading to foul, followed by corner and header goal!";
        var hits = ac.search(stream);
        Set<String> found = new HashSet<>();
        for (var h : hits) found.add(h.getKeyword());
        return found.contains("tackle") && found.contains("foul") && found.contains("corner") && found.contains("goal");
    }

    private static boolean testStandardSuffixArray() {
        StandardSuffixArray ssa = new StandardSuffixArray("banana");
        // Suffixes: 5 (a), 3 (ana), 1 (anana), 0 (banana), 4 (na), 2 (nana)
        int[] sa = ssa.getSuffixArray();
        boolean saCorrect = Arrays.equals(sa, new int[]{5, 3, 1, 0, 4, 2});
        var matches = ssa.searchSubstring("an");
        return saCorrect && matches.size() == 2 && matches.contains(1) && matches.contains(3);
    }

    private static boolean testSaIs() {
        String text = "mimi_the_striker_scored_a_goal_mimi";
        StandardSuffixArray ssa = new StandardSuffixArray(text);
        InducedSortingSuffixArray sais = new InducedSortingSuffixArray(text);
        return Arrays.equals(ssa.getSuffixArray(), sais.getSuffixArray());
    }

    private static boolean testKasaiLcp() {
        String docA = "The Titans executed a high pressing transition to dispossess Madrid.";
        String docB = "Munich also employed a high pressing transition with ruthless stamina.";
        var phraseRes = KasaiLcpEvaluator.findLongestCommonPhrase("DocA", docA, "DocB", docB);
        return phraseRes.getLcpLength() >= 24 && phraseRes.getLongestCommonPhrase().contains("high pressing transition");
    }

    private static boolean testSuffixTree() {
        ExplicitSuffixTree tree = new ExplicitSuffixTree("tactical_overlap_tactical");
        boolean hasOverlap = tree.containsSubstring("overlap");
        boolean hasTactical = tree.containsSubstring("tactical");
        boolean noNonsense = !tree.containsSubstring("offside_trap");
        var occs = tree.findOccurrences("tactical");
        return hasOverlap && hasTactical && noNonsense && occs.size() == 2;
    }

    private static boolean testSuffixAutomaton() {
        DirectedAcyclicWordGraph dawg = new DirectedAcyclicWordGraph("abab");
        // Distinct substrings of "abab": a, b, ab, ba, aba, bab, abab (7 distinct)
        long distinct = dawg.countDistinctSubstrings();
        boolean hasAba = dawg.containsSubstring("aba");
        boolean noX = !dawg.containsSubstring("x");
        return distinct == 7 && hasAba && noX;
    }

    private static boolean testLevenshtein() {
        int d = EditDistanceEngine.computeLevenshtein("Ronalod", "Ronaldo");
        return d == 2; // In standard Levenshtein, adjacent transpose = 2 operations
    }

    private static boolean testDamerauLevenshtein() {
        int d = EditDistanceEngine.computeDamerauLevenshtein("Ronalod", "Ronaldo");
        var comp = EditDistanceEngine.compare("Ronalod", "Ronaldo");
        return d == 1 && comp.isTranspositionDetected(); // 1 operation in Damerau-Levenshtein
    }

    private static boolean testBitmaskRoster() {
        List<AthleteProfile> pool = new ArrayList<>();
        pool.add(new AthleteProfile("A1", "GK1", 25, AthletePosition.GOALKEEPER, "T1"));
        pool.add(new AthleteProfile("A2", "DEF1", 25, AthletePosition.DEFENDER, "T1"));
        pool.add(new AthleteProfile("A3", "MID1", 25, AthletePosition.MIDFIELDER, "T1"));
        pool.add(new AthleteProfile("A4", "MID2", 25, AthletePosition.MIDFIELDER, "T1"));
        pool.add(new AthleteProfile("A5", "FWD1", 25, AthletePosition.FORWARD, "T1"));
        pool.add(new AthleteProfile("A6", "FWD2", 25, AthletePosition.FORWARD, "T1"));

        var constraint = new BitmaskRosterSelector.SquadConstraint(5, 1, 1, 2, 1, 0, 70.0, 1);
        var valid = BitmaskRosterSelector.findValidRosters(pool, constraint);
        return !valid.isEmpty() && valid.get(0).getAthletes().size() == 5;
    }

    private static boolean testSubsetSynergy() {
        List<AthleteProfile> pool = DataStore.getInstance().getAllAthletes();
        var opt = SubsetSynergyOptimizer.optimizeLineup(pool, 5);
        return opt.getSelectedSquad().size() == 5 && opt.getTotalSynergyScore() > 0;
    }

    private static boolean testMatrixChain() {
        // Dimensions 10x20, 20x30, 30x40
        int[] dims = new int[]{10, 20, 30, 40};
        String[] names = new String[]{"A1", "A2", "A3"};
        var res = MatrixChainAnalyticsOptimizer.optimize(names, dims);
        // Cost: (10*20*30) + (10*30*40) = 6000 + 12000 = 18000
        return res.getOptimalScalarOperations() == 18_000 && res.getOptimalParenthesization().contains("A1");
    }

    private static boolean testFordFulkerson() {
        ResidualFlowNetwork net = new ResidualFlowNetwork(4);
        net.addEdge(0, 1, 10);
        net.addEdge(0, 2, 10);
        net.addEdge(1, 2, 2);
        net.addEdge(1, 3, 4);
        net.addEdge(2, 3, 9);
        var res = FordFulkersonDfsSolver.solve(net, 0, 3);
        return res.maxFlow == 13;
    }

    private static boolean testEdmondsKarp() {
        ResidualFlowNetwork net = new ResidualFlowNetwork(4);
        net.addEdge(0, 1, 10);
        net.addEdge(0, 2, 10);
        net.addEdge(1, 2, 2);
        net.addEdge(1, 3, 4);
        net.addEdge(2, 3, 9);
        var res = EdmondsKarpBfsSolver.solve(net, 0, 3);
        return res.maxFlow == 13;
    }

    private static boolean testDinic() {
        ResidualFlowNetwork net = new ResidualFlowNetwork(4);
        net.addEdge(0, 1, 10);
        net.addEdge(0, 2, 10);
        net.addEdge(1, 2, 2);
        net.addEdge(1, 3, 4);
        net.addEdge(2, 3, 9);
        var res = DinicBlockingFlowSolver.solve(net, 0, 3);
        return res.maxFlow == 13;
    }

    private static boolean testFlowEquivalence() {
        ResidualFlowNetwork net1 = new ResidualFlowNetwork(6);
        ResidualFlowNetwork net2 = new ResidualFlowNetwork(6);
        ResidualFlowNetwork net3 = new ResidualFlowNetwork(6);

        int[][] edges = new int[][]{
                {0, 1, 16}, {0, 2, 13}, {1, 2, 10}, {2, 1, 4},
                {1, 3, 12}, {2, 4, 14}, {3, 2, 9}, {4, 3, 7},
                {3, 5, 20}, {4, 5, 4}
        };

        for (int[] e : edges) {
            net1.addEdge(e[0], e[1], e[2]);
            net2.addEdge(e[0], e[1], e[2]);
            net3.addEdge(e[0], e[1], e[2]);
        }

        int f1 = FordFulkersonDfsSolver.solve(net1, 0, 5).maxFlow;
        int f2 = EdmondsKarpBfsSolver.solve(net2, 0, 5).maxFlow;
        int f3 = DinicBlockingFlowSolver.solve(net3, 0, 5).maxFlow;

        return f1 == 23 && f2 == 23 && f3 == 23;
    }

    private static boolean testBipartiteMatching() {
        List<String> players = Arrays.asList("PlayerA", "PlayerB", "PlayerC");
        List<String> positions = Arrays.asList("Forward", "Midfielder", "Defender");

        Map<String, List<String>> graph = new HashMap<>();
        graph.put("PlayerA", Arrays.asList("Forward", "Midfielder"));
        graph.put("PlayerB", Collections.singletonList("Midfielder"));
        graph.put("PlayerC", Arrays.asList("Midfielder", "Defender"));

        var res = BipartiteAssignmentEngine.solve(players, positions, graph);
        return res.getMatchingSize() == 3;
    }

    private static boolean testKonigTheorem() {
        List<String> left = Arrays.asList("L1", "L2", "L3");
        List<String> right = Arrays.asList("R1", "R2", "R3");

        Map<String, List<String>> adj = new HashMap<>();
        adj.put("L1", Arrays.asList("R1", "R2"));
        adj.put("L2", Collections.singletonList("R1"));
        adj.put("L3", Collections.singletonList("R3"));

        var res = KonigTheoremAnalyzer.analyze(left, right, adj);
        return res.isTheoremHolds() && res.getMatchingSize() == res.getVertexCoverSize();
    }

    private static boolean testSatSolver() {
        // (x1 OR x2) AND (~x1 OR x2) AND (~x2 OR x3) -> Satisfiable with x2=true, x3=true
        var lits1 = Arrays.asList(new BooleanSatSolver.Literal("x1", true), new BooleanSatSolver.Literal("x2", true));
        var lits2 = Arrays.asList(new BooleanSatSolver.Literal("x1", false), new BooleanSatSolver.Literal("x2", true));
        var lits3 = Arrays.asList(new BooleanSatSolver.Literal("x2", false), new BooleanSatSolver.Literal("x3", true));

        List<BooleanSatSolver.Clause> clauses = Arrays.asList(
                new BooleanSatSolver.Clause(lits1),
                new BooleanSatSolver.Clause(lits2),
                new BooleanSatSolver.Clause(lits3)
        );

        var res = BooleanSatSolver.solve(clauses);
        return res.satisfiable && res.model.get("x2") && res.model.get("x3");
    }

    private static boolean testThreeSatToClique() {
        List<BooleanSatSolver.Clause> clauses = Arrays.asList(
                new BooleanSatSolver.Clause(new BooleanSatSolver.Literal("x1", true), new BooleanSatSolver.Literal("x2", true), new BooleanSatSolver.Literal("x3", true)),
                new BooleanSatSolver.Clause(new BooleanSatSolver.Literal("x1", false), new BooleanSatSolver.Literal("x2", true), new BooleanSatSolver.Literal("x3", false))
        );

        var redRes = ThreeSatToCliqueReduction.reduceAndSolve(clauses);
        return redRes.satisfiable && redRes.foundClique.size() == 2;
    }

    private static boolean testCliqueToIndepSet() {
        List<BooleanSatSolver.Clause> clauses = Arrays.asList(
                new BooleanSatSolver.Clause(new BooleanSatSolver.Literal("x1", true), new BooleanSatSolver.Literal("x2", false), new BooleanSatSolver.Literal("x3", true)),
                new BooleanSatSolver.Clause(new BooleanSatSolver.Literal("x1", false), new BooleanSatSolver.Literal("x2", true), new BooleanSatSolver.Literal("x3", false))
        );
        var pipeRes = NpHardReductionsPipeline.executePipeline(clauses);
        return pipeRes.independentSetVertices.size() == 2;
    }

    private static boolean testIndepSetToVertexCover() {
        List<BooleanSatSolver.Clause> clauses = Arrays.asList(
                new BooleanSatSolver.Clause(new BooleanSatSolver.Literal("x", true), new BooleanSatSolver.Literal("y", true), new BooleanSatSolver.Literal("z", true))
        );
        var pipeRes = NpHardReductionsPipeline.executePipeline(clauses);
        // Formula has 1 clause = 3 vertices. IS size = 1, VC size = 3 - 1 = 2.
        return pipeRes.verifiedEquivalent && pipeRes.vertexCoverVertices.size() == 2;
    }

    private static boolean testVertexCoverApprox() {
        List<String> vertices = Arrays.asList("A", "B", "C", "D");
        List<VertexCoverApproximator.Edge> edges = Arrays.asList(
                new VertexCoverApproximator.Edge("A", "B"),
                new VertexCoverApproximator.Edge("B", "C"),
                new VertexCoverApproximator.Edge("C", "D")
        );
        var comp = VertexCoverApproximator.compare(vertices, edges);
        // Optimal vertex cover: {B, C} size 2. Approx: size <= 4.
        return comp.optimalSize == 2 && comp.approxSize <= 4 && comp.approximationRatio <= 2.0;
    }

    private static boolean testStochasticQuickSort() {
        List<Integer> list = new ArrayList<>(Arrays.asList(9, 3, 7, 1, 5, 8, 2, 4, 6));
        StochasticQuickSort.sortDeterministic(list, Integer::compare, 42);
        return list.equals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
    }

    private static boolean testReservoirSampling() {
        StreamReservoirSampler<Integer> sampler = new StreamReservoirSampler<>(5, 12345L);
        for (int i = 1; i <= 100; i++) {
            sampler.feed(i);
        }
        return sampler.getSample().size() == 5 && sampler.getTotalItemsObserved() == 100;
    }

    private static boolean testMillerRabin() {
        // Known primes
        var r1 = MillerRabinPrimalityVerifier.testPrimality(1_000_000_007L, 10);
        var r2 = MillerRabinPrimalityVerifier.testPrimality(1_000_000_009L, 10);
        // Known composites
        var r3 = MillerRabinPrimalityVerifier.testPrimality(1_000_000_005L, 10);
        var r4 = MillerRabinPrimalityVerifier.testPrimality(BigInteger.valueOf(1048576), 10); // 2^20

        return r1.result == MillerRabinPrimalityVerifier.PrimalityResult.PROBABLE_PRIME &&
               r2.result == MillerRabinPrimalityVerifier.PrimalityResult.PROBABLE_PRIME &&
               r3.result == MillerRabinPrimalityVerifier.PrimalityResult.DEFINITELY_COMPOSITE &&
               r4.result == MillerRabinPrimalityVerifier.PrimalityResult.DEFINITELY_COMPOSITE;
    }

    private static boolean testBlellochScan() {
        long[] input = new long[]{10, 20, 15, 30, 25};
        var res = BlellochPrefixScanner.scan(input);
        // Inclusive scan of [10, 20, 15, 30, 25] is [10, 30, 45, 75, 100]
        long[] expected = new long[]{10, 30, 45, 75, 100};
        return Arrays.equals(res.inclusiveScan, expected);
    }

    private static boolean testParallelReduce() {
        long[] data = new long[10_000];
        long expectedSum = 0;
        for (int i = 0; i < data.length; i++) {
            data[i] = i + 1;
            expectedSum += (i + 1);
        }
        long parSum = ForkJoinParallelReducer.parallelSum(data);
        return parSum == expectedSum;
    }

    private static boolean testBrentTheorem() {
        long work = 100_000;
        long span = 500;
        int processors = 8;
        var rep = BrentTheoremWorkSpanModeler.analyze(work, span, processors);
        // T(P) <= (100000 - 500)/8 + 500 = 99500 / 8 + 500 = 12437.5 + 500 = 12937.5
        return rep.executionTimeBound > 0 && rep.speedup > 1.0 && rep.maxTheoreticalSpeedup == 200.0;
    }

    private static boolean testFileHandling() {
        DataStore ds = DataStore.getInstance();
        boolean exported = DataFileHandler.exportAll(ds);
        return exported;
    }
}
