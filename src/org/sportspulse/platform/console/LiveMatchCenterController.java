package org.sportspulse.platform.console;

import org.sportspulse.platform.algorithms.parallel.BlellochPrefixScanner;
import org.sportspulse.platform.algorithms.parallel.ForkJoinParallelReducer;
import org.sportspulse.platform.algorithms.randomized.StreamReservoirSampler;
import org.sportspulse.platform.algorithms.strings.AhoCorasickEventAutomaton;
import org.sportspulse.platform.domain.model.FixtureMatch;
import org.sportspulse.platform.domain.model.GameIncident;
import org.sportspulse.platform.domain.repository.DataStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Live Match Center:
 * Real-time match monitoring, continuous event streaming, incident tracking,
 * and high-throughput parallel telemetry aggregation.
 * Powered by:
 * - Aho-Corasick Multi-Pattern Automaton
 * - Streaming Reservoir Sampling (Algorithm R)
 * - Blelloch Work-Efficient Parallel Prefix Scan
 * - Multi-Threaded ForkJoin Parallel Reducer
 */
public class LiveMatchCenterController {

    public static void showMenu() {
        DataStore ds = DataStore.getInstance();
        boolean back = false;

        while (!back) {
            TerminalIO.printHeader("LIVE MATCH CENTER & REAL-TIME TELEMETRY");
            TerminalIO.println("  [1] Live Match Incident Feed & Timeline Stream");
            TerminalIO.println("  [2] Automated Match Event Keyword Detector (Aho-Corasick)");
            TerminalIO.println("  [3] Maintain Continuous Unbiased Event Sample (Reservoir Sampling)");
            TerminalIO.println("  [4] Aggregate Match Telemetry: Passes, Shots, Fouls (Parallel Reduce)");
            TerminalIO.println("  [5] Cumulative Possession & Pressure Progression (Blelloch Parallel Scan)");
            TerminalIO.println("  [0] Return to Main Dashboard");

            int choice = TerminalIO.readInt("Select Option", 0, 5);
            switch (choice) {
                case 1 -> showLiveIncidentStream(ds);
                case 2 -> runAhoCorasickTracker(ds);
                case 3 -> runReservoirSampler(ds);
                case 4 -> runParallelTelemetryReduction();
                case 5 -> runBlellochCumulativeTelemetry();
                case 0 -> back = true;
            }
        }
    }

    private static void showLiveIncidentStream(DataStore ds) {
        TerminalIO.printSubHeader("Live Match Incident Feed");
        List<FixtureMatch> fixtures = ds.getAllFixtures();
        if (fixtures.isEmpty()) {
            TerminalIO.printWarning("No live fixtures found.");
            TerminalIO.pressEnterToContinue();
            return;
        }

        FixtureMatch match = fixtures.get(0);
        TerminalIO.println(TerminalIO.color(TerminalIO.BOLD + TerminalIO.CYAN,
                String.format("LIVE: %s [%d] vs [%d] %s | Venue: %s",
                        match.getHomeFranchiseId(), match.getHomeScore(), match.getAwayScore(),
                        match.getAwayFranchiseId(), match.getVenue())));

        TerminalIO.println("\nContinuous Incident Feed (" + match.getIncidents().size() + " events):");
        for (GameIncident inc : match.getIncidents()) {
            TerminalIO.println(String.format("  [%02d'] %-15s : %s (Actor: %s)",
                    inc.getMinute(), inc.getType().getLabel(), inc.getDescription(), inc.getPrimaryAthleteId()));
        }
        TerminalIO.pressEnterToContinue();
    }

    private static void runAhoCorasickTracker(DataStore ds) {
        TerminalIO.printSubHeader("Automated Live Incident Keyword Tracker");
        List<String> keywords = Arrays.asList("goal", "tackle", "foul", "corner", "assist", "counter-attack", "caution", "strike");
        AhoCorasickEventAutomaton ac = new AhoCorasickEventAutomaton(keywords);

        StringBuilder feed = new StringBuilder();
        for (var m : ds.getAllFixtures()) feed.append(m.getCommentaryStream()).append("\n");

        long start = System.nanoTime();
        var hits = ac.search(feed.toString());
        long time = System.nanoTime() - start;

        TerminalIO.printSuccess(String.format("Aho-Corasick Automaton intercepted %d live match alerts:", hits.size()));
        String[] headers = new String[]{"Alert Type", "Minute/Offset", "Commentary Context"};
        List<String[]> rows = new ArrayList<>();
        for (var h : hits) {
            rows.add(new String[]{h.getKeyword().toUpperCase(), "[" + h.getStartIndex() + ".." + h.getEndIndex() + "]", h.getSurroundingSnippet()});
        }
        TerminalIO.printTable(headers, rows);

        askTechnicalDetails("Aho-Corasick String Matching Automaton",
                String.format("Single pass over %d characters with dictionary output links", feed.length()),
                time, "O(N + matches) linear scan");
        TerminalIO.pressEnterToContinue();
    }

