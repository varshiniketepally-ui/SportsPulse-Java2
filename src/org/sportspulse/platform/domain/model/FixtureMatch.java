package org.sportspulse.platform.domain.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain model representing a competitive sports match / fixture.
 */
public class FixtureMatch implements Serializable {

    public enum MatchStatus {
        SCHEDULED,
        LIVE,
        CONCLUDED,
        POSTPONED
    }

    private String id;
    private String leagueId;
    private String homeFranchiseId;
    private String awayFranchiseId;
    private String matchDate;
    private String venue;
    private MatchStatus status;
    private int homeScore;
    private int awayScore;
    private final List<GameIncident> incidents;
    private String commentaryStream;

    public FixtureMatch(String id, String leagueId, String homeFranchiseId,
                        String awayFranchiseId, String matchDate, String venue) {
        this.id = id;
        this.leagueId = leagueId;
        this.homeFranchiseId = homeFranchiseId;
        this.awayFranchiseId = awayFranchiseId;
        this.matchDate = matchDate;
        this.venue = venue;
        this.status = MatchStatus.SCHEDULED;
        this.homeScore = 0;
        this.awayScore = 0;
        this.incidents = new ArrayList<>();
        this.commentaryStream = "";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLeagueId() { return leagueId; }
    public void setLeagueId(String leagueId) { this.leagueId = leagueId; }

    public String getHomeFranchiseId() { return homeFranchiseId; }
    public void setHomeFranchiseId(String homeFranchiseId) { this.homeFranchiseId = homeFranchiseId; }

    public String getAwayFranchiseId() { return awayFranchiseId; }
    public void setAwayFranchiseId(String awayFranchiseId) { this.awayFranchiseId = awayFranchiseId; }

    public String getMatchDate() { return matchDate; }
    public void setMatchDate(String matchDate) { this.matchDate = matchDate; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }

    public int getHomeScore() { return homeScore; }
    public void setHomeScore(int homeScore) { this.homeScore = homeScore; }

    public int getAwayScore() { return awayScore; }
    public void setAwayScore(int awayScore) { this.awayScore = awayScore; }

    public List<GameIncident> getIncidents() { return incidents; }

    public void addIncident(GameIncident incident) {
        this.incidents.add(incident);
        if (incident.getType() == MatchIncidentType.GOAL) {
            // Note: score attribution can be updated per event
        }
        appendCommentary("[" + incident.getMinute() + "'] " + incident.getDescription() + "\n");
    }

    public String getCommentaryStream() { return commentaryStream; }
    public void setCommentaryStream(String commentaryStream) { this.commentaryStream = commentaryStream; }

    public void appendCommentary(String text) {
        if (this.commentaryStream == null) {
            this.commentaryStream = text;
        } else {
            this.commentaryStream += text;
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %s vs %s | Score: %d-%d | Status: %s | Venue: %s | Date: %s",
                id, homeFranchiseId, awayFranchiseId, homeScore, awayScore, status, venue, matchDate);
    }
}
