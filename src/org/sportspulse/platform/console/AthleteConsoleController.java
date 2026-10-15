package org.sportspulse.platform.console;

import org.sportspulse.platform.algorithms.dp.EditDistanceEngine;
import org.sportspulse.platform.domain.model.AthletePosition;
import org.sportspulse.platform.domain.model.AthleteProfile;
import org.sportspulse.platform.domain.model.FitnessRegimen;
import org.sportspulse.platform.domain.repository.DataStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Console controller managing athlete profile operations, statistics,
 * comparisons, and fuzzy search.
 */
public class AthleteConsoleController {

    public static void showMenu() {
        DataStore ds = DataStore.getInstance();
        boolean back = false;

        while (!back) {
            TerminalIO.printHeader("ATHLETE & PLAYER MANAGEMENT");
            TerminalIO.println("  [1] List All Athletes");
            TerminalIO.println("  [2] Register New Athlete");
            TerminalIO.println("  [3] Update Athlete Profile & Readiness");
            TerminalIO.println("  [4] Remove Athlete");
            TerminalIO.println("  [5] Search Athlete (with Typo Correction)");
            TerminalIO.println("  [6] View Athlete Details & Fitness History");
            TerminalIO.println("  [7] Compare Two Athletes Side-by-Side");
            TerminalIO.println("  [8] Player Performance Rankings (Stochastic QuickSort)");
            TerminalIO.println("  [0] Return to Main Dashboard");

            int choice = TerminalIO.readInt("Select Option", 0, 8);
            switch (choice) {
                case 1 -> listAthletes(ds.getAllAthletes());
                case 2 -> addAthlete(ds);
                case 3 -> updateAthlete(ds);
                case 4 -> removeAthlete(ds);
                case 5 -> searchAthlete(ds);
                case 6 -> viewAthleteDetails(ds);
                case 7 -> compareAthletes(ds);
                case 8 -> rankAthletes(ds);
                case 0 -> back = true;
            }
        }
    }

    public static void listAthletes(List<AthleteProfile> athletes) {
        String[] headers = new String[]{"ID", "Name", "Age", "Pos", "Rating", "Fitness", "Fatigue", "Goals", "Team"};
        List<String[]> rows = new ArrayList<>();
        for (AthleteProfile a : athletes) {
            rows.add(new String[]{
                    a.getId(), a.getName(), String.valueOf(a.getAge()),
                    a.getPrimaryPosition().getCode(), String.format("%.1f", a.getOverallRating()),
                    String.format("%.1f", a.getFitnessScore()), String.format("%.1f", a.getFatigueIndex()),
                    String.valueOf(a.getGoals()), a.getFranchiseId()
            });
        }
        TerminalIO.printTable(headers, rows);
        TerminalIO.pressEnterToContinue();
    }

    private static void addAthlete(DataStore ds) {
        TerminalIO.printSubHeader("Register New Athlete");
        String id = "ATH-" + (ds.getAllAthletes().size() + 1);
        String name = TerminalIO.readNonEmptyString("Athlete Full Name");
        int age = TerminalIO.readInt("Athlete Age", 16, 45);

        TerminalIO.println("Positions: 1.GK  2.DEF  3.MID  4.FWD  5.WNG");
        int pChoice = TerminalIO.readInt("Primary Position", 1, 5);
        AthletePosition primary = AthletePosition.values()[pChoice - 1];

        String franchiseId = TerminalIO.readString("Franchise ID (e.g. FRAN-01 or leave blank)");
        if (franchiseId.isEmpty()) franchiseId = "FREE_AGENT";

        double rating = TerminalIO.readDouble("Initial Overall Rating", 50.0, 99.0);
        double fitness = TerminalIO.readDouble("Fitness Score", 40.0, 100.0);

        AthleteProfile athlete = new AthleteProfile(id, name, age, primary, franchiseId);
        athlete.setOverallRating(rating);
        athlete.setFitnessScore(fitness);
        ds.saveAthlete(athlete);

        var fran = ds.getFranchise(franchiseId);
        if (fran != null) {
            fran.addAthlete(id);
        }

        TerminalIO.printSuccess("Athlete registered successfully with ID: " + id);
        TerminalIO.pressEnterToContinue();
    }

