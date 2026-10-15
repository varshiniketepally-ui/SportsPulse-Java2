package com.sportspulse.cli;

import com.sportspulse.algo.rand.*;
import com.sportspulse.data.MockData;
import com.sportspulse.model.Match;
import com.sportspulse.model.MatchEvent;
import com.sportspulse.model.Player;
import com.sportspulse.util.Ansi;
import com.sportspulse.util.Console;

import java.util.*;

/**
 * Live Match Center & Streaming Telemetry Analytics.
 * Powered by Module 6 Randomized & Parallel Algorithms:
 * - Reservoir Sampling (Algorithm R: Uniform random sample from unbounded streams O(N))
 * - Blelloch Parallel Scan (Work-efficient prefix sums with Up-sweep & Down-sweep O(N) work, O(log N) span)
 * - Parallel Reduce (Fork/Join RecursiveTask divide-and-conquer aggregation)
 * - Randomized QuickSort (Expected O(N log N) sorting with random pivot selection)
 * - Miller-Rabin Primality Test (Monte Carlo probabilistic primality verification)
 * - Brent's Scheduling Theorem (Theoretical bounds on parallel runtime and speedup)
 */
public class LiveMatchMenu {
    private final MockData data;
    private final Scanner sc;

    public LiveMatchMenu(MockData data, Scanner sc) { this.data = data; this.sc = sc; }

    public void run() {
        boolean back = false;
        while (!back) {
            Console.header("LIVE MATCH CENTER & STREAMING ANALYTICS", "Module 6 Randomized & Parallel Analytics Engine");
            Console.bullet("1. Simulate & sample a high-velocity event stream  (Reservoir Sampling)");
            Console.bullet("2. Cumulative stat totals over match minutes  (Blelloch Parallel Scan)");
            Console.bullet("3. Multi-threaded aggregate telemetry metrics  (Parallel Reduce)");
            Console.bullet("4. Player performance leaderboard ranking  (Randomized QuickSort)");
            Console.bullet("5. Prime transaction token verification  (Miller-Rabin)");
            Console.bullet("6. Parallel runtime & speedup estimation  (Brent's Theorem)");
            Console.bullet("0. Back to main menu");
            Console.prompt("\nSelect an option: ");
            switch (sc.nextLine().trim()) {
                case "1" -> reservoirDemo();
                case "2" -> blellochDemo();
                case "3" -> parallelReduceDemo();
                case "4" -> leaderboardDemo();
                case "5" -> millerRabinDemo();
                case "6" -> brentDemo();
                case "0" -> back = true;
                default -> Console.error("Invalid option.");
            }
            if (!back) Console.pause(sc);
        }
    }

    private Match pickMatch() {
        System.out.println("Matches available:");
        for (int i = 0; i < data.allMatches.size(); i++) System.out.println("  " + (i + 1) + ". " + data.allMatches.get(i));
        System.out.print("Pick a match number: ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (idx < 0 || idx >= data.allMatches.size()) { Console.warn("Out of range."); return null; }
            return data.allMatches.get(idx);
        } catch (NumberFormatException e) { Console.error("Invalid number."); return null; }
    }

    private void reservoirDemo() {
        Console.section("Reservoir Sampling over a High-Velocity Event Stream");
        Match m = pickMatch();
        if (m == null) return;

        List<String> simulatedStream = new ArrayList<>();
        Random rng = new Random();
        for (int i = 1; i <= 5000; i++) {
            MatchEvent base = m.getEvents().get(rng.nextInt(m.getEvents().size()));
            simulatedStream.add(String.format("EVT-%05d  %s", i, base));
        }
        int k = 8;
        List<String> sample = ReservoirSampling.sample(simulatedStream, k);
        Console.kv("Simulated Live Stream Length", simulatedStream.size() + " incoming telemetry events");
        Console.kv("Reservoir Sample Size (K)", k);
        System.out.println("Uniform random sample drawn in single linear pass (Algorithm R):");
        for (String e : sample) System.out.println("  " + e);

        maybeShowTechnicalDetails("Reservoir Sampling (Algorithm R)",
                "Maintains an unbiased random sample of size K from an unknown or infinite data stream",
                simulatedStream.size(), k + " uniformly selected telemetry packets",
                "O(N) single-pass linear time", "O(K) constant memory",
                "Reservoir array with decreasing replacement probability k/i");
    }

    private void blellochDemo() {
        Console.section("Blelloch Parallel Scan: Cumulative Stats Over Time");
        Match m = pickMatch();
        if (m == null) return;
        int maxMinute = m.getEvents().stream().mapToInt(MatchEvent::getMinute).max().orElse(90);
        int[] perMinuteCount = new int[maxMinute + 1];
        for (MatchEvent e : m.getEvents()) perMinuteCount[e.getMinute()]++;
        int[] running = BlellochScan.inclusiveScan(perMinuteCount);
        Console.kv("Total Telemetry Events", Arrays.stream(perMinuteCount).sum());
        System.out.println("Minute : Events in Minute : Cumulative Running Total");
        for (int i = 0; i <= maxMinute; i++) {
            if (perMinuteCount[i] > 0) System.out.printf("  %3d    :       %d          :     %d%n", i, perMinuteCount[i], running[i]);
        }

        maybeShowTechnicalDetails("Blelloch Work-Efficient Parallel Prefix Scan",
                "Computes all prefix sums with optimal work using Up-sweep (reduce) and Down-sweep phases",
                perMinuteCount.length, "Cumulative array of length " + running.length,
                "Work: O(N) operations, Span: O(log N) depth", "O(N) array storage",
                "Implicit binary tree traversed in two phases (Up-sweep & Down-sweep)");
    }

