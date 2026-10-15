package com.sportspulse.cli;

import com.sportspulse.algo.npc.*;
import com.sportspulse.data.MockData;
import com.sportspulse.data.Sports;
import com.sportspulse.model.Player;
import com.sportspulse.model.Sport;
import com.sportspulse.util.Ansi;
import com.sportspulse.util.Console;

import java.util.*;

/**
 * Player Compatibility & Squad Rule Analyzer.
 * Powered by Module 5 NP-Completeness, Karp Reductions & Approximation:
 * - DPLL Boolean SAT / 3-SAT Solver for tactical formation rules
 * - Karp Reduction Chain: 3-SAT -> CLIQUE -> INDEPENDENT SET -> VERTEX COVER
 * - Branch & Bound Exact Minimum Vertex Cover
 * - Polynomial-Time 2-Approximation Vertex Cover for conflict resolution
 */
public class SelectionAdvisorMenu {
    private final MockData data;
    private final Scanner sc;

    public SelectionAdvisorMenu(MockData data, Scanner sc) { this.data = data; this.sc = sc; }

    public void run() {
        boolean back = false;
        while (!back) {
            Console.header("SELECTION ADVISOR & SQUAD RULE ANALYZER", "Module 5 NP-Completeness & Reductions");
            Console.bullet("1. Verify tactical selection rules & availability  (SAT Solver)");
            Console.bullet("2. Discover largest mutually-compatible lineup core  (Max Clique)");
            Console.bullet("3. Identify largest conflict-free athlete roster  (Max Independent Set)");
            Console.bullet("4. Resolve dressing room friction - minimum benching  (Exact Min Vertex Cover)");
            Console.bullet("5. Fast tactical conflict resolution for large squads  (Vertex-Cover 2-Approximation)");
            Console.bullet("6. Academic reduction pipeline inspection  (3-SAT -> CLIQUE -> IS -> VC)");
            Console.bullet("0. Back to main menu");
            Console.prompt("\nSelect an option: ");
            switch (sc.nextLine().trim()) {
                case "1" -> satDemo();
                case "2" -> maxCliqueDemo();
                case "3" -> independentSetDemo();
                case "4" -> minVertexCoverDemo();
                case "5" -> vertexCoverApproxDemo();
                case "6" -> fullKarpReductionPipeline();
                case "0" -> back = true;
                default -> Console.error("Invalid option.");
            }
            if (!back) Console.pause(sc);
        }
    }

