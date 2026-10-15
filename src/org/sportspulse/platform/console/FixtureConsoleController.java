package org.sportspulse.platform.console;

import org.sportspulse.platform.algorithms.strings.AhoCorasickEventAutomaton;
import org.sportspulse.platform.domain.model.*;
import org.sportspulse.platform.domain.repository.DataStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Console controller for Match Fixtures, Incident Logging, Live Commentary Streams,
 * and Training & Fitness management.
 */
public class FixtureConsoleController {

    public static void showFixtureMenu() {
        DataStore ds = DataStore.getInstance();
        boolean back = false;

        while (!back) {
            TerminalIO.printHeader("FIXTURE & MATCH INCIDENT OPERATIONS");
            TerminalIO.println("  [1] List All Fixtures");
            TerminalIO.println("  [2] Schedule New Match Fixture");
            TerminalIO.println("  [3] Record In-Game Incident (Goal, Foul, Card, Tackle)");
            TerminalIO.println("  [4] View Match Timeline & Commentary Stream");
            TerminalIO.println("  [5] Multi-Keyword Live Event Detector (Aho-Corasick)");
            TerminalIO.println("  [0] Return to Main Dashboard");

            int choice = TerminalIO.readInt("Select Option", 0, 5);
            switch (choice) {
                case 1 -> listFixtures(ds.getAllFixtures());
                case 2 -> scheduleFixture(ds);
                case 3 -> recordIncident(ds);
                case 4 -> viewMatchTimeline(ds);
                case 5 -> runAhoCorasickLiveDetector(ds);
                case 0 -> back = true;
            }
        }
    }

    public static void showFitnessMenu() {
        DataStore ds = DataStore.getInstance();
        boolean back = false;

        while (!back) {
            TerminalIO.printHeader("TRAINING, CONDITIONING & FITNESS LAB");
            TerminalIO.println("  [1] View All Recorded Fitness Observations");
            TerminalIO.println("  [2] Log New Conditioning Session");
            TerminalIO.println("  [3] High Workload Fatigue Warning Analysis");
            TerminalIO.println("  [0] Return to Main Dashboard");

            int choice = TerminalIO.readInt("Select Option", 0, 3);
            switch (choice) {
                case 1 -> listFitnessSessions(ds.getAllFitnessSessions());
                case 2 -> logFitnessSession(ds);
                case 3 -> analyzeFatigueWorkload(ds);
                case 0 -> back = true;
            }
        }
    }

    public static void listFixtures(List<FixtureMatch> fixtures) {
        String[] headers = new String[]{"Match ID", "Home Team", "Away Team", "Score", "Status", "Date", "Incidents"};
        List<String[]> rows = new ArrayList<>();
        for (FixtureMatch m : fixtures) {
            rows.add(new String[]{
                    m.getId(), m.getHomeFranchiseId(), m.getAwayFranchiseId(),
                    m.getHomeScore() + " - " + m.getAwayScore(),
                    m.getStatus().name(), m.getMatchDate(),
                    String.valueOf(m.getIncidents().size())
            });
        }
        TerminalIO.printTable(headers, rows);
        TerminalIO.pressEnterToContinue();
    }

    private static void scheduleFixture(DataStore ds) {
        TerminalIO.printSubHeader("Schedule New Match");
        String id = "MATCH-" + (ds.getAllFixtures().size() + 101);
        String home = TerminalIO.readNonEmptyString("Home Franchise ID (e.g. FRAN-01)");
        String away = TerminalIO.readNonEmptyString("Away Franchise ID (e.g. FRAN-02)");
        String date = TerminalIO.readNonEmptyString("Match Date (YYYY-MM-DD)");
        String venue = TerminalIO.readNonEmptyString("Venue / Stadium Name");

        FixtureMatch match = new FixtureMatch(id, "LG-01", home, away, date, venue);
        ds.saveFixture(match);
        TerminalIO.printSuccess("Match scheduled successfully with ID: " + id);
        TerminalIO.pressEnterToContinue();
    }

