package org.sportspulse.platform.domain.model;

import java.io.Serializable;

/**
 * Domain model representing an athletic training and fitness observation session.
 */
public class FitnessRegimen implements Serializable {

    private String sessionId;
    private String athleteId;
    private String sessionDate;
    private String drillType;
    private int durationMinutes;
    private double workloadUnits;
    private int heartRateAvg;
    private double recoveryIndex; // 0.0 to 100.0

    public FitnessRegimen(String sessionId, String athleteId, String sessionDate,
                          String drillType, int durationMinutes, double workloadUnits,
                          int heartRateAvg, double recoveryIndex) {
        this.sessionId = sessionId;
        this.athleteId = athleteId;
        this.sessionDate = sessionDate;
        this.drillType = drillType;
        this.durationMinutes = durationMinutes;
        this.workloadUnits = workloadUnits;
        this.heartRateAvg = heartRateAvg;
        this.recoveryIndex = recoveryIndex;
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getAthleteId() { return athleteId; }
    public void setAthleteId(String athleteId) { this.athleteId = athleteId; }

    public String getSessionDate() { return sessionDate; }
    public void setSessionDate(String sessionDate) { this.sessionDate = sessionDate; }

    public String getDrillType() { return drillType; }
    public void setDrillType(String drillType) { this.drillType = drillType; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public double getWorkloadUnits() { return workloadUnits; }
    public void setWorkloadUnits(double workloadUnits) { this.workloadUnits = workloadUnits; }

    public int getHeartRateAvg() { return heartRateAvg; }
    public void setHeartRateAvg(int heartRateAvg) { this.heartRateAvg = heartRateAvg; }

    public double getRecoveryIndex() { return recoveryIndex; }
    public void setRecoveryIndex(double recoveryIndex) { this.recoveryIndex = recoveryIndex; }

    @Override
    public String toString() {
        return String.format("[%s] Athlete: %s | Drill: %s (%d mins) | Workload: %.1f AU | HR: %d bpm | Recov: %.1f%%",
                sessionId, athleteId, drillType, durationMinutes, workloadUnits, heartRateAvg, recoveryIndex);
    }
}