    private void parallelReduceDemo() {
        Console.section("Parallel Reduce: Multi-Core Aggregate Match Metrics");
        Match m = pickMatch();
        if (m == null) return;
        double totalSkillOfInvolved = ParallelReduce.sum(m.getEvents(), e -> {
            Player p = data.findById(e.getPlayerId());
            return p == null ? 0 : p.getSkill();
        });
        Console.kv("Number of Events Aggregated", m.getEvents().size());
        Console.kv("Sum of Skill Ratings of Players Involved", String.format("%.1f", totalSkillOfInvolved));
        System.out.println("  (Computed via Java Fork/Join RecursiveTask splitting the event list across available CPU cores.)");

        maybeShowTechnicalDetails("Fork/Join Parallel Reducer",
                "Divide-and-conquer parallel tree reduction using Java ForkJoinPool work-stealing",
                m.getEvents().size(), "Aggregated skill score: " + String.format("%.1f", totalSkillOfInvolved),
                "Work: O(N), Span: O(log N) with P processors", "O(log N) stack frames",
                "RecursiveTask binary tree with fork() and join() work-stealing scheduler");
    }

    private void leaderboardDemo() {
        Console.section("Randomized QuickSort: Player Performance Leaderboard");
        System.out.println("Ranking ALL players across every sport by skill rating (descending):");
        List<Player> all = new ArrayList<>(data.allPlayers);
        RandomizedQuickSort.sort(all, Comparator.comparingDouble(Player::getSkill).reversed());
        int rank = 1;
        for (Player p : all) {
            System.out.printf("  #%-3d %s%n", rank++, p);
        }

        maybeShowTechnicalDetails("Randomized QuickSort",
                "Selects uniform random pivots to guarantee expected O(N log N) time on any input distribution",
                all.size(), all.size() + " athletes sorted in descending order",
                "Expected O(N log N), defeats worst-case quadratic skew", "O(log N) recursion depth",
                "In-place partitioning with uniform pseudo-random pivot selection");
    }

    private void millerRabinDemo() {
        Console.section("Miller-Rabin: Probabilistic Primality Verification");
        System.out.println("Generating 5 prime-valued candidate transaction IDs (Miller-Rabin used to test candidates for primality):");
        for (int i = 0; i < 5; i++) {
            long id = MillerRabin.generateSecureId(9);
            System.out.println("  TXN-" + id + "   (verified prime: " + MillerRabin.isProbablePrime(id, 20) + ")");
        }
        System.out.print("\nEnter a number to primality-test yourself: ");
        try {
            long n = Long.parseLong(sc.nextLine().trim());
            boolean prime = MillerRabin.isProbablePrime(n, 20);
            Console.kv(n + " is prime? (Miller-Rabin, 20 witness rounds)", prime ? "YES" : "NO");
        } catch (NumberFormatException e) { System.out.println("Skipped (not a valid number)."); }

        maybeShowTechnicalDetails("Miller-Rabin Primality Test",
                "Monte Carlo algorithm testing odd integers for compositeness via modular exponentiation",
                64, "Error probability <= 4^(-k) where k=20 rounds",
                "O(k * log^3 N) using repeated squaring", "O(1) auxiliary variables",
                "Strong pseudoprime witness testing based on Fermat's Little Theorem");
    }

    private void brentDemo() {
        Console.section("Brent's Theorem: Parallel Work-Time & Speedup Estimation");
        System.out.print("Enter total WORK units for the analytics job (e.g. 1000000): ");
        long work = safeLong(sc.nextLine().trim(), 1_000_000);
        System.out.print("Enter critical-path DEPTH (e.g. 20): ");
        long depth = safeLong(sc.nextLine().trim(), 20);
        System.out.print("Sweep how many processor counts (e.g. 8): ");
        int maxP = (int) safeLong(sc.nextLine().trim(), 8);

        BrentTheorem.Estimate[] sweep = BrentTheorem.sweep(work, depth, Math.max(1, maxP));
        System.out.println("Brent's bound:    T_p <= W/p + T_inf   (T_inf = critical-path depth = " + depth + ")");
        System.out.println("Theoretical min:  T_p >= max(W/p, T_inf)");
        System.out.println();
        System.out.println("Processors | Lower bound | Brent est. | Speedup | Efficiency");
        for (BrentTheorem.Estimate est : sweep) {
            double lowerBound = Math.max((double) work / est.processors, depth);
            System.out.printf("  %-10d | %-11.1f | %-10.1f | %-7.2fx | %.0f%%%n",
                    est.processors, lowerBound, est.estimatedTime, est.speedupVsSequential, est.efficiency * 100);
        }

        maybeShowTechnicalDetails("Brent's Scheduling Theorem",
                "Bounds execution time of a parallel computation of work W and span S on P processors",
                maxP, "T(P) <= (W - S)/P + S",
                "Theoretical scheduling bound", "O(1) calculation",
                "Work-Span directed acyclic graph (DAG) model");
    }

    private long safeLong(String s, long fallback) {
        try { return Long.parseLong(s); } catch (Exception e) { return fallback; }
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
