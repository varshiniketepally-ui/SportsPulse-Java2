package com.sportspulse.cli;

import com.sportspulse.algo.dp.*;
import com.sportspulse.data.MockData;
import com.sportspulse.data.Sports;
import com.sportspulse.model.Player;
import com.sportspulse.model.Sport;
import com.sportspulse.util.Ansi;
import com.sportspulse.util.Console;

import java.util.*;

/**
 * Smart Squad Planner & Analytics Pipeline Ordering.
 * Powered by Module 3 Advanced Dynamic Programming:
 * - Bitmask DP (Subset Masking with Pairwise Synergy O(2^N * N))
 * - Subset DP / 0-1 Knapsack (Budget-Constrained Lineup Optimization O(N * Budget))
 * - Matrix Chain Multiplication (Optimal Parenthesization O(K^3))
 */
public class SquadBuilderMenu {
    private final MockData data;
    private final Scanner sc;
    private final Random rng = new Random(42);

    public SquadBuilderMenu(MockData data, Scanner sc) { this.data = data; this.sc = sc; }

    public void run() {
        boolean back = false;
        while (!back) {
            Console.header("SQUAD BUILDER & TACTICAL PLANNER", "Module 3 Dynamic Programming & Roster Optimization");
            Console.bullet("1. Best synergy-aware lineup for a sport  (Bitmask DP)");
            Console.bullet("2. Best squad under a budget cap  (DP on subsets / knapsack)");
            Console.bullet("3. Optimize analytics pipeline execution order  (Matrix-Chain Multiplication)");
            Console.bullet("0. Back to main menu");
            Console.prompt("\nSelect an option: ");
            switch (sc.nextLine().trim()) {
                case "1" -> bitmaskLineup();
                case "2" -> knapsackSquad();
                case "3" -> matrixChainDemo();
                case "0" -> back = true;
                default -> Console.error("Invalid option.");
            }
            if (!back) Console.pause(sc);
        }
    }

    private Sport pickSport() {
        System.out.println("Sports: 1. Football  2. Basketball  3. Cricket");
        System.out.print("Choose sport: ");
        return switch (sc.nextLine().trim()) {
            case "2" -> Sports.BASKETBALL;
            case "3" -> Sports.CRICKET;
            default -> Sports.FOOTBALL;
        };
    }

    private void bitmaskLineup() {
        Console.section("Bitmask DP: Synergy-Aware Lineup Selection");
        Sport sport = pickSport();
        List<Player> pool = data.playersBySport(sport);
        int k = Math.min(pool.size(), sport.getName().equals("Basketball") ? 3 : 5);
        System.out.print("How many players to select (max " + pool.size() + ", suggested " + k + "): ");
        String in = sc.nextLine().trim();
        int desiredK = parseIntOrDefault(in, k);
        if (desiredK < 1 || desiredK > pool.size()) { System.out.println("Out of range, using suggested " + k + "."); desiredK = k; }

        int n = pool.size();
        double[][] synergy = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double s = rng.nextDouble() * 5; // small pseudo-random synergy bonus, deterministic seed
                if (pool.get(i).getPosition().equals(pool.get(j).getPosition())) s -= 2; // discourage duplicate positions
                synergy[i][j] = s;
                synergy[j][i] = s;
            }
        }
        BitmaskDP.Result result = BitmaskDP.bestLineup(pool, desiredK, synergy);
        Console.kv("Optimal total score (skill + synergy)", String.format("%.2f", result.score));
        Console.section("Selected Starting Lineup");
        for (Player p : result.lineup) System.out.println("  " + p);

        maybeShowTechnicalDetails("Bitmask Dynamic Programming",
                "Evaluates exponential combinations via integer bit manipulation tracking chosen player subsets",
                n, "Optimal synergy score: " + String.format("%.2f", result.score),
                "O(2^N * N) exponential subset search", "O(2^N) 1D DP memoization array",
                "Integer bitmask representing active roster state");
    }

    private void knapsackSquad() {
        Console.section("DP on Subsets: Budget-Constrained Squad Selection");
        Sport sport = pickSport();
        List<Player> pool = data.playersBySport(sport);
        double avgCost = pool.stream().mapToDouble(Player::getCost).average().orElse(50);
        int suggestedBudget = (int) Math.round(avgCost * sport.getSquadSize() * 0.7);
        System.out.print("Enter total budget units (suggested ~" + suggestedBudget + "): ");
        String in = sc.nextLine().trim();
        int budget = parseIntOrDefault(in, suggestedBudget);
        if (budget < 0) { System.out.println("Invalid budget, using suggested " + suggestedBudget + "."); budget = suggestedBudget; }

        SubsetSelectionDP.Result result = SubsetSelectionDP.select(pool, budget);
        Console.kv("Total skill achieved", String.format("%.1f", result.totalSkill));
        Console.kv("Total cost used", String.format("%.1f / %d", result.totalCost, budget));
        Console.section("Selected Squad");
        for (Player p : result.selected) System.out.println("  " + p);

        maybeShowTechnicalDetails("DP on Subsets (0-1 Knapsack Principle)",
                "Maximizes cumulative team capability subject to strict financial salary/budget cap",
                pool.size(), "Selected " + result.selected.size() + " athletes under budget",
                "O(N * Budget) pseudo-polynomial time", "O(N * Budget) 2D DP table",
                "2D DP recurrence table dp[i][b] with backtracking reconstruction");
    }

    private void matrixChainDemo() {
        Console.section("Matrix-Chain Multiplication: Analytics Pipeline Ordering");
        System.out.println("Example pipeline: RawEvents -> PlayerAggregates -> NormalizedFeatures -> RankingScores -> Report");
        int[] dims = {5000, 40, 25, 15, 8}; // dims.length = stages+1
        String[] stages = {"RawEvents->PlayerAgg", "PlayerAgg->Normalized", "Normalized->RankingScores", "RankingScores->Report"};
        MatrixChain.Result result = MatrixChain.solve(dims, stages);
        Console.kv("Minimum scalar multiplications", result.minCost);
        Console.kv("Optimal execution order", result.order);
        System.out.println("\n  (Matrix dimensions: " + Arrays.toString(dims) + ")");

        maybeShowTechnicalDetails("Matrix Chain Multiplication DP",
                "Calculates optimal associative parenthesization minimizing scalar arithmetic multiplications",
                dims.length - 1, result.order + " (cost=" + result.minCost + ")",
                "O(K^3) where K is number of pipeline stages", "O(K^2) cost and split table",
                "2D upper-triangular DP cost table m[i][j] + split index table s[i][j]");
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

    private int parseIntOrDefault(String s, int fallback) {
        if (s.isBlank()) return fallback;
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return fallback; }
    }

    private String truncate(String s, int len) { return s.length() <= len ? s : s.substring(0, len - 3) + "..."; }
}
