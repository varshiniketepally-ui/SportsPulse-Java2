package org.sportspulse.platform.domain.model;

import java.io.Serializable;

/**
 * Domain model representing an individual sports athlete with physical,
 * technical, performance, and workload metrics.
 */
public class AthleteProfile implements Serializable, Comparable<AthleteProfile> {

    private String id;
    private String name;
    private int age;
    private AthletePosition primaryPosition;
    private AthletePosition secondaryPosition;
    private String franchiseId;

    // Performance & Condition metrics (0-100 scale)
    private double overallRating;
    private double fitnessScore;
    private double fatigueIndex;
    private double trainingScore;

    // Match statistics
    private int matchesPlayed;
    private int goals;
    private int assists;
    private int yellowCards;
    private int redCards;

    public AthleteProfile(String id, String name, int age, AthletePosition primaryPosition,
                          AthletePosition secondaryPosition, String franchiseId,
                          double overallRating, double fitnessScore, double fatigueIndex,
                          double trainingScore, int matchesPlayed, int goals, int assists,
                          int yellowCards, int redCards) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.primaryPosition = primaryPosition;
        this.secondaryPosition = secondaryPosition;
        this.franchiseId = franchiseId;
        this.overallRating = overallRating;
        this.fitnessScore = fitnessScore;
        this.fatigueIndex = fatigueIndex;
        this.trainingScore = trainingScore;
        this.matchesPlayed = matchesPlayed;
        this.goals = goals;
        this.assists = assists;
        this.yellowCards = yellowCards;
        this.redCards = redCards;
    }

    public AthleteProfile(String id, String name, int age, AthletePosition primaryPosition, String franchiseId) {
        this(id, name, age, primaryPosition, primaryPosition, franchiseId, 75.0, 85.0, 15.0, 80.0, 0, 0, 0, 0, 0);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public AthletePosition getPrimaryPosition() { return primaryPosition; }
    public void setPrimaryPosition(AthletePosition primaryPosition) { this.primaryPosition = primaryPosition; }

    public AthletePosition getSecondaryPosition() { return secondaryPosition; }
    public void setSecondaryPosition(AthletePosition secondaryPosition) { this.secondaryPosition = secondaryPosition; }

    public String getFranchiseId() { return franchiseId; }
    public void setFranchiseId(String franchiseId) { this.franchiseId = franchiseId; }

    public double getOverallRating() { return overallRating; }
    public void setOverallRating(double overallRating) { this.overallRating = Math.max(0.0, Math.min(100.0, overallRating)); }

    public double getFitnessScore() { return fitnessScore; }
    public void setFitnessScore(double fitnessScore) { this.fitnessScore = Math.max(0.0, Math.min(100.0, fitnessScore)); }

    public double getFatigueIndex() { return fatigueIndex; }
    public void setFatigueIndex(double fatigueIndex) { this.fatigueIndex = Math.max(0.0, Math.min(100.0, fatigueIndex)); }

    public double getTrainingScore() { return trainingScore; }
    public void setTrainingScore(double trainingScore) { this.trainingScore = Math.max(0.0, Math.min(100.0, trainingScore)); }

    public int getMatchesPlayed() { return matchesPlayed; }
    public void setMatchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; }

    public int getGoals() { return goals; }
    public void setGoals(int goals) { this.goals = goals; }

    public int getAssists() { return assists; }
    public void setAssists(int assists) { this.assists = assists; }

    public int getYellowCards() { return yellowCards; }
    public void setYellowCards(int yellowCards) { this.yellowCards = yellowCards; }

    public int getRedCards() { return redCards; }
    public void setRedCards(int redCards) { this.redCards = redCards; }

    /**
     * Effective Match Readiness score considering fitness and fatigue.
     */
    public double getEffectiveReadiness() {
        return Math.max(0.0, fitnessScore - (fatigueIndex * 0.5) + (trainingScore * 0.2));
    }

    @Override
    public int compareTo(AthleteProfile o) {
        // Natural sorting by overall rating descending
        return Double.compare(o.overallRating, this.overallRating);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s (%s) | Rating: %.1f | Fit: %.1f | Goals: %d | Team: %s",
                id, name, primaryPosition.getCode(), secondaryPosition.getCode(),
                overallRating, fitnessScore, goals, franchiseId);
    }
}
