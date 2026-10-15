package org.sportspulse.platform.algorithms.dp;

import org.sportspulse.platform.domain.model.AthletePosition;
import org.sportspulse.platform.domain.model.AthleteProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Bitmask Dynamic Programming and State Enumeration for Squad Selection.
 * Evaluates combinations of players against strict positional quotas
 * and physical fitness threshold constraints.
 */
public class BitmaskRosterSelector {

    public static class SquadConstraint {
        public final int totalRequired;
        public final int reqGk;
        public final int reqDef;
        public final int reqMid;
        public final int reqFwd;
        public final int reqWng;
        public final double minFitnessThreshold;
        public final int maxLowFitnessPlayers;

        public SquadConstraint(int totalRequired, int reqGk, int reqDef, int reqMid,
                               int reqFwd, int reqWng, double minFitnessThreshold, int maxLowFitnessPlayers) {
            this.totalRequired = totalRequired;
            this.reqGk = reqGk;
            this.reqDef = reqDef;
            this.reqMid = reqMid;
            this.reqFwd = reqFwd;
            this.reqWng = reqWng;
            this.minFitnessThreshold = minFitnessThreshold;
            this.maxLowFitnessPlayers = maxLowFitnessPlayers;
        }

        public static SquadConstraint defaultFiveAside() {
            return new SquadConstraint(5, 1, 1, 2, 1, 0, 85.0, 1);
        }
    }

    public static class SelectedRoster {
        private final List<AthleteProfile> athletes;
        private final double totalRating;
        private final double averageFitness;
        private final int lowFitnessCount;

        public SelectedRoster(List<AthleteProfile> athletes, double totalRating,
                              double averageFitness, int lowFitnessCount) {
            this.athletes = athletes;
            this.totalRating = totalRating;
            this.averageFitness = averageFitness;
            this.lowFitnessCount = lowFitnessCount;
        }

        public List<AthleteProfile> getAthletes() { return athletes; }
        public double getTotalRating() { return totalRating; }
        public double getAverageFitness() { return averageFitness; }
        public int getLowFitnessCount() { return lowFitnessCount; }
    }

    /**
     * Evaluates all subsets of size constraint.totalRequired using bit manipulation.
     * Returns valid squad configurations satisfying positional and fitness rules.
     */
    public static List<SelectedRoster> findValidRosters(List<AthleteProfile> candidates, SquadConstraint constraint) {
        List<SelectedRoster> validRosters = new ArrayList<>();
        if (candidates == null || candidates.size() < constraint.totalRequired) {
            return validRosters;
        }

        int n = Math.min(candidates.size(), 20); // Cap at 20 for 2^N state limit
        int totalSubsets = 1 << n;

        for (int mask = 0; mask < totalSubsets; mask++) {
            if (Integer.bitCount(mask) != constraint.totalRequired) {
                continue;
            }

            int gk = 0, def = 0, mid = 0, fwd = 0, wng = 0;
            int lowFit = 0;
            double ratingSum = 0;
            double fitSum = 0;
            List<AthleteProfile> squad = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    AthleteProfile a = candidates.get(i);
                    squad.add(a);
                    ratingSum += a.getOverallRating();
                    fitSum += a.getFitnessScore();

                    if (a.getFitnessScore() < constraint.minFitnessThreshold) {
                        lowFit++;
                    }

                    if (a.getPrimaryPosition() == AthletePosition.GOALKEEPER) gk++;
                    else if (a.getPrimaryPosition() == AthletePosition.DEFENDER) def++;
                    else if (a.getPrimaryPosition() == AthletePosition.MIDFIELDER) mid++;
                    else if (a.getPrimaryPosition() == AthletePosition.FORWARD) fwd++;
                    else if (a.getPrimaryPosition() == AthletePosition.WINGER) wng++;
                }
            }

            // Verify constraints
            if (lowFit <= constraint.maxLowFitnessPlayers &&
                gk >= constraint.reqGk &&
                def >= constraint.reqDef &&
                mid >= constraint.reqMid &&
                fwd >= constraint.reqFwd &&
                wng >= constraint.reqWng) {

                validRosters.add(new SelectedRoster(squad, ratingSum, fitSum / constraint.totalRequired, lowFit));
            }
        }

        // Sort descending by total rating
        validRosters.sort((a, b) -> Double.compare(b.getTotalRating(), a.getTotalRating()));
        return validRosters;
    }
}
