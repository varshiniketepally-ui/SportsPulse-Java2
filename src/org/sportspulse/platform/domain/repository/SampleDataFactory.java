package org.sportspulse.platform.domain.repository;

import org.sportspulse.platform.domain.model.*;

/**
 * Factory class generating rich, realistic sample data for the SportsPulse platform.
 */
public class SampleDataFactory {

    public static void populate(DataStore ds) {
        // 1. Leagues
        TournamentLeague l1 = new TournamentLeague("LG-01", "Continental Champions Cup", "Football", "2026/27");
        TournamentLeague l2 = new TournamentLeague("LG-02", "National Elite Premiership", "Football", "2026/27");
        ds.saveLeague(l1);
        ds.saveLeague(l2);

        // 2. Franchises
        SportsFranchise f1 = new SportsFranchise("FRAN-01", "Apex Titans FC", "Manchester", "Titans Arena", "Marcus Vance", "LG-01");
        SportsFranchise f2 = new SportsFranchise("FRAN-02", "Madrid Royals", "Madrid", "Estadio Imperial", "Carlos Mendoza", "LG-01");
        SportsFranchise f3 = new SportsFranchise("FRAN-03", "Munich Dynamos", "Munich", "Olympia Park", "Hans Gruber", "LG-02");
        SportsFranchise f4 = new SportsFranchise("FRAN-04", "London Strikers", "London", "Strikers Ground", "Arthur Pendelton", "LG-02");

        l1.addFranchise("FRAN-01");
        l1.addFranchise("FRAN-02");
        l2.addFranchise("FRAN-03");
        l2.addFranchise("FRAN-04");

        // 3. Athletes
        AthleteProfile[] athletes = new AthleteProfile[]{
                new AthleteProfile("ATH-01", "Leo Sterling", 28, AthletePosition.FORWARD, AthletePosition.WINGER, "FRAN-01", 91.5, 94.0, 12.0, 92.0, 24, 21, 9, 2, 0),
                new AthleteProfile("ATH-02", "Kevin De Vries", 31, AthletePosition.MIDFIELDER, AthletePosition.FORWARD, "FRAN-01", 93.0, 88.0, 18.0, 95.0, 26, 11, 19, 3, 0),
                new AthleteProfile("ATH-03", "Virgil Van Horn", 32, AthletePosition.DEFENDER, AthletePosition.DEFENDER, "FRAN-01", 89.5, 87.0, 14.0, 90.0, 25, 4, 2, 4, 0),
                new AthleteProfile("ATH-04", "Alisson Beckerly", 30, AthletePosition.GOALKEEPER, AthletePosition.GOALKEEPER, "FRAN-01", 90.0, 92.0, 8.0, 89.0, 26, 0, 1, 1, 0),
                new AthleteProfile("ATH-05", "Gabriel Martinelli", 24, AthletePosition.WINGER, AthletePosition.FORWARD, "FRAN-01", 86.5, 96.0, 9.0, 88.0, 22, 14, 8, 3, 0),
                new AthleteProfile("ATH-06", "Rodri Hernandez", 27, AthletePosition.MIDFIELDER, AthletePosition.DEFENDER, "FRAN-01", 92.0, 89.0, 16.0, 94.0, 26, 7, 10, 5, 0),
                new AthleteProfile("ATH-17", "Lionel Messi", 37, AthletePosition.FORWARD, AthletePosition.MIDFIELDER, "FRAN-01", 93.5, 82.0, 18.0, 95.0, 22, 19, 15, 1, 0),

                new AthleteProfile("ATH-07", "Kylian Mbappo", 26, AthletePosition.FORWARD, AthletePosition.WINGER, "FRAN-02", 94.5, 98.0, 10.0, 96.0, 25, 27, 8, 1, 0),
                new AthleteProfile("ATH-08", "Luka Modric", 38, AthletePosition.MIDFIELDER, AthletePosition.MIDFIELDER, "FRAN-02", 87.0, 78.0, 25.0, 91.0, 20, 3, 12, 2, 0),
                new AthleteProfile("ATH-09", "Antonio Rudiger", 31, AthletePosition.DEFENDER, AthletePosition.DEFENDER, "FRAN-02", 88.0, 91.0, 15.0, 89.0, 24, 2, 1, 7, 1),
                new AthleteProfile("ATH-10", "Thibaut Courtois", 32, AthletePosition.GOALKEEPER, AthletePosition.GOALKEEPER, "FRAN-02", 91.0, 85.0, 11.0, 88.0, 21, 0, 0, 0, 0),
                new AthleteProfile("ATH-11", "Vinicius Junior", 25, AthletePosition.WINGER, AthletePosition.FORWARD, "FRAN-02", 92.5, 95.0, 14.0, 93.0, 24, 18, 11, 4, 0),
                new AthleteProfile("ATH-12", "Jude Bellingham", 22, AthletePosition.MIDFIELDER, AthletePosition.FORWARD, "FRAN-02", 92.0, 93.0, 13.0, 92.0, 25, 16, 10, 4, 0),
                new AthleteProfile("ATH-18", "Cristiano Ronaldo", 39, AthletePosition.FORWARD, AthletePosition.FORWARD, "FRAN-02", 91.0, 88.0, 15.0, 96.0, 25, 26, 4, 3, 0),

                new AthleteProfile("ATH-13", "Harry Kane", 32, AthletePosition.FORWARD, AthletePosition.MIDFIELDER, "FRAN-03", 92.0, 86.0, 17.0, 93.0, 26, 28, 9, 2, 0),
                new AthleteProfile("ATH-14", "Joshua Kimmich", 29, AthletePosition.MIDFIELDER, AthletePosition.DEFENDER, "FRAN-03", 89.0, 91.0, 16.0, 92.0, 25, 5, 14, 5, 0),
                new AthleteProfile("ATH-15", "Manuel Neuer", 38, AthletePosition.GOALKEEPER, AthletePosition.GOALKEEPER, "FRAN-03", 88.0, 80.0, 20.0, 87.0, 22, 0, 0, 1, 0),
                new AthleteProfile("ATH-16", "Declan Rice", 26, AthletePosition.MIDFIELDER, AthletePosition.DEFENDER, "FRAN-04", 89.5, 94.0, 12.0, 91.0, 26, 6, 8, 3, 0)
        };

        for (AthleteProfile a : athletes) {
            ds.saveAthlete(a);
            SportsFranchise fran = ds.getFranchise(a.getFranchiseId());
            if (fran != null) {
                fran.addAthlete(a.getId());
            }
        }

        f1.recordResult(3, 1);
        f1.recordResult(2, 2);
        f2.recordResult(1, 3);
        f2.recordResult(4, 0);
        f3.recordResult(3, 0);
        f4.recordResult(1, 2);

        ds.saveFranchise(f1);
        ds.saveFranchise(f2);
        ds.saveFranchise(f3);
        ds.saveFranchise(f4);

        // 4. Fixtures
        FixtureMatch m1 = new FixtureMatch("MATCH-101", "LG-01", "FRAN-01", "FRAN-02", "2026-10-15", "Titans Arena");
        m1.setStatus(FixtureMatch.MatchStatus.CONCLUDED);
        m1.setHomeScore(3);
        m1.setAwayScore(1);

        GameIncident inc1 = new GameIncident("INC-01", "MATCH-101", 14, MatchIncidentType.GOAL, "ATH-01", "ATH-02", "Thunderous strike from 20 yards after crisp tactical pass");
        GameIncident inc2 = new GameIncident("INC-02", "MATCH-101", 28, MatchIncidentType.FOUL, "ATH-09", "ATH-01", "Hard tactical tackle interrupting high-speed counter attack");
        GameIncident inc3 = new GameIncident("INC-03", "MATCH-101", 33, MatchIncidentType.YELLOW_CARD, "ATH-09", "", "Caution issued for repeated persistent foul play");
        GameIncident inc4 = new GameIncident("INC-04", "MATCH-101", 52, MatchIncidentType.GOAL, "ATH-07", "ATH-12", "Solo sprint cutting inside to slot into bottom corner");
        GameIncident inc5 = new GameIncident("INC-05", "MATCH-101", 67, MatchIncidentType.GOAL, "ATH-05", "ATH-06", "Header from near post corner kick routine");
        GameIncident inc6 = new GameIncident("INC-06", "MATCH-101", 84, MatchIncidentType.GOAL, "ATH-01", "ATH-02", "Fast break counter attack rounding the goalkeeper");

        ds.saveFixture(m1);
        ds.recordIncident(inc1);
        ds.recordIncident(inc2);
        ds.recordIncident(inc3);
        ds.recordIncident(inc4);
        ds.recordIncident(inc5);
        ds.recordIncident(inc6);

        FixtureMatch m2 = new FixtureMatch("MATCH-102", "LG-02", "FRAN-03", "FRAN-04", "2026-10-18", "Olympia Park");
        m2.setStatus(FixtureMatch.MatchStatus.CONCLUDED);
        m2.setHomeScore(3);
        m2.setAwayScore(0);
        GameIncident inc7 = new GameIncident("INC-07", "MATCH-102", 21, MatchIncidentType.GOAL, "ATH-13", "ATH-14", "Penalty kick converted with pinpoint accuracy");
        GameIncident inc8 = new GameIncident("INC-08", "MATCH-102", 55, MatchIncidentType.TACKLE, "ATH-16", "ATH-13", "Crucial sliding challenge on edge of the box");
        GameIncident inc9 = new GameIncident("INC-09", "MATCH-102", 72, MatchIncidentType.GOAL, "ATH-13", "", "Header off cross into the top corner");

        ds.saveFixture(m2);
        ds.recordIncident(inc7);
        ds.recordIncident(inc8);
        ds.recordIncident(inc9);

        // 5. Training Facilities
        ds.saveFacility(new TrainingFacility("FAC-01", "North Pitch Tactical Field", TrainingFacility.FacilityType.TACTICAL_PITCH, 22));
        ds.saveFacility(new TrainingFacility("FAC-02", "Olympic High Performance Gym", TrainingFacility.FacilityType.PERFORMANCE_GYM, 15));
        ds.saveFacility(new TrainingFacility("FAC-03", "Cryo & Hydrotherapy Wellness Center", TrainingFacility.FacilityType.HYDRO_RECOVERY, 8));
        ds.saveFacility(new TrainingFacility("FAC-04", "Kinematic Biomechanics Lab", TrainingFacility.FacilityType.BIOMECHANICS_LAB, 6));
        ds.saveFacility(new TrainingFacility("FAC-05", "Elite Medical Bay", TrainingFacility.FacilityType.MEDICAL_BAY, 5));

        // 6. Fitness Sessions
        ds.saveFitnessSession(new FitnessRegimen("FIT-01", "ATH-01", "2026-10-10", "High Intensity Sprint Intervals", 60, 480.0, 168, 92.0));
        ds.saveFitnessSession(new FitnessRegimen("FIT-02", "ATH-02", "2026-10-10", "Tactical Positioning & Endurance", 75, 420.0, 155, 87.0));
        ds.saveFitnessSession(new FitnessRegimen("FIT-03", "ATH-07", "2026-10-11", "Acceleration & Power Plyometrics", 45, 510.0, 174, 94.0));
        ds.saveFitnessSession(new FitnessRegimen("FIT-04", "ATH-08", "2026-10-11", "Recovery Aerobic & Flexibility", 40, 220.0, 130, 84.0));

        // 7. Match Dossiers
        ds.saveDossier(new MatchDossier("DOS-01", "MATCH-101", "Champions Cup Match Analysis: Apex Titans vs Madrid Royals",
                "Chief Analyst Dr. Elena Rostova",
                "Apex Titans displayed high pressing transition and clinical counter attack precision. Defensive overload in midfield suffocated Madrid's playmakers.",
                "Virgil Van Horn maintained superior aerial duel supremacy. Leo Sterling demonstrated lethal finishing efficiency on counter attacks.",
                "Tactical discipline and quick vertical passing created recurring defensive fractures in Madrid's backline. High stamina and low fatigue were decisive factors."));

        ds.saveDossier(new MatchDossier("DOS-02", "MATCH-101", "Tactical Scouting Dossier: Madrid Royals Structural Flaws",
                "Tactical Scout Jean-Luc Picard",
                "Madrid Royals suffered from counter-attacking vulnerability whenever their fullbacks pushed forward into attacking thirds.",
                "Antonio Rudiger aggressive challenge patterns resulted in early disciplinary caution, restricting defensive aggression later.",
                "High pressing transition from opposition exposed slow central pivot recovery. Tactical reorganization required before second leg."));

        ds.saveDossier(new MatchDossier("DOS-03", "MATCH-102", "Performance Dossier: Munich Dynamos Pressing Masterclass",
                "Performance Director Wolfgang Meyer",
                "Munich Dynamos applied relentless zonal pressing from the opening whistle. Declan Rice fought admirably but was isolated.",
                "Harry Kane showed exceptional spatial awareness and spatial intelligence, dropping deep to link midfield transitions.",
                "Physical stamina depletion in second half doomed London Strikers to heavy defeat. Tactical discipline remained solid."));
    }
}