    private static void recordIncident(DataStore ds) {
        TerminalIO.printSubHeader("Record In-Game Match Incident");
        String matchId = TerminalIO.readNonEmptyString("Match ID (e.g. MATCH-101)");
        FixtureMatch match = ds.getFixture(matchId);
        if (match == null) {
            TerminalIO.printError("Match not found.");
            TerminalIO.pressEnterToContinue();
            return;
        }

        int minute = TerminalIO.readInt("Minute of Incident", 1, 120);
        TerminalIO.println("Incident Types:");
        MatchIncidentType[] types = MatchIncidentType.values();
        for (int i = 0; i < types.length; i++) {
            TerminalIO.println(String.format("  [%d] %s", i + 1, types[i].getLabel()));
        }
        int typeChoice = TerminalIO.readInt("Select Type", 1, types.length);
        MatchIncidentType selectedType = types[typeChoice - 1];

        String primaryActor = TerminalIO.readNonEmptyString("Primary Actor / Athlete ID (e.g. ATH-01)");
        String secondaryActor = TerminalIO.readString("Secondary Actor / Assist Provider ID (or blank)");
        String description = TerminalIO.readNonEmptyString("Commentary Description of Incident");

        String incId = "INC-" + (ds.getAllIncidents().size() + 1);
        GameIncident incident = new GameIncident(incId, matchId, minute, selectedType, primaryActor, secondaryActor, description);
        ds.recordIncident(incident);

        if (selectedType == MatchIncidentType.GOAL) {
            boolean isHome = TerminalIO.readBoolean("Was the goal scored by the home team (" + match.getHomeFranchiseId() + ")?");
            if (isHome) {
                match.setHomeScore(match.getHomeScore() + 1);
            } else {
                match.setAwayScore(match.getAwayScore() + 1);
            }
        }

        TerminalIO.printSuccess(String.format("Incident recorded! Current Score: %d - %d", match.getHomeScore(), match.getAwayScore()));
        TerminalIO.pressEnterToContinue();
    }

    private static void viewMatchTimeline(DataStore ds) {
        TerminalIO.printSubHeader("Match Timeline & Live Commentary");
        String matchId = TerminalIO.readNonEmptyString("Match ID (e.g. MATCH-101)");
        FixtureMatch match = ds.getFixture(matchId);
        if (match == null) {
            TerminalIO.printError("Match not found.");
            TerminalIO.pressEnterToContinue();
            return;
        }

        TerminalIO.println(TerminalIO.color(TerminalIO.BOLD + TerminalIO.CYAN,
                String.format("=== %s vs %s [%d - %d] | Venue: %s ===",
                        match.getHomeFranchiseId(), match.getAwayFranchiseId(),
                        match.getHomeScore(), match.getAwayScore(), match.getVenue())));

        TerminalIO.println("\nTimeline Events (" + match.getIncidents().size() + "):");
        for (GameIncident inc : match.getIncidents()) {
            TerminalIO.println(String.format("  * %s", inc));
        }

        TerminalIO.println("\nRaw Commentary Feed:");
        TerminalIO.println(match.getCommentaryStream().isEmpty() ? "  (No raw commentary feed recorded)" : match.getCommentaryStream());
        TerminalIO.pressEnterToContinue();
    }

    private static void runAhoCorasickLiveDetector(DataStore ds) {
        TerminalIO.printSubHeader("M1 Aho-Corasick Multi-Keyword Match Event Detector");
        String matchId = TerminalIO.readNonEmptyString("Match ID to Scan (e.g. MATCH-101)");
        FixtureMatch match = ds.getFixture(matchId);
        if (match == null) {
            TerminalIO.printError("Match not found.");
            TerminalIO.pressEnterToContinue();
            return;
        }

        List<String> keywords = Arrays.asList("goal", "tackle", "foul", "corner", "assist", "counter-attack", "caution", "strike");
        TerminalIO.println("Active Detection Keywords: " + keywords);

        AhoCorasickEventAutomaton ac = new AhoCorasickEventAutomaton(keywords);
        List<AhoCorasickEventAutomaton.MatchHit> hits = ac.search(match.getCommentaryStream());

        TerminalIO.println(String.format("\nDetected %d Critical Events in Single Pass O(N):", hits.size()));
        String[] headers = new String[]{"Keyword", "Span [Start..End]", "Context Snippet"};
        List<String[]> rows = new ArrayList<>();
        for (var h : hits) {
            rows.add(new String[]{
                    h.getKeyword().toUpperCase(),
                    "[" + h.getStartIndex() + ".." + h.getEndIndex() + "]",
                    h.getSurroundingSnippet()
            });
        }
        TerminalIO.printTable(headers, rows);
        TerminalIO.pressEnterToContinue();
    }

