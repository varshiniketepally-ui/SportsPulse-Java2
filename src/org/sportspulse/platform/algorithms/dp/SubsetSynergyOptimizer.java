package org.sportspulse.platform.algorithms.dp;

import org.sportspulse.platform.domain.model.AthletePosition;
import org.sportspulse.platform.domain.model.AthleteProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Advanced Dynamic Programming on Subsets for Squad Tactical Synergy Optimization.
 * Uses dp[mask] recurrence over player subsets to maximize technical readiness,
 * pairwise chemistry synergies, and tactical role fulfillment.
 */
public class SubsetSynergyOptimizer {

    public static class SynergyLineupResult {
        private final List<AthleteProfile> selectedSquad;
        private final double totalSynergyScore;
        private final double individualReadinessScore;
        private final double chemistryBonusScore;

        public SynergyLineupResult(List<AthleteProfile> squad, double total, double readiness, double chemistry) {
            this.selectedSquad = squad;
            this.totalSynergyScore = total;
            this.individualReadinessScore = readiness;
            this.chemistryBonusScore = chemistry;
        }

        public List<AthleteProfile> getSelectedSquad() { return selectedSquad; }
        public double getTotalSynergyScore() { return totalSynergyScore; }
        public double getIndividualReadinessScore() { return individualReadinessScore; }
        public double getChemistryBonusScore() { return chemistryBonusScore; }

        @Override
        public String toString() {
            return String.format("Total Synergy: %.2f (Readiness: %.2f, Chemistry: %.2f) | Squad Size: %d",
                    totalSynergyScore, individualReadinessScore, chemistryBonusScore, selectedSquad.size());
        }
    }

    /**
     * Finds the optimal squad of targetSize using Subset DP.
     */
    public static SynergyLineupResult optimizeLineup(List<AthleteProfile> candidates, int targetSize) {
        if (candidates == null || candidates.isEmpty() || targetSize <= 0) {
            return new SynergyLineupResult(new ArrayList<>(), 0.0, 0.0, 0.0);
        }

        int n = Math.min(candidates.size(), 16); // 2^16 = 65,536 states
        targetSize = Math.min(targetSize, n);

        int totalStates = 1 << n;
        double[] dp = new double[totalStates];
        int[] parent = new int[totalStates];
        Arrays.fill(dp, -1e9);
        Arrays.fill(parent, -1);

        dp[0] = 0.0;

        // Precompute pairwise chemistry matrix
        double[][] chemistry = new double[n][n];
        for (int i = 0; i < n; i++) {
            AthleteProfile a1 = candidates.get(i);
            for (int j = i + 1; j < n; j++) {
                AthleteProfile a2 = candidates.get(j);
                double bonus = 0.0;
                // Franchise familiarity
                if (a1.getFranchiseId() != null && a1.getFranchiseId().equalsIgnoreCase(a2.getFranchiseId())) {
                    bonus += 6.0;
                }
                // Positional synergy
                if ((a1.getPrimaryPosition() == AthletePosition.MIDFIELDER && a2.getPrimaryPosition() == AthletePosition.FORWARD) ||
                    (a1.getPrimaryPosition() == AthletePosition.FORWARD && a2.getPrimaryPosition() == AthletePosition.MIDFIELDER)) {
                    bonus += 4.5;
                }
                if ((a1.getPrimaryPosition() == AthletePosition.DEFENDER && a2.getPrimaryPosition() == AthletePosition.GOALKEEPER) ||
                    (a1.getPrimaryPosition() == AthletePosition.GOALKEEPER && a2.getPrimaryPosition() == AthletePosition.DEFENDER)) {
                    bonus += 4.0;
                }
                chemistry[i][j] = bonus;
                chemistry[j][i] = bonus;
            }
        }

        // DP state transitions
        for (int mask = 0; mask < totalStates; mask++) {
            if (dp[mask] < -1e8) continue;
            int count = Integer.bitCount(mask);
            if (count >= targetSize) continue;

            for (int next = 0; next < n; next++) {
                if ((mask & (1 << next)) == 0) {
                    int nextMask = mask | (1 << next);

                    // Marginal contribution of adding player 'next'
                    double marginal = candidates.get(next).getEffectiveReadiness();
                    for (int j = 0; j < n; j++) {
                        if ((mask & (1 << j)) != 0) {
                            marginal += chemistry[next][j];
                        }
                    }

                    if (dp[mask] + marginal > dp[nextMask]) {
                        dp[nextMask] = dp[mask] + marginal;
                        parent[nextMask] = mask;
                    }
                }
            }
        }

        // Find best mask with exact targetSize
        double bestScore = -1e9;
        int bestMask = 0;

        for (int mask = 0; mask < totalStates; mask++) {
            if (Integer.bitCount(mask) == targetSize && dp[mask] > bestScore) {
                bestScore = dp[mask];
                bestMask = mask;
            }
        }

        List<AthleteProfile> optimalSquad = new ArrayList<>();
        double totalReadiness = 0.0;
        double totalChemistry = 0.0;

        for (int i = 0; i < n; i++) {
            if ((bestMask & (1 << i)) != 0) {
                optimalSquad.add(candidates.get(i));
                totalReadiness += candidates.get(i).getEffectiveReadiness();
            }
        }

        for (int i = 0; i < n; i++) {
            if ((bestMask & (1 << i)) != 0) {
                for (int j = i + 1; j < n; j++) {
                    if ((bestMask & (1 << j)) != 0) {
                        totalChemistry += chemistry[i][j];
                    }
                }
            }
        }

        return new SynergyLineupResult(optimalSquad, bestScore, totalReadiness, totalChemistry);
    }
}
