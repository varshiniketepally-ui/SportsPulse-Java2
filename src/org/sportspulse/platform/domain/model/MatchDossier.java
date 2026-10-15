package org.sportspulse.platform.domain.model;

import java.io.Serializable;

/**
 * Domain model representing sports scouting dossiers, tactical match reports,
 * and performance narratives.
 */
public class MatchDossier implements Serializable {

    private String dossierId;
    private String matchId;
    private String title;
    private String author;
    private String tacticalSummary;
    private String scoutObservations;
    private String keyTakeaways;

    public MatchDossier(String dossierId, String matchId, String title, String author,
                        String tacticalSummary, String scoutObservations, String keyTakeaways) {
        this.dossierId = dossierId;
        this.matchId = matchId;
        this.title = title;
        this.author = author;
        this.tacticalSummary = tacticalSummary;
        this.scoutObservations = scoutObservations;
        this.keyTakeaways = keyTakeaways;
    }

    public String getDossierId() { return dossierId; }
    public void setDossierId(String dossierId) { this.dossierId = dossierId; }

    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getTacticalSummary() { return tacticalSummary; }
    public void setTacticalSummary(String tacticalSummary) { this.tacticalSummary = tacticalSummary; }

    public String getScoutObservations() { return scoutObservations; }
    public void setScoutObservations(String scoutObservations) { this.scoutObservations = scoutObservations; }

    public String getKeyTakeaways() { return keyTakeaways; }
    public void setKeyTakeaways(String keyTakeaways) { this.keyTakeaways = keyTakeaways; }

    public String getFullContent() {
        return title + "\n" + tacticalSummary + "\n" + scoutObservations + "\n" + keyTakeaways;
    }

    @Override
    public String toString() {
        return String.format("[%s] \"%s\" by %s (Match: %s)",
                dossierId, title, author, matchId);
    }
}
