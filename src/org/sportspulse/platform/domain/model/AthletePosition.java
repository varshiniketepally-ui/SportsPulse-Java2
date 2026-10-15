package org.sportspulse.platform.domain.model;

/**
 * Tactical positions for sports athletes.
 */
public enum AthletePosition {
    GOALKEEPER("GK"),
    DEFENDER("DEF"),
    MIDFIELDER("MID"),
    FORWARD("FWD"),
    WINGER("WNG");

    private final String code;

    AthletePosition(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