    private static void updateAthlete(DataStore ds) {
        TerminalIO.printSubHeader("Update Athlete Profile");
        String id = TerminalIO.readNonEmptyString("Enter Athlete ID");
        AthleteProfile a = ds.getAthlete(id);
        if (a == null) {
            TerminalIO.printError("Athlete not found with ID: " + id);
            TerminalIO.pressEnterToContinue();
            return;
        }

        TerminalIO.println("Current Profile: " + a);
        double rating = TerminalIO.readDouble("New Overall Rating", 40.0, 99.0);
        double fitness = TerminalIO.readDouble("New Fitness Score", 30.0, 100.0);
        double fatigue = TerminalIO.readDouble("New Fatigue Index", 0.0, 100.0);
        int goals = TerminalIO.readInt("Total Goals Scored", 0, 100);

        a.setOverallRating(rating);
        a.setFitnessScore(fitness);
        a.setFatigueIndex(fatigue);
        a.setGoals(goals);

        TerminalIO.printSuccess("Athlete record updated. Effective Readiness: " + String.format("%.1f", a.getEffectiveReadiness()));
        TerminalIO.pressEnterToContinue();
    }

    private static void removeAthlete(DataStore ds) {
        TerminalIO.printSubHeader("Remove Athlete Record");
        String id = TerminalIO.readNonEmptyString("Enter Athlete ID to Remove");
        if (ds.removeAthlete(id)) {
            TerminalIO.printSuccess("Athlete " + id + " removed from system.");
        } else {
            TerminalIO.printError("Athlete not found.");
        }
        TerminalIO.pressEnterToContinue();
    }

    private static void searchAthlete(DataStore ds) {
        TerminalIO.printSubHeader("Athlete Search (with Typo Correction via Damerau-Levenshtein)");
        String query = TerminalIO.readNonEmptyString("Enter Athlete Name Query (e.g. 'Ronalod' or 'Sterling')");

        List<AthleteProfile> all = ds.getAllAthletes();
        List<String> names = new ArrayList<>();
        for (AthleteProfile a : all) {
            names.add(a.getName());
            String[] tokens = a.getName().split("\\s+");
            for (String t : tokens) {
                if (t.length() > 2 && !names.contains(t)) {
                    names.add(t);
                }
            }
        }

        String closest = EditDistanceEngine.findClosestEntity(query, names);
        AthleteProfile exactOrClosest = null;

        for (AthleteProfile a : all) {
            if (a.getName().equalsIgnoreCase(query)) {
                exactOrClosest = a;
                break;
            }
        }

        if (exactOrClosest != null) {
            TerminalIO.printSuccess("Exact Match Found:");
            TerminalIO.println("  " + exactOrClosest);
        } else if (closest != null) {
            int dist = EditDistanceEngine.computeDamerauLevenshtein(query, closest);
            TerminalIO.printWarning(String.format("No exact match. Closest athlete name: '%s' (Edit Distance: %d)", closest, dist));
            for (AthleteProfile a : all) {
                if (a.getName().equalsIgnoreCase(closest)) {
                    TerminalIO.println("  " + a);
                    break;
                }
            }
        } else {
            TerminalIO.printError("No matching athletes found.");
        }
        TerminalIO.pressEnterToContinue();
    }

    private static void viewAthleteDetails(DataStore ds) {
        TerminalIO.printSubHeader("Athlete Dossier & Fitness History");
        String id = TerminalIO.readNonEmptyString("Enter Athlete ID");
        AthleteProfile a = ds.getAthlete(id);
        if (a == null) {
            TerminalIO.printError("Athlete not found.");
            TerminalIO.pressEnterToContinue();
            return;
        }

        TerminalIO.println(TerminalIO.color(TerminalIO.BOLD + TerminalIO.CYAN, "Profile Overview:"));
        TerminalIO.println("  ID:                 " + a.getId());
        TerminalIO.println("  Name:               " + a.getName() + " (" + a.getAge() + " yrs)");
        TerminalIO.println("  Franchise:          " + a.getFranchiseId());
        TerminalIO.println("  Positions:          Primary: " + a.getPrimaryPosition() + ", Secondary: " + a.getSecondaryPosition());
        TerminalIO.println(String.format("  Technical Rating:   %.1f / 100", a.getOverallRating()));
        TerminalIO.println(String.format("  Fitness Score:      %.1f / 100", a.getFitnessScore()));
        TerminalIO.println(String.format("  Fatigue Index:      %.1f / 100", a.getFatigueIndex()));
        TerminalIO.println(String.format("  Training Score:     %.1f / 100", a.getTrainingScore()));
        TerminalIO.println(String.format("  Match Readiness:    %.1f / 100", a.getEffectiveReadiness()));
        TerminalIO.println("  Matches Played:     " + a.getMatchesPlayed());
        TerminalIO.println("  Goals / Assists:    " + a.getGoals() + " / " + a.getAssists());
        TerminalIO.println("  Discipline Cards:   Yellow: " + a.getYellowCards() + ", Red: " + a.getRedCards());

        List<FitnessRegimen> sessions = ds.getFitnessSessionsForAthlete(id);
        TerminalIO.println("\nTraining & Fitness Regimens (" + sessions.size() + " recorded):");
        if (sessions.isEmpty()) {
            TerminalIO.println("  (No individual training sessions recorded)");
        } else {
            for (FitnessRegimen s : sessions) {
                TerminalIO.println("  * " + s);
            }
        }
        TerminalIO.pressEnterToContinue();
    }

