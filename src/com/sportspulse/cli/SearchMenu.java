package com.sportspulse.cli;

import com.sportspulse.algo.strmatch.*;
import com.sportspulse.data.MockData;
import com.sportspulse.model.MatchReport;
import com.sportspulse.model.Player;
import com.sportspulse.util.Ansi;
import com.sportspulse.util.Console;

import java.util.*;

/**
 * Intelligent Sports Search Menu.
 * Powered by Module 1 Advanced String Matching & Module 3 Dynamic Programming:
 * - KMP (Knuth-Morris-Pratt O(N + M))
 * - Damerau-Levenshtein Typo Distance O(N * M)
 * - Z-Algorithm (Z-Boxes O(N + M))
 * - Rabin-Karp Rolling Hash Polynomial Matching O(N + M)
 * - Aho-Corasick Multi-Pattern Automaton O(N + M + K)
 */
public class SearchMenu {
    private final MockData data;
    private final Scanner sc;

    public SearchMenu(MockData data, Scanner sc) { this.data = data; this.sc = sc; }

    public void run() {
        boolean back = false;
        while (!back) {
            Console.header("INTELLIGENT SPORTS SEARCH & LOOKUP", "Module 1 Advanced String Matching Engine");
            Console.bullet("1. Find player by exact/partial name  (KMP)");
            Console.bullet("2. Fuzzy name auto-correct & suggestions  (Damerau-Levenshtein)");
            Console.bullet("3. Detect repeated commentary patterns  (Z-Function)");
            Console.bullet("4. Batch player/match ID lookup in text  (Rabin-Karp)");
            Console.bullet("5. Multi-keyword event & telemetry scan  (Aho-Corasick)");
            Console.bullet("0. Back to main menu");
            Console.prompt("\nSelect an option: ");
            switch (sc.nextLine().trim()) {
                case "1" -> kmpSearch();
                case "2" -> fuzzyAutocorrect();
                case "3" -> zFunctionDemo();
                case "4" -> rabinKarpDemo();
                case "5" -> ahoCorasickDemo();
                case "0" -> back = true;
                default -> Console.error("Invalid option.");
            }
            if (!back) Console.pause(sc);
        }
    }

    private void kmpSearch() {
        Console.section("KMP Exact Search Across Match Reports");
        System.out.print("Enter a name or word to search across all match reports: ");
        String query = sc.nextLine().trim();
        int totalHits = 0;
        int totalChars = 0;
        for (MatchReport r : data.allReports) {
            totalChars += r.getBody().length();
            List<Integer> hits = KMP.search(r.getBody().toLowerCase(), query.toLowerCase());
            if (!hits.isEmpty()) {
                totalHits += hits.size();
                System.out.printf("  [%s] %d occurrence(s) at char offsets %s%n", r.getMatchId(), hits.size(), hits);
            }
        }
        System.out.println(totalHits == 0 ? "No matches found." : "Total occurrences: " + totalHits);

        maybeShowTechnicalDetails("Knuth-Morris-Pratt (KMP)",
                "Exact pattern matching avoiding re-examination of matched characters via pi table",
                totalChars, totalHits + " matches across reports",
                "O(N + M) linear time", "O(M) failure function space",
                "Prefix failure function (pi table / LPS array)");
    }

    private void fuzzyAutocorrect() {
        Console.section("Fuzzy Player Name Auto-correct (Damerau-Levenshtein)");
        System.out.print("Type a (possibly misspelled) player name: ");
        String query = sc.nextLine().trim();
        List<String> names = data.allPlayers.stream().map(Player::getName).toList();
        String best = com.sportspulse.algo.dp.EditDistance.closestMatch(query, names);
        int dist = com.sportspulse.algo.dp.EditDistance.damerauLevenshtein(query.toLowerCase(), best.toLowerCase());
        Console.kv("Query Entered", query);
        Console.kv("Did you mean?", Ansi.paint(Ansi.BRIGHT_GREEN, best));
        Console.kv("Edit Distance", dist + " edit(s) (insert, delete, replace, transposition)");
        Player p = data.allPlayers.stream().filter(pl -> pl.getName().equals(best)).findFirst().orElse(null);
        if (p != null) Console.kv("Player Profile", p);

        maybeShowTechnicalDetails("Damerau-Levenshtein Edit Distance",
                "Dynamic programming string alignment with support for adjacent transposition swaps",
                Math.max(query.length(), best.length()), "Distance = " + dist + " to nearest athlete",
                "O(N * M) time", "O(N * M) DP matrix table",
                "2D recurrence matrix with diagonal swap lookback");
    }

