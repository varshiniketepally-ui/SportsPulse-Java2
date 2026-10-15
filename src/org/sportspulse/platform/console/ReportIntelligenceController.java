package org.sportspulse.platform.console;

import org.sportspulse.platform.algorithms.suffix.*;
import org.sportspulse.platform.domain.model.MatchDossier;
import org.sportspulse.platform.domain.repository.DataStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tactical Reports & Document Intelligence:
 * Provides sports scouts and analysts with deep text mining into scouting dossiers.
 * Powered by Suffix Structures:
 * - Substring indexing: Suffix Array & Explicit Suffix Tree
 * - Linear-time construction: SA-IS (O(N) Nong-Zhang-Chan)
 * - Cross-report similarity: Kasai's LCP algorithm
 * - Repeated tactical pattern mining: Suffix Automaton (DAWG)
 */
public class ReportIntelligenceController {

    public static void showMenu() {
        DataStore ds = DataStore.getInstance();
        boolean back = false;

        while (!back) {
            TerminalIO.printHeader("REPORTS & TACTICAL INTELLIGENCE");
            TerminalIO.println("  [1] Search Within Tactical Scouting Dossiers (Suffix Array Index)");
            TerminalIO.println("  [2] Cross-Report Similarity: Extract Common Tactical Phrases (Kasai LCP)");
            TerminalIO.println("  [3] Mine Repeated Tactical Buzzwords (Suffix Automaton DAWG)");
            TerminalIO.println("  [4] Rebuild Document Index (Benchmark: Suffix Array vs SA-IS O(N))");
            TerminalIO.println("  [5] Browse All Scouting Dossiers & Tactical Notes");
            TerminalIO.println("  [0] Return to Main Dashboard");

            int choice = TerminalIO.readInt("Select Option", 0, 5);
            switch (choice) {
                case 1 -> searchInsideDossiers(ds);
                case 2 -> compareReportsKasai(ds);
                case 3 -> mineRepeatedPhrases(ds);
                case 4 -> benchmarkIndexing(ds);
                case 5 -> browseDossiers(ds);
                case 0 -> back = true;
            }
        }
    }

    private static void browseDossiers(DataStore ds) {
        TerminalIO.printSubHeader("Available Match Dossiers & Tactical Reports");
        for (MatchDossier d : ds.getAllDossiers()) {
            TerminalIO.println(TerminalIO.color(TerminalIO.BOLD + TerminalIO.CYAN, "  * [" + d.getDossierId() + "] " + d.getTitle()));
            TerminalIO.println("    Author: " + d.getAuthor() + " | Match: " + d.getMatchId());
            TerminalIO.println("    Tactical Summary: " + d.getTacticalSummary());
            TerminalIO.println("    Scout Takeaways:  " + d.getKeyTakeaways() + "\n");
        }
        TerminalIO.pressEnterToContinue();
    }

    private static void searchInsideDossiers(DataStore ds) {
        TerminalIO.printSubHeader("Suffix Array Substring Index Search");
        String id = TerminalIO.readString("Enter Dossier ID to search (e.g. DOS-01 or press enter for default)");
        if (id.isEmpty()) id = "DOS-01";

        MatchDossier dossier = ds.getDossier(id);
        if (dossier == null) {
            TerminalIO.printError("Dossier not found.");
            TerminalIO.pressEnterToContinue();
            return;
        }

        String content = dossier.getFullContent();
        StandardSuffixArray ssa = new StandardSuffixArray(content);
        String query = TerminalIO.readNonEmptyString("Enter search phrase (e.g. 'pressing', 'counter', 'fatigue')");

        List<Integer> offsets = ssa.searchSubstring(query);
        TerminalIO.printSuccess(String.format("Found %d occurrence(s) of '%s' in dossier \"%s\":", offsets.size(), query, dossier.getTitle()));

        for (int off : offsets) {
            int left = Math.max(0, off - 25);
            int right = Math.min(content.length(), off + query.length() + 25);
            TerminalIO.println(String.format("  [Offset %4d]: ...%s...", off, content.substring(left, right).replace('\n', ' ')));
        }

        askTechnicalDetails("Standard Suffix Array & Explicit Suffix Tree", query, content.length(), offsets.size(), ssa.getConstructionTimeNs(), "O(M log N) query, O(N log^2 N) construction");
        TerminalIO.pressEnterToContinue();
    }