    private static void listFitnessSessions(List<FitnessRegimen> sessions) {
        String[] headers = new String[]{"Session ID", "Athlete", "Date", "Drill Type", "Mins", "Workload", "HR Avg", "Recovery"};
        List<String[]> rows = new ArrayList<>();
        for (FitnessRegimen s : sessions) {
            rows.add(new String[]{
                    s.getSessionId(), s.getAthleteId(), s.getSessionDate(),
                    s.getDrillType(), String.valueOf(s.getDurationMinutes()),
                    String.format("%.1f AU", s.getWorkloadUnits()),
                    s.getHeartRateAvg() + " bpm",
                    String.format("%.1f%%", s.getRecoveryIndex())
            });
        }
        TerminalIO.printTable(headers, rows);
        TerminalIO.pressEnterToContinue();
    }

    private static void logFitnessSession(DataStore ds) {
        TerminalIO.printSubHeader("Log Training / Conditioning Session");
        String athId = TerminalIO.readNonEmptyString("Athlete ID (e.g. ATH-01)");
        if (ds.getAthlete(athId) == null) {
            TerminalIO.printError("Athlete not registered.");
            TerminalIO.pressEnterToContinue();
            return;
        }

        String sid = "FIT-" + (ds.getAllFitnessSessions().size() + 1);
        String date = TerminalIO.readNonEmptyString("Session Date (YYYY-MM-DD)");
        String drill = TerminalIO.readNonEmptyString("Drill Type (e.g. Tactical Sprints, Aerobic Endurance)");
        int mins = TerminalIO.readInt("Duration Minutes", 15, 180);
        double workload = TerminalIO.readDouble("Workload Units (AU)", 50.0, 1000.0);
        int hr = TerminalIO.readInt("Average Heart Rate (bpm)", 60, 210);
        double recovery = TerminalIO.readDouble("Post-Session Recovery Score (%)", 0.0, 100.0);

        FitnessRegimen s = new FitnessRegimen(sid, athId, date, drill, mins, workload, hr, recovery);
        ds.saveFitnessSession(s);

        // Update athlete profile fitness & fatigue
        AthleteProfile a = ds.getAthlete(athId);
        a.setFitnessScore(Math.min(100.0, a.getFitnessScore() + (recovery * 0.05)));
        a.setFatigueIndex(Math.min(100.0, a.getFatigueIndex() + (workload / 25.0) - (recovery * 0.1)));

        TerminalIO.printSuccess("Conditioning session logged. Updated athlete readiness: " + String.format("%.1f", a.getEffectiveReadiness()));
        TerminalIO.pressEnterToContinue();
    }

    private static void analyzeFatigueWorkload(DataStore ds) {
        TerminalIO.printSubHeader("High Workload & Fatigue Risk Diagnostics");
        List<AthleteProfile> athletes = ds.getAllAthletes();
        String[] headers = new String[]{"Athlete ID", "Name", "Fitness", "Fatigue", "Readiness", "Status"};
        List<String[]> rows = new ArrayList<>();

        for (AthleteProfile a : athletes) {
            String status;
            if (a.getFatigueIndex() > 20.0 || a.getFitnessScore() < 80.0) {
                status = "ELEVATED RISK";
            } else if (a.getEffectiveReadiness() >= 85.0) {
                status = "OPTIMAL";
            } else {
                status = "NORMAL";
            }

            rows.add(new String[]{
                    a.getId(), a.getName(),
                    String.format("%.1f", a.getFitnessScore()),
                    String.format("%.1f", a.getFatigueIndex()),
                    String.format("%.1f", a.getEffectiveReadiness()),
                    status
            });
        }
        TerminalIO.printTable(headers, rows);
        TerminalIO.pressEnterToContinue();
    }
}
