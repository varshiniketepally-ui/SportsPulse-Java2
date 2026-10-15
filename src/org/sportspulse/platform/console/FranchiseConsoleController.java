package org.sportspulse.platform.console;

import org.sportspulse.platform.domain.model.AthleteProfile;
import org.sportspulse.platform.domain.model.SportsFranchise;
import org.sportspulse.platform.domain.model.TournamentLeague;
import org.sportspulse.platform.domain.repository.DataStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Console controller for Team / Franchise and Competition League operations.
 */
public class FranchiseConsoleController {

    public static void showFranchiseMenu() {
        DataStore ds = DataStore.getInstance();
        boolean back = false;

        while (!back) {
            TerminalIO.printHeader("FRANCHISE & TEAM MANAGEMENT");
            TerminalIO.println("  [1] List All Franchises");
            TerminalIO.println("  [2] Create New Franchise");
            TerminalIO.println("  [3] View Franchise Roster & Tactics");
            TerminalIO.println("  [4] Transfer Athlete to Franchise");
            TerminalIO.println("  [5] Release Athlete from Franchise");
            TerminalIO.println("  [6] Compare Two Franchises");
            TerminalIO.println("  [0] Return to Main Dashboard");

            int choice = TerminalIO.readInt("Select Option", 0, 6);
            switch (choice) {
                case 1 -> listFranchises(ds.getAllFranchises());
                case 2 -> addFranchise(ds);
                case 3 -> viewFranchise(ds);
                case 4 -> transferAthlete(ds);
                case 5 -> releaseAthlete(ds);
                case 6 -> compareFranchises(ds);
                case 0 -> back = true;
            }
        }
    }

    public static void showCompetitionMenu() {
        DataStore ds = DataStore.getInstance();
        boolean back = false;

        while (!back) {
            TerminalIO.printHeader("LEAGUE & COMPETITION STANDINGS");
            TerminalIO.println("  [1] View All Active Leagues");
            TerminalIO.println("  [2] View League Standings Table");
            TerminalIO.println("  [0] Return to Main Dashboard");

            int choice = TerminalIO.readInt("Select Option", 0, 2);
            switch (choice) {
                case 1 -> listLeagues(ds.getAllLeagues());
                case 2 -> showLeagueTable(ds);
                case 0 -> back = true;
            }
        }
    }

    public static void listFranchises(List<SportsFranchise> franchises) {
        String[] headers = new String[]{"ID", "Name", "City", "Stadium", "Coach", "Pts", "W-D-L", "Roster Size"};
        List<String[]> rows = new ArrayList<>();
        for (SportsFranchise f : franchises) {
            rows.add(new String[]{
                    f.getId(), f.getName(), f.getCity(), f.getStadium(),
                    f.getHeadCoach(), String.valueOf(f.getPoints()),
                    f.getWins() + "-" + f.getDraws() + "-" + f.getLosses(),
                    String.valueOf(f.getRosterAthleteIds().size())
            });
        }
        TerminalIO.printTable(headers, rows);
        TerminalIO.pressEnterToContinue();
    }

    private static void addFranchise(DataStore ds) {
        TerminalIO.printSubHeader("Create New Franchise");
        String id = "FRAN-0" + (ds.getAllFranchises().size() + 1);
        String name = TerminalIO.readNonEmptyString("Franchise Name");
        String city = TerminalIO.readNonEmptyString("City");
        String stadium = TerminalIO.readNonEmptyString("Home Stadium");
        String coach = TerminalIO.readNonEmptyString("Head Coach");
        String leagueId = "LG-01";

        SportsFranchise f = new SportsFranchise(id, name, city, stadium, coach, leagueId);
        ds.saveFranchise(f);
        TerminalIO.printSuccess("Franchise created successfully with ID: " + id);
        TerminalIO.pressEnterToContinue();
    }

    private static void viewFranchise(DataStore ds) {
        TerminalIO.printSubHeader("Franchise Details & Squad Roster");
        String id = TerminalIO.readNonEmptyString("Enter Franchise ID (e.g. FRAN-01)");
        SportsFranchise f = ds.getFranchise(id);
        if (f == null) {
            TerminalIO.printError("Franchise not found.");
            TerminalIO.pressEnterToContinue();
            return;
        }

        TerminalIO.println(TerminalIO.color(TerminalIO.BOLD + TerminalIO.CYAN, "Franchise Information:"));
        TerminalIO.println("  Name:        " + f.getName());
        TerminalIO.println("  City/Venue:  " + f.getCity() + " | " + f.getStadium());
        TerminalIO.println("  Head Coach:  " + f.getHeadCoach());
        TerminalIO.println("  League ID:   " + f.getLeagueId());
        TerminalIO.println(String.format("  Record:      %d Points | W-D-L: %d-%d-%d | GD: %+d",
                f.getPoints(), f.getWins(), f.getDraws(), f.getLosses(), f.getGoalDifference()));

        TerminalIO.println("\nRegistered Athletes (" + f.getRosterAthleteIds().size() + "):");
        String[] headers = new String[]{"ID", "Name", "Pos", "Rating", "Fitness", "Readiness"};
        List<String[]> rows = new ArrayList<>();
        for (String athId : f.getRosterAthleteIds()) {
            AthleteProfile a = ds.getAthlete(athId);
            if (a != null) {
                rows.add(new String[]{
                        a.getId(), a.getName(), a.getPrimaryPosition().getCode(),
                        String.format("%.1f", a.getOverallRating()),
                        String.format("%.1f", a.getFitnessScore()),
                        String.format("%.1f", a.getEffectiveReadiness())
                });
            }
        }
        TerminalIO.printTable(headers, rows);
        TerminalIO.pressEnterToContinue();
    }