    private static void compareAthletes(DataStore ds) {
        TerminalIO.printSubHeader("Side-by-Side Athlete Comparison");
        String id1 = TerminalIO.readNonEmptyString("First Athlete ID");
        String id2 = TerminalIO.readNonEmptyString("Second Athlete ID");

        AthleteProfile a1 = ds.getAthlete(id1);
        AthleteProfile a2 = ds.getAthlete(id2);

        if (a1 == null || a2 == null) {
            TerminalIO.printError("One or both athletes could not be found.");
            TerminalIO.pressEnterToContinue();
            return;
        }

        String[] headers = new String[]{"Performance Metric", a1.getName(), a2.getName(), "Advantage"};
        List<String[]> rows = new ArrayList<>();
        rows.add(createCompRow("Overall Rating", a1.getOverallRating(), a2.getOverallRating(), a1.getName(), a2.getName()));
        rows.add(createCompRow("Fitness Score", a1.getFitnessScore(), a2.getFitnessScore(), a1.getName(), a2.getName()));
        rows.add(createCompRow("Match Readiness", a1.getEffectiveReadiness(), a2.getEffectiveReadiness(), a1.getName(), a2.getName()));
        rows.add(createCompRow("Fatigue (Lower Best)", a2.getFatigueIndex(), a1.getFatigueIndex(), a2.getName(), a1.getName()));
        rows.add(createCompRow("Goals Scored", a1.getGoals(), a2.getGoals(), a1.getName(), a2.getName()));
        rows.add(createCompRow("Assists Made", a1.getAssists(), a2.getAssists(), a1.getName(), a2.getName()));

        TerminalIO.printTable(headers, rows);
        TerminalIO.pressEnterToContinue();
    }

    private static void rankAthletes(DataStore ds) {
        TerminalIO.printSubHeader("Player Performance Rankings Leaderboard");
        List<AthleteProfile> list = new ArrayList<>(ds.getAllAthletes());
        TerminalIO.println("Rank Players By: 1.Technical Rating  2.Fitness Score  3.Goals Scored  4.Match Readiness");
        int crit = TerminalIO.readInt("Select Ranking Metric", 1, 4);

        java.util.Comparator<AthleteProfile> comp = switch (crit) {
            case 1 -> (a, b) -> Double.compare(b.getOverallRating(), a.getOverallRating());
            case 2 -> (a, b) -> Double.compare(b.getFitnessScore(), a.getFitnessScore());
            case 3 -> (a, b) -> Integer.compare(b.getGoals(), a.getGoals());
            default -> (a, b) -> Double.compare(b.getEffectiveReadiness(), a.getEffectiveReadiness());
        };

        long start = System.nanoTime();
        var metrics = org.sportspulse.platform.algorithms.randomized.StochasticQuickSort.sort(list, comp);
        long time = System.nanoTime() - start;

        TerminalIO.printSuccess(String.format("Ranked %d athletes using Stochastic QuickSort (Time: %.2f µs, Comparisons: %d, Swaps: %d):",
                list.size(), time / 1000.0, metrics.comparisons, metrics.swaps));
        listAthletes(list);
    }

    private static String[] createCompRow(String metric, double v1, double v2, String name1, String name2) {
        String adv = (v1 > v2) ? name1 : (v2 > v1) ? name2 : "EQUAL";
        return new String[]{metric, String.format("%.1f", v1), String.format("%.1f", v2), adv};
    }
}