    private static void compareReportsKasai(DataStore ds) {
        TerminalIO.printSubHeader("Cross-Report Similarity: Extract Longest Common Tactical Phrase");
        MatchDossier d1 = ds.getDossier("DOS-01");
        MatchDossier d2 = ds.getDossier("DOS-02");

        TerminalIO.println("Comparing Dossiers:");
        TerminalIO.println("  Report 1: " + d1.getTitle());
        TerminalIO.println("  Report 2: " + d2.getTitle() + "\n");

        var result = KasaiLcpEvaluator.findLongestCommonPhrase(d1.getTitle(), d1.getFullContent(), d2.getTitle(), d2.getFullContent());

        TerminalIO.println(TerminalIO.color(TerminalIO.BOLD + TerminalIO.GREEN, "Longest Common Tactical Phrase Identified:"));
        TerminalIO.println("  \"" + result.getLongestCommonPhrase() + "\"");
        TerminalIO.println(String.format("  Phrase Length: %d characters (Offsets: %d in Report 1, %d in Report 2)",
                result.getLcpLength(), result.getDocAOffset(), result.getDocBOffset()));

        askTechnicalDetails("Kasai's Algorithm for LCP Array Construction", "Common Phrase", d1.getFullContent().length() + d2.getFullContent().length(), 1, 0, "O(N) linear time LCP array calculation");
        TerminalIO.pressEnterToContinue();
    }

    private static void mineRepeatedPhrases(DataStore ds) {
        TerminalIO.printSubHeader("Mine Recurring Tactical Buzzwords via Suffix Automaton (DAWG)");
        String text = ds.getDossier("DOS-01").getFullContent();
        DirectedAcyclicWordGraph dawg = new DirectedAcyclicWordGraph(text);

        TerminalIO.println("Corpus Text Length: " + text.length() + " characters");
        TerminalIO.println("Suffix Automaton States: " + dawg.getStateCount());
        TerminalIO.println("Total Distinct Substrings Indexed: " + dawg.countDistinctSubstrings());

        String longestRepeated = dawg.findLongestRepeatedSubstring();
        TerminalIO.printSuccess("Longest Recurring Phrase Across Dossier:");
        TerminalIO.println("  \"" + longestRepeated.trim() + "\" (Length: " + longestRepeated.trim().length() + " characters)");

        askTechnicalDetails("Directed Acyclic Word Graph (DAWG / Suffix Automaton)", "Repeated Patterns", text.length(), (int) dawg.countDistinctSubstrings(), 0, "O(N) linear time construction with at most 2N-1 states");
        TerminalIO.pressEnterToContinue();
    }

    private static void benchmarkIndexing(DataStore ds) {
        TerminalIO.printSubHeader("Document Indexing Engine Benchmark: Suffix Array vs SA-IS (Linear Time)");
        StringBuilder corpus = new StringBuilder();
        for (MatchDossier d : ds.getAllDossiers()) {
            corpus.append(d.getFullContent()).append(" ");
        }
        String text = corpus.toString();

        StandardSuffixArray ssa = new StandardSuffixArray(text);
        InducedSortingSuffixArray sais = new InducedSortingSuffixArray(text);

        boolean identical = Arrays.equals(ssa.getSuffixArray(), sais.getSuffixArray());
        TerminalIO.println(String.format("Corpus Size: %d characters", text.length()));
        TerminalIO.println(String.format("Index Correctness Check: %s", identical ? "VERIFIED IDENTICAL [PASS]" : "MISMATCH"));

        String[] headers = new String[]{"Indexing Engine", "Asymptotic Complexity", "Time (µs)", "Speedup Factor"};
        List<String[]> rows = new ArrayList<>();
        double t1 = ssa.getConstructionTimeNs() / 1000.0;
        double t2 = sais.getConstructionTimeNs() / 1000.0;
        rows.add(new String[]{"Standard Suffix Array", "O(N log^2 N)", String.format("%.2f", t1), "1.00x"});
        rows.add(new String[]{"SA-IS (Induced Sorting)", "O(N) Linear Time", String.format("%.2f", t2), String.format("%.2fx", t1 / Math.max(1, t2))});
        TerminalIO.printTable(headers, rows);
        TerminalIO.pressEnterToContinue();
    }

    private static void askTechnicalDetails(String algorithm, String query, int corpusSize, int matches,
                                           long timeNs, String complexity) {
        boolean show = TerminalIO.readBoolean("\n[T] View technical DSA algorithmic details?");
        if (show) {
            TerminalIO.println(TerminalIO.color(TerminalIO.CYAN, "--------------------------------------------------------------------------------"));
            TerminalIO.println(TerminalIO.color(TerminalIO.BOLD + TerminalIO.CYAN, "  TECHNICAL DSA SUFFIX STRUCTURE PROFILE"));
            TerminalIO.println(TerminalIO.color(TerminalIO.CYAN, "--------------------------------------------------------------------------------"));
            TerminalIO.println("  Data Structure:        " + algorithm);
            TerminalIO.println("  Query Context:         \"" + query + "\"");
            TerminalIO.println("  Document Corpus Size:  " + corpusSize + " characters");
            TerminalIO.println("  Matches / Phrases:     " + matches);
            if (timeNs > 0) {
                TerminalIO.println(String.format("  Execution Time:        %.3f µs", timeNs / 1000.0));
            }
            TerminalIO.println("  Theoretical Bound:     " + complexity);
            TerminalIO.println("  Academic Module:       Module 2 (CO2 - Suffix Structures & Document Intelligence)");
            TerminalIO.println(TerminalIO.color(TerminalIO.CYAN, "--------------------------------------------------------------------------------"));
        }
    }
}