    private static void transferAthlete(DataStore ds) {
        TerminalIO.printSubHeader("Transfer Athlete to Franchise");
        String athId = TerminalIO.readNonEmptyString("Athlete ID (e.g. ATH-01)");
        String franId = TerminalIO.readNonEmptyString("Target Franchise ID (e.g. FRAN-02)");

        AthleteProfile a = ds.getAthlete(athId);
        SportsFranchise f = ds.getFranchise(franId);

        if (a == null || f == null) {
            TerminalIO.printError("Invalid athlete or franchise ID.");
            TerminalIO.pressEnterToContinue();
            return;
        }

        // Remove from old franchise if applicable
        if (a.getFranchiseId() != null) {
            SportsFranchise old = ds.getFranchise(a.getFranchiseId());
            if (old != null) old.removeAthlete(athId);
        }

        a.setFranchiseId(franId);
        f.addAthlete(athId);
        TerminalIO.printSuccess("Athlete " + a.getName() + " successfully transferred to " + f.getName());
        TerminalIO.pressEnterToContinue();
    }

    private static void releaseAthlete(DataStore ds) {
        TerminalIO.printSubHeader("Release Athlete from Franchise");
        String athId = TerminalIO.readNonEmptyString("Athlete ID");
        AthleteProfile a = ds.getAthlete(athId);
        if (a == null) {
            TerminalIO.printError("Athlete not found.");
            TerminalIO.pressEnterToContinue();
            return;
        }

        String oldFran = a.getFranchiseId();
        if (oldFran != null) {
            SportsFranchise f = ds.getFranchise(oldFran);
            if (f != null) f.removeAthlete(athId);
        }
        a.setFranchiseId("FREE_AGENT");
        TerminalIO.printSuccess("Athlete " + a.getName() + " is now a Free Agent.");
        TerminalIO.pressEnterToContinue();
    }

    private static void compareFranchises(DataStore ds) {
        TerminalIO.printSubHeader("Compare Two Franchises");
        String id1 = TerminalIO.readNonEmptyString("First Franchise ID");
        String id2 = TerminalIO.readNonEmptyString("Second Franchise ID");

        SportsFranchise f1 = ds.getFranchise(id1);
        SportsFranchise f2 = ds.getFranchise(id2);

        if (f1 == null || f2 == null) {
            TerminalIO.printError("One or both franchises could not be found.");
            TerminalIO.pressEnterToContinue();
            return;
        }

        String[] headers = new String[]{"Metric", f1.getName(), f2.getName(), "Leading"};
        List<String[]> rows = new ArrayList<>();
        rows.add(createRow("Points", f1.getPoints(), f2.getPoints(), f1.getName(), f2.getName()));
        rows.add(createRow("Wins", f1.getWins(), f2.getWins(), f1.getName(), f2.getName()));
        rows.add(createRow("Goals Scored", f1.getGoalsFor(), f2.getGoalsFor(), f1.getName(), f2.getName()));
        rows.add(createRow("Goal Difference", f1.getGoalDifference(), f2.getGoalDifference(), f1.getName(), f2.getName()));
        rows.add(createRow("Squad Size", f1.getRosterAthleteIds().size(), f2.getRosterAthleteIds().size(), f1.getName(), f2.getName()));

        TerminalIO.printTable(headers, rows);
        TerminalIO.pressEnterToContinue();
    }

    private static String[] createRow(String metric, int v1, int v2, String n1, String n2) {
        String lead = (v1 > v2) ? n1 : (v2 > v1) ? n2 : "TIED";
        return new String[]{metric, String.valueOf(v1), String.valueOf(v2), lead};
    }

    private static void listLeagues(List<TournamentLeague> leagues) {
        String[] headers = new String[]{"ID", "Competition Name", "Sport", "Season", "Teams"};
        List<String[]> rows = new ArrayList<>();
        for (TournamentLeague l : leagues) {
            rows.add(new String[]{
                    l.getId(), l.getName(), l.getSport(), l.getSeason(),
                    String.valueOf(l.getFranchiseIds().size())
            });
        }
        TerminalIO.printTable(headers, rows);
        TerminalIO.pressEnterToContinue();
    }

    private static void showLeagueTable(DataStore ds) {
        TerminalIO.printSubHeader("League Standings Table");
        List<SportsFranchise> list = new ArrayList<>(ds.getAllFranchises());
        // Sort by Points descending, then GD descending, then Wins descending
        list.sort((a, b) -> {
            if (b.getPoints() != a.getPoints()) return Integer.compare(b.getPoints(), a.getPoints());
            if (b.getGoalDifference() != a.getGoalDifference()) return Integer.compare(b.getGoalDifference(), a.getGoalDifference());
            return Integer.compare(b.getWins(), a.getWins());
        });

        String[] headers = new String[]{"Pos", "Team Name", "MP", "W", "D", "L", "GF", "GA", "GD", "Pts"};
        List<String[]> rows = new ArrayList<>();
        int rank = 1;
        for (SportsFranchise f : list) {
            rows.add(new String[]{
                    String.valueOf(rank++), f.getName(),
                    String.valueOf(f.getMatchesPlayed()),
                    String.valueOf(f.getWins()),
                    String.valueOf(f.getDraws()),
                    String.valueOf(f.getLosses()),
                    String.valueOf(f.getGoalsFor()),
                    String.valueOf(f.getGoalsAgainst()),
                    String.format("%+d", f.getGoalDifference()),
                    String.valueOf(f.getPoints())
            });
        }
        TerminalIO.printTable(headers, rows);
        TerminalIO.pressEnterToContinue();
    }
}
