package com.sportspulse.cli;

import com.sportspulse.algo.suffix.*;
import com.sportspulse.data.MockData;
import com.sportspulse.model.MatchReport;
import com.sportspulse.util.Ansi;
import com.sportspulse.util.Console;

import java.util.List;
import java.util.Scanner;

/**
 * Tactical Reports & Commentary Document Mining Menu.
 * Powered by Module 2 Advanced Suffix Structures:
 * - Suffix Array (Prefix Doubling O(N log^2 N) / O(N log N))
 * - SA-IS (Linear Time O(N) Induced Sorting)
 * - Kasai's LCP Array (Longest Common Prefix O(N))
 * - Explicit Compressed Suffix Tree (Edge Compression O(M + K))
 * - Suffix Automaton / DAWG (Online O(N) Construction & O(M) Membership)
 */
public class ReportsMenu {
    private final MockData data;
    private final Scanner sc;

    public ReportsMenu(MockData data, Scanner sc) { this.data = data; this.sc = sc; }

    public void run() {
        boolean back = false;
        while (!back) {
            Console.header("REPORTS & COMMENTARY ANALYSIS", "Module 2 Suffix Structures & Document Mining");
            Console.bullet("1. Index a report and search substrings  (Suffix Array)");
            Console.bullet("2. Linear-time index construction  (SA-IS O(N) Induced Sorting)");
            Console.bullet("3. Longest repeated phrase in a report  (Kasai's LCP)");
            Console.bullet("4. Cross-report similarity & tactical overlap  (LCP-based)");
            Console.bullet("5. Deep tactical phrase tree mining  (Explicit Suffix Tree)");
            Console.bullet("6. Fast substring existence check  (Suffix Automaton)");
            Console.bullet("0. Back to main menu");
            Console.prompt("\nSelect an option: ");
            switch (sc.nextLine().trim()) {
                case "1" -> suffixArraySearch();
                case "2" -> saisLinearIndex();
                case "3" -> longestRepeated();
                case "4" -> crossReportSimilarity();
                case "5" -> suffixTreeDemo();
                case "6" -> suffixAutomatonDemo();
                case "0" -> back = true;
                default -> Console.error("Invalid option.");
            }
            if (!back) Console.pause(sc);
        }
    }

    private MatchReport pickReport() {
        System.out.println("Match reports available:");
        for (int i = 0; i < data.allReports.size(); i++) System.out.println("  " + (i + 1) + ". " + data.allReports.get(i));
        System.out.print("Pick a report number: ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (idx < 0 || idx >= data.allReports.size()) { Console.warn("Out of range."); return null; }
            return data.allReports.get(idx);
        } catch (NumberFormatException e) { Console.error("Invalid number."); return null; }
    }

    private void suffixArraySearch() {
        Console.section("Suffix Array Indexing & Search");
        MatchReport r = pickReport();
        if (r == null) return;
        SuffixArray sa = new SuffixArray(r.getBody().toLowerCase());
        System.out.print("Enter a substring to search for: ");
        String pattern = sc.nextLine().trim().toLowerCase();
        List<Integer> positions = sa.search(pattern);
        Console.kv("Occurrences", positions.size());
        if (!positions.isEmpty()) Console.kv("Character offsets", positions);
        Console.kv("Suffix array size (incl. sentinel)", sa.getSuffixArray().length);

        maybeShowTechnicalDetails("Suffix Array (Prefix Doubling)",
                "Full-text substring indexing via sorted suffix indices and binary search",
                r.getBody().length(), positions.size() + " matches found",
                "O(N log^2 N) build, O(M log N) query", "O(N) memory",
                "Integer array of suffix permutation offsets");
    }

    private void saisLinearIndex() {
        Console.section("SA-IS: Linear-Time O(N) Suffix Array Construction");
        MatchReport r = pickReport();
        if (r == null) return;
        String text = r.getBody().toLowerCase();

        long startSais = System.nanoTime();
        SAIS sais = new SAIS(text);
        long saisTime = System.nanoTime() - startSais;

        long startStd = System.nanoTime();
        SuffixArray stdSa = new SuffixArray(text);
        long stdTime = System.nanoTime() - startStd;

        Console.kv("Report Length", text.length() + " characters");
        Console.kv("Standard Suffix Array Build Time", String.format("%.3f ms", stdTime / 1_000_000.0));
        Console.kv("SA-IS Linear-Time Build Time", String.format("%.3f ms", saisTime / 1_000_000.0));
        double speedup = stdTime > 0 ? (double) stdTime / Math.max(1, saisTime) : 1.0;
        Console.kv("Observed Efficiency Ratio", String.format("%.2fx speedup", speedup));
        Console.kv("SA-IS Suffix Elements", sais.getSuffixArray().length);

        maybeShowTechnicalDetails("SA-IS (Suffix Array by Induced Sorting)",
                "Linear time suffix array construction classifying characters into S-type, L-type, and LMS",
                text.length(), sais.getSuffixArray().length + " suffix indices sorted",
                "O(N) strict linear time", "O(N) space",
                "Induced Sorting Bucket Array + LMS (Leftmost S-type) recursion");
    }