    private void zFunctionDemo() {
        Console.section("Repeated Commentary Phrase Detection (Z-Function)");
        System.out.println("Match reports available:");
        for (int i = 0; i < data.allReports.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + data.allReports.get(i));
        }
        System.out.print("Pick a report number: ");
        int idx = parseIndex(sc.nextLine().trim(), data.allReports.size());
        if (idx < 0) return;
        MatchReport r = data.allReports.get(idx);
        List<String> repeats = ZFunction.findRepeatedPhrases(r.getBody(), 5, 2);
        if (repeats.isEmpty()) {
            System.out.println("No 5-word phrase repeats 2+ times in this report.");
        } else {
            System.out.println("Repeated 5-word phrases found:");
            for (String s : repeats) Console.bullet(s);
        }

        maybeShowTechnicalDetails("Z-Algorithm (Fundamental Preprocessing)",
                "Calculates longest prefix match at every string position via maintainable Z-boxes",
                r.getBody().length(), repeats.size() + " repeated phrases detected",
                "O(N) linear time", "O(N) integer Z-array",
                "Z-box interval pointers [L, R] tracking rightmost segment");
    }

    private void rabinKarpDemo() {
        Console.section("Batch Player ID Lookup (Rabin-Karp)");
        System.out.println("Sample player IDs available, e.g. P-FB-06, P-BB-01, P-CK-05 ...");
        System.out.print("Enter comma-separated IDs to batch-search across event descriptions: ");
        String[] ids = sc.nextLine().trim().split("\\s*,\\s*");
        StringBuilder haystack = new StringBuilder();
        for (var m : data.allMatches) {
            for (var e : m.getEvents()) haystack.append(e.getPlayerId()).append(" ").append(e.getDescription()).append(" | ");
        }
        Map<String, List<Integer>> results = RabinKarp.batchSearch(haystack.toString(), Arrays.asList(ids));
        int totalFound = 0;
        for (var entry : results.entrySet()) {
            System.out.printf("  %-10s -> %d occurrence(s)%n", entry.getKey(), entry.getValue().size());
            totalFound += entry.getValue().size();
        }

        maybeShowTechnicalDetails("Rabin-Karp Rolling Hash",
                "Multi-pattern substring filtering using rolling polynomial hash modulo large prime",
                haystack.length(), totalFound + " occurrences mapped",
                "O(N + M) average time, O(N * M) worst-case", "O(K) pattern hash table",
                "Rolling polynomial hash accumulator with Horner's rule");
    }

    private void ahoCorasickDemo() {
        Console.section("Multi-Keyword Event Scan (Aho-Corasick)");
        System.out.println("Scanning all match reports for simultaneous event keywords...");
        AhoCorasick ac = new AhoCorasick();
        List<String> keywords = List.of("goal", "foul", "yellow card", "red card", "wicket",
                "three", "dunk", "six", "four", "maiden");
        for (String k : keywords) ac.addKeyword(k);
        ac.build();
        int totalHits = 0;
        int totalChars = 0;
        for (MatchReport r : data.allReports) {
            totalChars += r.getBody().length();
            Map<String, List<Integer>> hits = ac.search(r.getBody());
            if (!hits.isEmpty()) {
                System.out.println("  Report: " + r.getTitle());
                for (var e : hits.entrySet()) {
                    System.out.printf("     '%s' found %d time(s)%n", e.getKey(), e.getValue().size());
                    totalHits += e.getValue().size();
                }
            }
        }

        maybeShowTechnicalDetails("Aho-Corasick Multi-Pattern Automaton",
                "Simultaneous multi-keyword stream matching combining trie with suffix fail links",
                totalChars, totalHits + " event keywords matched in single linear pass",
                "O(N + M + K) where K is match count", "O(Total pattern length) automaton state space",
                "Trie automaton + BFS suffix fallback links + Dictionary output links");
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

    private int parseIndex(String input, int size) {
        try {
            int i = Integer.parseInt(input) - 1;
            if (i < 0 || i >= size) { Console.warn("Out of range."); return -1; }
            return i;
        } catch (NumberFormatException e) { Console.error("Invalid number."); return -1; }
    }

    private String truncate(String s, int len) { return s.length() <= len ? s : s.substring(0, len - 3) + "..."; }
}
