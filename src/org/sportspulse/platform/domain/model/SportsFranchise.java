package org.sportspulse.platform.domain.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain model representing a Sports Franchise / Team.
 */
public class SportsFranchise implements Serializable {

    private String id;
    private String name;
    private String city;
    private String stadium;
    private String headCoach;
    private String leagueId;
    private final List<String> rosterAthleteIds;

    // League record
    private int matchesPlayed;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
    private int points;

    public SportsFranchise(String id, String name, String city, String stadium,
                           String headCoach, String leagueId) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.stadium = stadium;
        this.headCoach = headCoach;
        this.leagueId = leagueId;
        this.rosterAthleteIds = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStadium() { return stadium; }
    public void setStadium(String stadium) { this.stadium = stadium; }

    public String getHeadCoach() { return headCoach; }
    public void setHeadCoach(String headCoach) { this.headCoach = headCoach; }

    public String getLeagueId() { return leagueId; }
    public void setLeagueId(String leagueId) { this.leagueId = leagueId; }

    public List<String> getRosterAthleteIds() { return rosterAthleteIds; }

    public void addAthlete(String athleteId) {
        if (!rosterAthleteIds.contains(athleteId)) {
            rosterAthleteIds.add(athleteId);
        }
    }

    public boolean removeAthlete(String athleteId) {
        return rosterAthleteIds.remove(athleteId);
    }

    public int getMatchesPlayed() { return matchesPlayed; }
    public int getWins() { return wins; }
    public int getDraws() { return draws; }
    public int getLosses() { return losses; }
    public int getGoalsFor() { return goalsFor; }
    public int getGoalsAgainst() { return goalsAgainst; }
    public int getGoalDifference() { return goalsFor - goalsAgainst; }
    public int getPoints() { return points; }

    public void recordResult(int scored, int conceded) {
        matchesPlayed++;
        goalsFor += scored;
        goalsAgainst += conceded;
        if (scored > conceded) {
            wins++;
            points += 3;
        } else if (scored == conceded) {
            draws++;
            points += 1;
        } else {
            losses++;
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) | Coach: %s | Pts: %d | W-D-L: %d-%d-%d | Roster: %d",
                id, name, city, headCoach, points, wins, draws, losses, rosterAthleteIds.size());
    }
}