    private void longestRepeated() {
        Console.section("Longest Repeated Substring (Kasai's LCP)");
        MatchReport r = pickReport();
        if (r == null) return;
        String longest = Kasai.longestRepeatedSubstring(r.getBody());
        Console.kv("Longest repeated substring", longest.isBlank() ? "(none found)" : "\"" + longest.trim() + "\"");
        Console.kv("Length", longest.length());

        maybeShowTechnicalDetails("Kasai's Algorithm",
                "Linear-time LCP (Longest Common Prefix) array computation for repeated pattern extraction",
                r.getBody().length(), "\"" + longest.trim() + "\" (" + longest.length() + " chars)",
                "O(N) time", "O(N) space",
                "Rank array + Suffix array + LCP height pointer");
    }

    private void crossReportSimilarity() {
        Console.section("Cross-Report Similarity Check");
        if (data.allReports.size() < 2) { System.out.println("Need at least 2 reports."); return; }
        System.out.println("Comparing all report pairs for shared longest common substring coverage:");
        for (int i = 0; i < data.allReports.size(); i++) {
            for (int j = i + 1; j < data.allReports.size(); j++) {
                MatchReport a = data.allReports.get(i), b = data.allReports.get(j);
                double sim = Kasai.similarity(a.getBody(), b.getBody());
                System.out.printf("  %-45s vs %-45s -> similarity=%.1f%%%n",
                        truncate(a.getTitle(), 40), truncate(b.getTitle(), 40), sim * 100);
            }
        }

        maybeShowTechnicalDetails("Cross-Document LCP Similarity",
                "Generalized suffix array over concatenated documents to extract tactical overlap",
                data.allReports.size() * 500, "Pairwise similarity matrix",
                "O(N1 + N2) combined linear scan", "O(N1 + N2) space",
                "Combined Suffix Array + LCP array with document boundary tracking");
    }

    private void suffixTreeDemo() {
        Console.section("Explicit Compressed Suffix Tree");
        MatchReport r = pickReport();
        if (r == null) return;
        String text = r.getBody().toLowerCase();
        SuffixTree tree = new SuffixTree(text);

        System.out.print("Enter a tactical phrase to search: ");
        String query = sc.nextLine().trim().toLowerCase();
        List<Integer> occurrences = tree.search(query);

        Console.kv("Search Pattern", "\"" + query + "\"");
        Console.kv("Pattern Occurrences", occurrences.size());
        if (!occurrences.isEmpty()) {
            Console.kv("Character Offsets", occurrences);
        }

        maybeShowTechnicalDetails("Explicit Compressed Suffix Tree",
                "Edge-compressed trie storing all suffixes with character edge compression",
                text.length(), occurrences.size() + " occurrences found",
                "O(M + K) query time (M=query length, K=occurrences)", "O(N) nodes",
                "Multi-way branching Node/Edge tree with edge index compression");
    }

    private void suffixAutomatonDemo() {
        Console.section("Fast Substring Existence Check (Suffix Automaton / DAWG)");
        MatchReport r = pickReport();
        if (r == null) return;
        SuffixAutomaton sam = new SuffixAutomaton(r.getBody().toLowerCase());
        System.out.print("Enter a phrase to test for existence: ");
        String phrase = sc.nextLine().trim().toLowerCase();
        boolean found = sam.contains(phrase);
        Console.kv("Phrase found", found ? "YES" : "NO");
        Console.kv("Automaton state count", sam.numberOfStates());

        maybeShowTechnicalDetails("Suffix Automaton (DAWG)",
                "Minimal deterministic automaton recognizing all text substrings",
                phrase.length(), found ? "EXISTS" : "NOT FOUND",
                "O(M) query time independent of text length", "O(N) states (at most 2N-1)",
                "State transition graph with suffix links");
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
