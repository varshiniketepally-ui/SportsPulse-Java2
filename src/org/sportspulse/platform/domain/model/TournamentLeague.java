package org.sportspulse.platform.domain.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain model representing a Competition or Tournament League.
 */
public class TournamentLeague implements Serializable {

    private String id;
    private String name;
    private String sport;
    private String season;
    private final List<String> franchiseIds;

    public TournamentLeague(String id, String name, String sport, String season) {
        this.id = id;
        this.name = name;
        this.sport = sport;
        this.season = season;
        this.franchiseIds = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }

    public List<String> getFranchiseIds() { return franchiseIds; }

    public void addFranchise(String franchiseId) {
        if (!franchiseIds.contains(franchiseId)) {
            franchiseIds.add(franchiseId);
        }
    }

    public boolean removeFranchise(String franchiseId) {
        return franchiseIds.remove(franchiseId);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s, Season %s) - %d Teams",
                id, name, sport, season, franchiseIds.size());
    }
}