    private void satDemo() {
        Console.section("Tactical Roster Selection Rules (SAT Engine)");
        System.out.println("Checking compatibility against core tactical constraints:");
        System.out.println("  Constraint 1: Exactly one Goalkeeper starts (GK-A XOR GK-B)");
        System.out.println("  Constraint 2: Striker rotation policy (Striker-A and Striker-B cannot both start)");
        System.out.println("  Constraint 3: Mandatory attack presence (At least one Striker must start)");

        SATSolver solver = new SATSolver(4);
        solver.addClause(1, 2);   // at least one GK
        solver.addClause(-1, -2); // at most one GK
        solver.addClause(-3, -4); // at most one Striker
        solver.addClause(3, 4);   // at least one Striker
        Map<Integer, Boolean> assignment = solver.solve();

        if (assignment == null) {
            Console.error("UNSATISFIABLE: No valid lineup configuration satisfies tactical criteria.");
        } else {
            Console.success("SATISFIABLE: Tactical constraints successfully satisfied!\n");
            String[] roles = {"Goalkeeper A (Starter)", "Goalkeeper B (Backup)", "Striker A (Starter)", "Striker B (Rotation)"};
            for (int i = 1; i <= 4; i++) {
                boolean starts = assignment.getOrDefault(i, false);
                System.out.printf("  %-30s : %s%n", roles[i - 1], starts
                        ? Ansi.paint(Ansi.BRIGHT_GREEN, "[ SELECTED ]")
                        : Ansi.paint(Ansi.DIM, "[ BENCH ]"));
            }
        }

        maybeShowTechnicalDetails("DPLL Boolean SAT Solver",
                "Encodes managerial roster constraints into propositional Conjunctive Normal Form (CNF)",
                4, "Boolean Satisfiability Verdict: " + (assignment != null ? "SAT" : "UNSAT"),
                "O(2^V) worst-case, fast via unit propagation & pure literal elimination", "O(V + C) clause database",
                "CNF formula array list of signed integer literal clauses");
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

    private boolean[][] buildCompatibilityGraph(List<Player> players) {
        int n = players.size();
        boolean[][] adj = new boolean[n][n];
        Random localRng = new Random(players.size() * 31L + 7);
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                boolean samePosition = players.get(i).getPosition().equals(players.get(j).getPosition());
                boolean compatible = !samePosition && localRng.nextDouble() > 0.35;
                adj[i][j] = adj[j][i] = compatible;
            }
        }
        return adj;
    }

    private void maxCliqueDemo() {
        Console.section("High-Synergy Core Lineup (Maximum Clique)");
        Sport sport = pickSport();
        List<Player> players = data.playersBySport(sport);
        boolean[][] adj = buildCompatibilityGraph(players);
        List<Integer> clique = GraphReductions.maxClique(adj, players.size());
        Console.kv("Sport Analyzed", sport.getName());
        Console.kv("Maximum Mutually-Compatible Lineup Core Size", clique.size() + " athletes");
        Console.section("Compatible Squad Core");
        for (int idx : clique) System.out.println("  " + players.get(idx));

        maybeShowTechnicalDetails("Maximum Clique (Branch & Bound)",
                "Discovers the largest complete subgraph of mutually synergistic athletes",
                players.size(), "Max clique size: " + clique.size(),
                "NP-hard, O(2^V) branch & bound with bounding heuristics", "O(V^2) adjacency matrix",
                "Adjacency bit matrix with greedy pruning search tree");
    }

    private void independentSetDemo() {
        Console.section("Conflict-Free Roster Selection (Max Independent Set)");
        Sport sport = pickSport();
        List<Player> players = data.playersBySport(sport);
        boolean[][] conflictAdj = invert(buildCompatibilityGraph(players));
        List<Integer> is = GraphReductions.maxIndependentSet(conflictAdj, players.size());
        Console.kv("Sport Analyzed", sport.getName());
        Console.kv("Largest Conflict-Free Roster Group Size", is.size() + " athletes");
        Console.section("Conflict-Free Athletes");
        for (int idx : is) System.out.println("  " + players.get(idx));

        maybeShowTechnicalDetails("Maximum Independent Set",
                "Finds largest athlete subset with zero pairwise tactical friction (Clique on Complement)",
                players.size(), "Max independent set size: " + is.size(),
                "NP-hard, equivalent to Max Clique on complement conflict graph G'", "O(V^2) complement matrix",
                "Conflict adjacency matrix inverted to compatibility graph");
    }

    private void minVertexCoverDemo() {
        Console.section("Exact Conflict Elimination (Minimum Vertex Cover)");
        Sport sport = pickSport();
        List<Player> players = data.playersBySport(sport);
        boolean[][] conflictAdj = invert(buildCompatibilityGraph(players));
        List<Integer> cover = GraphReductions.minVertexCoverExact(conflictAdj, players.size());
        Console.kv("Sport Analyzed", sport.getName());
        Console.kv("Minimum Athletes to Rest/Bench to Eliminate ALL Tactical Conflicts", cover.size());
        Console.section("Recommended Athletes to Sub/Rest");
        for (int idx : cover) System.out.println("  " + players.get(idx));

        maybeShowTechnicalDetails("Minimum Vertex Cover (Exact via Duality)",
                "Smallest athlete set covering every friction edge (Duality: V \\ MIS = MVC)",
                players.size(), "Exact MVC size: " + cover.size(),
                "NP-hard, derived via Gallai's Theorem (|MIS| + |MVC| = |V|)", "O(V^2) graph representation",
                "Complement of Maximum Independent Set");
    }

    private void vertexCoverApproxDemo() {
        Console.section("Scalable Friction Resolution (Vertex-Cover 2-Approximation)");
        Sport sport = pickSport();
        List<Player> players = data.playersBySport(sport);
        boolean[][] conflictAdj = invert(buildCompatibilityGraph(players));
        long start = System.nanoTime();
        Set<Integer> cover = GraphReductions.vertexCover2Approx(conflictAdj, players.size());
        long elapsed = System.nanoTime() - start;

        List<Integer> exactCover = GraphReductions.minVertexCoverExact(conflictAdj, players.size());

        Console.kv("Sport Analyzed", sport.getName());
        Console.kv("2-Approximation Cover Size", cover.size() + " athletes");
        Console.kv("Exact Optimal Cover Size", exactCover.size() + " athletes");
        double ratio = exactCover.isEmpty() ? 1.0 : (double) cover.size() / exactCover.size();
        Console.kv("Empirical Approximation Ratio", String.format("%.2fx (guaranteed <= 2.0x)", ratio));
        Console.kv("Execution Time", String.format("%.3f µs", elapsed / 1000.0));

        Console.section("Approximate Athletes to Rotate/Bench");
        for (int idx : cover) System.out.println("  " + players.get(idx));

        maybeShowTechnicalDetails("Vertex Cover 2-Approximation",
                "Maximal matching heuristic selecting both endpoints of uncovered edges in O(V + E)",
                players.size(), "Cover size: " + cover.size() + " (ratio: " + String.format("%.2fx", ratio) + ")",
                "O(V + E) linear-time approximation", "O(V + E) edge coverage set",
                "Greedy edge deletion using LinkedHashSet");
    }

    private void fullKarpReductionPipeline() {
        Console.section("Academic Karp Reduction Pipeline: 3-SAT -> CLIQUE -> IS -> VC -> 2-APPROX");
        System.out.println("Demonstrating polynomial-time reductions proving NP-completeness across problem domains:\n");

        List<int[]> clauses = List.of(
                new int[]{1, 2, -3},
                new int[]{-1, 2, 3},
                new int[]{1, -2, 3}
        );
        System.out.println("Step 1 [3-SAT]: Formula (x1 v x2 v !x3) ^ (!x1 v x2 v x3) ^ (x1 v !x2 v x3)");

        ThreeSatToClique.Reduction r = ThreeSatToClique.reduce(clauses);
        System.out.println("Step 2 [CLIQUE]: Gadget constructed graph with " + r.nodes.size() + " vertices (target clique = " + r.targetCliqueSize + ")");
        List<Integer> clique = GraphReductions.maxClique(r.adjacency, r.nodes.size());
        System.out.println("         Max clique discovered: size = " + clique.size() + " " + (clique.size() >= r.targetCliqueSize ? "[SATISFIABLE]" : "[UNSAT]"));

        boolean[][] comp = GraphReductions.complement(r.adjacency, r.nodes.size());
        System.out.println("Step 3 [INDEPENDENT SET]: Inverting adjacency to complement graph G'");
        List<Integer> mis = GraphReductions.maxIndependentSet(comp, r.nodes.size());
        System.out.println("         Max independent set in G': size = " + mis.size());

        List<Integer> mvc = GraphReductions.minVertexCoverExact(comp, r.nodes.size());
        System.out.println("Step 4 [VERTEX COVER]: Min vertex cover = V \\ MIS (size = " + mvc.size() + ")");

        Set<Integer> approx = GraphReductions.vertexCover2Approx(comp, r.nodes.size());
        System.out.println("Step 5 [2-APPROXIMATION]: Fast 2-approx vertex cover on G': size = " + approx.size() + " (<= 2 * " + mvc.size() + ")");

        maybeShowTechnicalDetails("Karp NP-Complete Reduction Chain",
                "Textbook reduction gadget chain establishing computational equivalence among NP-complete problems",
                clauses.size() * 3, "3-SAT -> CLIQUE -> INDEPENDENT SET -> VERTEX COVER",
                "Polynomial-time reduction transformation O(V^2)", "O(V^2) gadget graphs",
                "Literal node tripartite gadgets with inter-clause consistency edges");
    }

    private boolean[][] invert(boolean[][] adj) {
        int n = adj.length;
        boolean[][] inv = new boolean[n][n];
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) if (i != j) inv[i][j] = !adj[i][j];
        return inv;
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
