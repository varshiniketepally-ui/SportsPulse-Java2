package org.sportspulse.platform.domain.model;

import java.io.Serializable;

/**
 * Domain model representing a granular in-game event/incident in a fixture.
 */
public class GameIncident implements Serializable {

    private String id;
    private String matchId;
    private int minute;
    private MatchIncidentType type;
    private String primaryAthleteId;
    private String secondaryAthleteId; // e.g. assist provider, fouled player, sub-in
    private String description;

    public GameIncident(String id, String matchId, int minute, MatchIncidentType type,
                        String primaryAthleteId, String secondaryAthleteId, String description) {
        this.id = id;
        this.matchId = matchId;
        this.minute = minute;
        this.type = type;
        this.primaryAthleteId = primaryAthleteId;
        this.secondaryAthleteId = secondaryAthleteId;
        this.description = description;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }

    public int getMinute() { return minute; }
    public void setMinute(int minute) { this.minute = minute; }

    public MatchIncidentType getType() { return type; }
    public void setType(MatchIncidentType type) { this.type = type; }

    public String getPrimaryAthleteId() { return primaryAthleteId; }
    public void setPrimaryAthleteId(String primaryAthleteId) { this.primaryAthleteId = primaryAthleteId; }

    public String getSecondaryAthleteId() { return secondaryAthleteId; }
    public void setSecondaryAthleteId(String secondaryAthleteId) { this.secondaryAthleteId = secondaryAthleteId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return String.format("[%d'] %s: %s (Actor: %s%s)",
                minute, type.getLabel(), description, primaryAthleteId,
                (secondaryAthleteId != null && !secondaryAthleteId.isEmpty()) ? ", Secondary: " + secondaryAthleteId : "");
    }
}
