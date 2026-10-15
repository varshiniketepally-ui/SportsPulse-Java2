package org.sportspulse.platform.algorithms.parallel;

import java.util.ArrayList;
import java.util.List;

/**
 * Brent's Scheduling Theorem & Work-Span Modeler.
 * Brent's Lemma establishes:
 * T(P) <= (W - S) / P + S <= W / P + S
 * Where:
 * - Work (W) = T(1): Total number of computational operations.
 * - Span (S) = T(infinity): Longest directed path of sequential dependencies (Critical Path).
 * - P: Number of parallel processing units.
 */
public class BrentTheoremWorkSpanModeler {

    public static class ProcessorScalePoint {
        public final int processors;
        public final double timeBound;
        public final double speedup;
        public final double efficiency;

        public ProcessorScalePoint(int p, double t, double s, double e) {
            this.processors = p;
            this.timeBound = t;
            this.speedup = s;
            this.efficiency = e;
        }
    }

    public static class BrentAnalysisReport {
        public final long work;
        public final long span;
        public final int targetProcessors;
        public final double executionTimeBound;
        public final double speedup;
        public final double efficiency;
        public final double maxTheoreticalSpeedup; // As P -> inf, speedup -> W / S
        public final List<ProcessorScalePoint> scalingCurve;

        public BrentAnalysisReport(long work, long span, int targetP, double timeBound,
                                   double speedup, double efficiency, double maxSpeedup,
                                   List<ProcessorScalePoint> curve) {
            this.work = work;
            this.span = span;
            this.targetProcessors = targetP;
            this.executionTimeBound = timeBound;
            this.speedup = speedup;
            this.efficiency = efficiency;
            this.maxTheoreticalSpeedup = maxSpeedup;
            this.scalingCurve = curve;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== BRENT'S THEOREM THEORETICAL PARALLEL ANALYSIS ===\n");
            sb.append(String.format("Total Work (W = T(1)):         %d operations\n", work));
            sb.append(String.format("Critical Path Span (S = T(∞)): %d sequential steps\n", span));
            sb.append(String.format("Maximum Theoretical Speedup:   %.2fx (Limit W / S)\n", maxTheoreticalSpeedup));
            sb.append(String.format("\nAnalysis for P = %d Processors:\n", targetProcessors));
            sb.append(String.format("  * Upper Time Bound T(P):     %.2f time units (via T(P) <= (W - S)/P + S)\n", executionTimeBound));
            sb.append(String.format("  * Theoretical Speedup S(P):  %.2fx\n", speedup));
            sb.append(String.format("  * Parallel Efficiency E(P):  %.1f%%\n\n", efficiency * 100.0));

            sb.append("Parallel Scaling Projection Across Hardware Configurations:\n");
            sb.append(String.format("%-12s | %-16s | %-12s | %-12s\n", "Processors", "Est. Time T(P)", "Speedup", "Efficiency"));
            sb.append("-------------+------------------+--------------+-------------\n");
            for (ProcessorScalePoint pt : scalingCurve) {
                sb.append(String.format("%-12d | %-16.2f | %-11.2fx | %-10.1f%%\n",
                        pt.processors, pt.timeBound, pt.speedup, pt.efficiency * 100.0));
            }
            sb.append("\nAcademic Meaning:\n");
            sb.append("  No matter how many processors are allocated, execution cannot complete faster than Span (S).\n");
            sb.append("  When P << (W / S), speedup is almost linear (Efficiency ~ 100%).\n");
            sb.append("  When P >> (W / S), diminishing returns occur as Span dominates execution.\n");
            return sb.toString();
        }
    }

    public static BrentAnalysisReport analyze(long work, long span, int processors) {
        if (work <= 0 || span <= 0 || processors <= 0) {
            throw new IllegalArgumentException("Work, Span, and Processors must all be positive.");
        }
        if (span > work) {
            span = work; // Span cannot exceed Work
        }

        double tP = ((double) (work - span) / processors) + span;
        double speedup = (double) work / tP;
        double efficiency = speedup / processors;
        double maxSpeedup = (double) work / span;

        List<ProcessorScalePoint> curve = new ArrayList<>();
        int[] testP = new int[]{1, 2, 4, 8, 16, 32, 64, 128};
        for (int p : testP) {
            double t = ((double) (work - span) / p) + span;
            double s = (double) work / t;
            double e = s / p;
            curve.add(new ProcessorScalePoint(p, t, s, e));
        }

        return new BrentAnalysisReport(work, span, processors, tP, speedup, efficiency, maxSpeedup, curve);
    }
}
