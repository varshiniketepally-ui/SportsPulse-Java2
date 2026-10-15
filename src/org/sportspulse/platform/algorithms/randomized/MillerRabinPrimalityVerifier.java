package org.sportspulse.platform.algorithms.randomized;

import java.math.BigInteger;
import java.util.Random;

/**
 * Miller-Rabin Probabilistic Primality Testing Algorithm.
 * Academically demonstrates Monte Carlo randomized decision algorithms.
 * Error probability after K independent witness rounds: P(error) <= 4^(-K).
 */
public class MillerRabinPrimalityVerifier {

    public enum PrimalityResult {
        PROBABLE_PRIME,
        DEFINITELY_COMPOSITE
    }

    public static class VerificationReport {
        public final BigInteger number;
        public final PrimalityResult result;
        public final int witnessRounds;
        public final BigInteger compositeWitness;
        public final double errorBound;
        public final long executionTimeNs;

        public VerificationReport(BigInteger number, PrimalityResult result, int rounds,
                                  BigInteger witness, double errorBound, long time) {
            this.number = number;
            this.result = result;
            this.witnessRounds = rounds;
            this.compositeWitness = witness;
            this.errorBound = errorBound;
            this.executionTimeNs = time;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== MILLER-RABIN PRIMALITY VERIFICATION ===\n");
            sb.append("Target Number: ").append(number).append("\n");
            sb.append("Verdict:       ").append(result == PrimalityResult.PROBABLE_PRIME ? "PROBABLE PRIME" : "COMPOSITE").append("\n");
            sb.append("Witness Rounds Tested: ").append(witnessRounds).append("\n");
            if (result == PrimalityResult.PROBABLE_PRIME) {
                sb.append(String.format("Error Probability Bound: <= %.2e (4^-%d)\n", errorBound, witnessRounds));
            } else if (compositeWitness != null) {
                sb.append("Compositeness Witness Base: a = ").append(compositeWitness).append("\n");
            }
            sb.append(String.format("Execution Time: %.3f µs\n", executionTimeNs / 1000.0));
            sb.append("Note: Demonstrated for cryptographic verification and academic complexity.\n");
            return sb.toString();
        }
    }

    public static VerificationReport testPrimality(BigInteger n, int rounds, Random random) {
        long start = System.nanoTime();

        if (n.compareTo(BigInteger.TWO) < 0) {
            long t = System.nanoTime() - start;
            return new VerificationReport(n, PrimalityResult.DEFINITELY_COMPOSITE, 0, null, 1.0, t);
        }
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3))) {
            long t = System.nanoTime() - start;
            return new VerificationReport(n, PrimalityResult.PROBABLE_PRIME, rounds, null, 0.0, t);
        }
        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            long t = System.nanoTime() - start;
            return new VerificationReport(n, PrimalityResult.DEFINITELY_COMPOSITE, 1, BigInteger.TWO, 1.0, t);
        }

        // Factor n - 1 = 2^s * d with d odd
        BigInteger d = n.subtract(BigInteger.ONE);
        int s = 0;
        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            d = d.divide(BigInteger.TWO);
            s++;
        }

        BigInteger nMinusOne = n.subtract(BigInteger.ONE);
        BigInteger nMinusThree = n.subtract(BigInteger.valueOf(3));

        for (int r = 1; r <= rounds; r++) {
            // Generate random base a in [2, n - 2]
            BigInteger a;
            do {
                a = new BigInteger(n.bitLength(), random);
            } while (a.compareTo(BigInteger.TWO) < 0 || a.compareTo(nMinusOne) >= 0);

            // x = a^d mod n
            BigInteger x = a.modPow(d, n);

            if (x.equals(BigInteger.ONE) || x.equals(nMinusOne)) {
                continue;
            }

            boolean composite = true;
            for (int i = 0; i < s - 1; i++) {
                x = x.modPow(BigInteger.TWO, n);
                if (x.equals(nMinusOne)) {
                    composite = false;
                    break;
                }
            }

            if (composite) {
                long t = System.nanoTime() - start;
                return new VerificationReport(n, PrimalityResult.DEFINITELY_COMPOSITE, r, a, 1.0, t);
            }
        }

        double errorBound = Math.pow(0.25, rounds);
        long t = System.nanoTime() - start;
        return new VerificationReport(n, PrimalityResult.PROBABLE_PRIME, rounds, null, errorBound, t);
    }

    public static VerificationReport testPrimality(long n, int rounds) {
        return testPrimality(BigInteger.valueOf(n), rounds, new Random());
    }

    public static VerificationReport testPrimality(BigInteger n, int rounds) {
        return testPrimality(n, rounds, new Random());
    }
}
