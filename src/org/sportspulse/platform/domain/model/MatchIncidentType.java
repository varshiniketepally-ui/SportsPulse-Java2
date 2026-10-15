package org.sportspulse.platform.domain.model;

/**
 * Categorical types of game incidents recorded in match commentary streams.
 */
public enum MatchIncidentType {
    GOAL("Goal"),
    ASSIST("Assist"),
    SHOT("Shot on Target"),
    TACKLE("Defensive Tackle"),
    FOUL("Foul Committed"),
    CORNER("Corner Kick"),
    YELLOW_CARD("Yellow Card"),
    RED_CARD("Red Card"),
    SUBSTITUTION("Player Substitution"),
    PENALTY("Penalty Kick"),
    OFFSIDE("Offside Infraction");

    private final String label;

    MatchIncidentType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
