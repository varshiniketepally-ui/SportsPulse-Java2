package org.sportspulse.platform.algorithms.dp;

/**
 * Matrix Chain Multiplication DP for the Sports Analytics Pipeline Optimizer.
 * Minimizes total scalar multiplications across sequential matrix transformations
 * (e.g. Raw Data -> Feature Extraction -> Performance Matrix -> Comparison -> Analysis).
 * Time Complexity: O(K^3)
 * Space Complexity: O(K^2)
 */
public class MatrixChainAnalyticsOptimizer {

    public static class PipelineOptimizationResult {
        private final String[] matrixNames;
        private final int[] dimensions;
        private final long optimalScalarOperations;
        private final long naiveLeftToRightOperations;
        private final String optimalParenthesization;
        private final double efficiencyGainPercent;

        public PipelineOptimizationResult(String[] matrixNames, int[] dimensions,
                                          long optimalOps, long naiveOps,
                                          String parenthesization) {
            this.matrixNames = matrixNames;
            this.dimensions = dimensions;
            this.optimalScalarOperations = optimalOps;
            this.naiveLeftToRightOperations = naiveOps;
            this.optimalParenthesization = parenthesization;
            this.efficiencyGainPercent = naiveOps > 0 ? ((double) (naiveOps - optimalOps) / naiveOps) * 100.0 : 0.0;
        }

        public String[] getMatrixNames() { return matrixNames; }
        public int[] getDimensions() { return dimensions; }
        public long getOptimalScalarOperations() { return optimalScalarOperations; }
        public long getNaiveLeftToRightOperations() { return naiveLeftToRightOperations; }
        public String getOptimalParenthesization() { return optimalParenthesization; }
        public double getEfficiencyGainPercent() { return efficiencyGainPercent; }

        @Override
        public String toString() {
            return String.format("Optimal Plan: %s\nOptimal Cost: %d ops | Naive L-to-R: %d ops | Savings: %.2f%%",
                    optimalParenthesization, optimalScalarOperations, naiveLeftToRightOperations, efficiencyGainPercent);
        }
    }

    /**
     * Solves the Matrix Chain Multiplication problem.
     * dims: array of size n, representing n-1 matrices where matrix i has dims[i-1] x dims[i].
     */
    public static PipelineOptimizationResult optimize(String[] names, int[] dims) {
        int n = dims.length - 1; // Number of matrices
        long[][] m = new long[n + 1][n + 1];
        int[][] s = new int[n + 1][n + 1];

        // Chain length L from 2 to n
        for (int l = 2; l <= n; l++) {
            for (int i = 1; i <= n - l + 1; i++) {
                int j = i + l - 1;
                m[i][j] = Long.MAX_VALUE;

                for (int k = i; k < j; k++) {
                    long cost = m[i][k] + m[k + 1][j] + (long) dims[i - 1] * dims[k] * dims[j];
                    if (cost < m[i][j]) {
                        m[i][j] = cost;
                        s[i][j] = k;
                    }
                }
            }
        }

        long optimalCost = m[1][n];
        String parens = buildParenthesization(s, names, 1, n);

        // Compute naive Left-to-Right cost: (((A1 x A2) x A3) x ...)
        long naiveCost = 0;
        int currentRows = dims[0];
        int currentCols = dims[1];
        for (int i = 2; i <= n; i++) {
            naiveCost += (long) currentRows * currentCols * dims[i];
            currentCols = dims[i];
        }

        return new PipelineOptimizationResult(names, dims, optimalCost, naiveCost, parens);
    }

    private static String buildParenthesization(int[][] s, String[] names, int i, int j) {
        if (i == j) {
            return names[i - 1];
        }
        return "(" + buildParenthesization(s, names, i, s[i][j]) + " x " +
               buildParenthesization(s, names, s[i][j] + 1, j) + ")";
    }

    public static PipelineOptimizationResult optimizeDefaultSportsPipeline() {
        String[] defaultStages = new String[]{
                "RawMatchEvents",
                "FeatureExtraction",
                "PerformanceMatrix",
                "TeamComparison",
                "TacticalProjection"
        };
        // Dimensions: (1000 x 50) x (50 x 200) x (200 x 20) x (20 x 80) x (80 x 10)
        int[] dims = new int[]{1000, 50, 200, 20, 80, 10};
        return optimize(defaultStages, dims);
    }
}
