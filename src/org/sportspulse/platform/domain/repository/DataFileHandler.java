package org.sportspulse.platform.domain.repository;

import org.sportspulse.platform.domain.model.*;

import java.io.*;
import java.nio.file.*;
import java.util.List;

/**
 * Portable relative file handling for SportsPulse.
 * Stores and loads records from "data/" using relative file paths only.
 */
public class DataFileHandler {

    private static final String DATA_DIR = "data";
    private static final String ATHLETES_FILE = DATA_DIR + File.separator + "athletes.csv";
    private static final String FRANCHISES_FILE = DATA_DIR + File.separator + "franchises.csv";
    private static final String FIXTURES_FILE = DATA_DIR + File.separator + "fixtures.csv";
    private static final String DOSSIERS_FILE = DATA_DIR + File.separator + "dossiers.txt";

    public static void initializeStorage() {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
            System.err.println("Could not initialize data directory: " + e.getMessage());
        }
    }

    public static boolean exportAll(DataStore ds) {
        initializeStorage();
        boolean success = true;
        success &= exportAthletes(ds.getAllAthletes());
        success &= exportFranchises(ds.getAllFranchises());
        success &= exportFixtures(ds.getAllFixtures());
        success &= exportDossiers(ds.getAllDossiers());
        return success;
    }

    public static boolean exportAthletes(List<AthleteProfile> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ATHLETES_FILE))) {
            pw.println("id,name,age,primaryPosition,secondaryPosition,franchiseId,overallRating,fitnessScore,fatigueIndex,trainingScore,matchesPlayed,goals,assists,yellowCards,redCards");
            for (AthleteProfile a : list) {
                pw.printf("%s,%s,%d,%s,%s,%s,%.1f,%.1f,%.1f,%.1f,%d,%d,%d,%d,%d%n",
                        a.getId(), a.getName(), a.getAge(),
                        a.getPrimaryPosition().name(), a.getSecondaryPosition().name(),
                        a.getFranchiseId(), a.getOverallRating(), a.getFitnessScore(),
                        a.getFatigueIndex(), a.getTrainingScore(), a.getMatchesPlayed(),
                        a.getGoals(), a.getAssists(), a.getYellowCards(), a.getRedCards());
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving athletes: " + e.getMessage());
            return false;
        }
    }

    public static boolean exportFranchises(List<SportsFranchise> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FRANCHISES_FILE))) {
            pw.println("id,name,city,stadium,headCoach,leagueId,points,wins,draws,losses");
            for (SportsFranchise f : list) {
                pw.printf("%s,%s,%s,%s,%s,%s,%d,%d,%d,%d%n",
                        f.getId(), f.getName(), f.getCity(), f.getStadium(),
                        f.getHeadCoach(), f.getLeagueId(), f.getPoints(),
                        f.getWins(), f.getDraws(), f.getLosses());
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving franchises: " + e.getMessage());
            return false;
        }
    }

    public static boolean exportFixtures(List<FixtureMatch> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FIXTURES_FILE))) {
            pw.println("id,leagueId,homeFranchiseId,awayFranchiseId,matchDate,venue,status,homeScore,awayScore");
            for (FixtureMatch m : list) {
                pw.printf("%s,%s,%s,%s,%s,%s,%s,%d,%d%n",
                        m.getId(), m.getLeagueId(), m.getHomeFranchiseId(),
                        m.getAwayFranchiseId(), m.getMatchDate(), m.getVenue(),
                        m.getStatus().name(), m.getHomeScore(), m.getAwayScore());
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving fixtures: " + e.getMessage());
            return false;
        }
    }

    public static boolean exportDossiers(List<MatchDossier> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DOSSIERS_FILE))) {
            for (MatchDossier d : list) {
                pw.println("--- DOSSIER START ---");
                pw.println("ID: " + d.getDossierId());
                pw.println("MATCH_ID: " + d.getMatchId());
                pw.println("TITLE: " + d.getTitle());
                pw.println("AUTHOR: " + d.getAuthor());
                pw.println("TACTICAL: " + d.getTacticalSummary());
                pw.println("SCOUT: " + d.getScoutObservations());
                pw.println("TAKEAWAYS: " + d.getKeyTakeaways());
                pw.println("--- DOSSIER END ---");
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving dossiers: " + e.getMessage());
            return false;
        }
    }

    public static boolean importAll(DataStore ds) {
        initializeStorage();
        boolean anyLoaded = false;
        File fAth = new File(ATHLETES_FILE);
        if (fAth.exists()) {
            importAthletes(ds);
            anyLoaded = true;
        }
        return anyLoaded;
    }

    public static void importAthletes(DataStore ds) {
        try (BufferedReader br = new BufferedReader(new FileReader(ATHLETES_FILE))) {
            String line = br.readLine(); // Header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 15) {
                    AthleteProfile a = new AthleteProfile(
                            parts[0], parts[1], Integer.parseInt(parts[2]),
                            AthletePosition.valueOf(parts[3]), AthletePosition.valueOf(parts[4]),
                            parts[5], Double.parseDouble(parts[6]), Double.parseDouble(parts[7]),
                            Double.parseDouble(parts[8]), Double.parseDouble(parts[9]),
                            Integer.parseInt(parts[10]), Integer.parseInt(parts[11]),
                            Integer.parseInt(parts[12]), Integer.parseInt(parts[13]),
                            Integer.parseInt(parts[14])
                    );
                    ds.saveAthlete(a);
                }
            }
        } catch (Exception e) {
            System.err.println("Error importing athletes: " + e.getMessage());
        }
    }
}