    private static void runReservoirSampler(DataStore ds) {
        TerminalIO.printSubHeader("Streaming Reservoir Sampling (Algorithm R)");
        int k = TerminalIO.readInt("Enter Reservoir Buffer Size (K)", 3, 10);
        StreamReservoirSampler<String> sampler = new StreamReservoirSampler<>(k);

        long start = System.nanoTime();
        for (var f : ds.getAllFixtures()) {
            for (var inc : f.getIncidents()) {
                sampler.feed(inc.toString());
            }
        }
        long time = System.nanoTime() - start;

        TerminalIO.printSuccess(String.format("Maintained uniform random sample of size %d from %d total streaming events:",
                k, sampler.getTotalItemsObserved()));
        TerminalIO.println(String.format("Empirical Inclusion Probability per Event: %.2f%%\n", sampler.getInclusionProbability() * 100.0));

        List<String> sample = sampler.getSample();
        for (int i = 0; i < sample.size(); i++) {
            TerminalIO.println(String.format("  Sample [%d]: %s", i + 1, sample.get(i)));
        }

        askTechnicalDetails("Reservoir Sampling (Algorithm R)",
                "Maintains unbiased uniform sample in single streaming pass without storing history",
                time, "O(N) time and O(K) memory");
        TerminalIO.pressEnterToContinue();
    }

    private static void runParallelTelemetryReduction() {
        TerminalIO.printSubHeader("Parallel Telemetry Aggregation (Multi-Core ForkJoin)");
        int size = 100_000;
        long[] passes = new long[size];
        for (int i = 0; i < size; i++) passes[i] = (i % 30) + 1;

        var comp = ForkJoinParallelReducer.compare(passes);

        TerminalIO.printSuccess("Match Telemetry Aggregated Across Multithreaded Core Pool:");
        TerminalIO.println(String.format("  Total Telemetry Units Summed: %d units", comp.totalSum));
        TerminalIO.println(String.format("  Sequential Single-Thread Time: %.3f µs", comp.sequentialTimeNs / 1000.0));
        TerminalIO.println(String.format("  Parallel ForkJoin Pool Time:   %.3f µs (%d worker threads)",
                comp.parallelTimeNs / 1000.0, comp.parallelism));
        TerminalIO.println(String.format("  Parallel Compute Speedup:      %.2fx", comp.speedup));

        askTechnicalDetails("ForkJoin Parallel Divide-and-Conquer Reducer",
                String.format("Aggregated %d telemetry events using recursive work-stealing tasks", size),
                comp.parallelTimeNs, "Work: O(N) | Span: O(log N)");
        TerminalIO.pressEnterToContinue();
    }

    private static void runBlellochCumulativeTelemetry() {
        TerminalIO.printSubHeader("Cumulative Match Progression (Blelloch Work-Efficient Scan)");
        long[] minutePressure = new long[]{15, 20, 10, 35, 25, 30, 45, 10};

        var scan = BlellochPrefixScanner.scan(minutePressure);

        TerminalIO.printSuccess("Calculated Cumulative Tactical Pressure Timeline:");
        TerminalIO.println("  Minute Segments:     [10', 20', 30', 40', 50', 60', 70', 80']");
        TerminalIO.println("  Segment Workload:    " + Arrays.toString(scan.originalData));
        TerminalIO.println("  Cumulative Total:    " + Arrays.toString(scan.inclusiveScan));

        askTechnicalDetails("Blelloch Work-Efficient Parallel Prefix Scan",
                String.format("Two-Phase Binary Tree: Up-Sweep (reduce) + Down-Sweep (prefix distribution). Work = %d ops, Span = %d steps",
                        scan.work, scan.span),
                scan.executionTimeNs, "Work: O(N) | Span: O(log N)");
        TerminalIO.pressEnterToContinue();
    }

    private static void askTechnicalDetails(String algorithm, String summary, long timeNs, String complexity) {
        boolean show = TerminalIO.readBoolean("\n[T] View technical DSA algorithmic details?");
        if (show) {
            TerminalIO.println(TerminalIO.color(TerminalIO.CYAN, "--------------------------------------------------------------------------------"));
            TerminalIO.println(TerminalIO.color(TerminalIO.BOLD + TerminalIO.CYAN, "  TECHNICAL DSA LIVE STREAMING & PARALLEL PROFILE"));
            TerminalIO.println(TerminalIO.color(TerminalIO.CYAN, "--------------------------------------------------------------------------------"));
            TerminalIO.println("  Algorithm Executed:    " + algorithm);
            TerminalIO.println("  Analytical Context:    " + summary);
            TerminalIO.println(String.format("  Execution Time:        %.3f µs", timeNs / 1000.0));
            TerminalIO.println("  Theoretical Bound:     " + complexity);
            TerminalIO.println("  Academic Module:       Module 1 & Module 6 (Strings, Streaming & Parallel DSA)");
            TerminalIO.println(TerminalIO.color(TerminalIO.CYAN, "--------------------------------------------------------------------------------"));
        }
    }
}
