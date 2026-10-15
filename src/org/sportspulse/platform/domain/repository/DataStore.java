package org.sportspulse.platform.domain.repository;

import org.sportspulse.platform.datastructures.OpenAddressingHashTable;
import org.sportspulse.platform.domain.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Central In-Memory Storage & Repository using our Custom OpenAddressingHashTable.
 */
public class DataStore {

    private static DataStore instance;

    // Repositories backed by our Custom Hash Table
    private final OpenAddressingHashTable<String, AthleteProfile> athletes;
    private final OpenAddressingHashTable<String, SportsFranchise> franchises;
    private final OpenAddressingHashTable<String, TournamentLeague> leagues;
    private final OpenAddressingHashTable<String, FixtureMatch> fixtures;
    private final OpenAddressingHashTable<String, FitnessRegimen> fitnessSessions;
    private final OpenAddressingHashTable<String, MatchDossier> dossiers;
    private final OpenAddressingHashTable<String, TrainingFacility> facilities;

    // Linear collections for stream/time-series processing
    private final List<GameIncident> allIncidents;

    private DataStore() {
        this.athletes = new OpenAddressingHashTable<>(64);
        this.franchises = new OpenAddressingHashTable<>(32);
        this.leagues = new OpenAddressingHashTable<>(16);
        this.fixtures = new OpenAddressingHashTable<>(32);
        this.fitnessSessions = new OpenAddressingHashTable<>(64);
        this.dossiers = new OpenAddressingHashTable<>(32);
        this.facilities = new OpenAddressingHashTable<>(16);
        this.allIncidents = new ArrayList<>();
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
            SampleDataFactory.populate(instance);
        }
        return instance;
    }

    // Athletes
    public void saveAthlete(AthleteProfile athlete) {
        athletes.put(athlete.getId(), athlete);
    }

    public AthleteProfile getAthlete(String id) {
        return athletes.get(id);
    }

    public boolean removeAthlete(String id) {
        return athletes.remove(id);
    }

    public List<AthleteProfile> getAllAthletes() {
        return athletes.values();
    }

    public OpenAddressingHashTable<String, AthleteProfile> getAthletesTable() {
        return athletes;
    }

    // Franchises
    public void saveFranchise(SportsFranchise franchise) {
        franchises.put(franchise.getId(), franchise);
    }

    public SportsFranchise getFranchise(String id) {
        return franchises.get(id);
    }

    public boolean removeFranchise(String id) {
        return franchises.remove(id);
    }

    public List<SportsFranchise> getAllFranchises() {
        return franchises.values();
    }

    public OpenAddressingHashTable<String, SportsFranchise> getFranchisesTable() {
        return franchises;
    }

    // Leagues
    public void saveLeague(TournamentLeague league) {
        leagues.put(league.getId(), league);
    }

    public TournamentLeague getLeague(String id) {
        return leagues.get(id);
    }

    public List<TournamentLeague> getAllLeagues() {
        return leagues.values();
    }

    // Fixtures
    public void saveFixture(FixtureMatch match) {
        fixtures.put(match.getId(), match);
    }

    public FixtureMatch getFixture(String id) {
        return fixtures.get(id);
    }

    public List<FixtureMatch> getAllFixtures() {
        return fixtures.values();
    }

    // Fitness
    public void saveFitnessSession(FitnessRegimen session) {
        fitnessSessions.put(session.getSessionId(), session);
    }

    public FitnessRegimen getFitnessSession(String id) {
        return fitnessSessions.get(id);
    }

    public List<FitnessRegimen> getAllFitnessSessions() {
        return fitnessSessions.values();
    }

    public List<FitnessRegimen> getFitnessSessionsForAthlete(String athleteId) {
        List<FitnessRegimen> list = new ArrayList<>();
        for (FitnessRegimen s : fitnessSessions.values()) {
            if (s.getAthleteId().equalsIgnoreCase(athleteId)) {
                list.add(s);
            }
        }
        return list;
    }

    // Dossiers
    public void saveDossier(MatchDossier dossier) {
        dossiers.put(dossier.getDossierId(), dossier);
    }

    public MatchDossier getDossier(String id) {
        return dossiers.get(id);
    }

    public List<MatchDossier> getAllDossiers() {
        return dossiers.values();
    }

    // Facilities
    public void saveFacility(TrainingFacility facility) {
        facilities.put(facility.getId(), facility);
    }

    public TrainingFacility getFacility(String id) {
        return facilities.get(id);
    }

    public List<TrainingFacility> getAllFacilities() {
        return facilities.values();
    }

    // Incidents
    public void recordIncident(GameIncident incident) {
        allIncidents.add(incident);
        FixtureMatch match = getFixture(incident.getMatchId());
        if (match != null) {
            match.addIncident(incident);
        }
    }

    public List<GameIncident> getAllIncidents() {
        return allIncidents;
    }
}
