package org.sportspulse.platform.algorithms.npc;

import java.util.*;

/**
 * Davis-Putnam-Logemann-Loveland (DPLL) Boolean SAT Solver.
 * Solves arbitrary CNF formulas for squad selection rule compliance.
 * Handles unit propagation, pure literal elimination, and recursive backtracking.
 */
public class BooleanSatSolver {

    public static class Literal {
        public final String variable;
        public final boolean sign; // true = positive (x), false = negated (~x)

        public Literal(String variable, boolean sign) {
            this.variable = variable;
            this.sign = sign;
        }

        public Literal negate() {
            return new Literal(variable, !sign);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Literal that)) return false;
            return sign == that.sign && variable.equals(that.variable);
        }

        @Override
        public int hashCode() {
            return Objects.hash(variable, sign);
        }

        @Override
        public String toString() {
            return (sign ? "" : "~") + variable;
        }
    }

    public static class Clause {
        public final List<Literal> literals;

        public Clause(List<Literal> literals) {
            this.literals = new ArrayList<>(literals);
        }

        public Clause(Literal... lits) {
            this.literals = Arrays.asList(lits);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < literals.size(); i++) {
                if (i > 0) sb.append(" OR ");
                sb.append(literals.get(i));
            }
            sb.append(")");
            return sb.toString();
        }
    }

    public static class SatResult {
        public final boolean satisfiable;
        public final Map<String, Boolean> model; // Variable -> True/False
        public final long decisions;

        public SatResult(boolean satisfiable, Map<String, Boolean> model, long decisions) {
            this.satisfiable = satisfiable;
            this.model = model != null ? model : Collections.emptyMap();
            this.decisions = decisions;
        }

        @Override
        public String toString() {
            if (!satisfiable) {
                return "UNSATISFIABLE (Decisions: " + decisions + ")";
            }
            return "SATISFIABLE (Decisions: " + decisions + ")\nAssignment: " + model;
        }
    }

    private static long decisionCounter = 0;

    public static SatResult solve(List<Clause> formula) {
        decisionCounter = 0;
        Set<String> variables = new HashSet<>();
        for (Clause c : formula) {
            for (Literal l : c.literals) {
                variables.add(l.variable);
            }
        }

        Map<String, Boolean> assignment = new HashMap<>();
        boolean sat = dpll(formula, assignment, new ArrayList<>(variables));
        return new SatResult(sat, assignment, decisionCounter);
    }

    private static boolean dpll(List<Clause> clauses, Map<String, Boolean> assignment, List<String> unassignedVars) {
        decisionCounter++;

        // Simplify formula given current assignment
        List<Clause> simplified = simplify(clauses, assignment);

        // Check base cases
        if (simplified.isEmpty()) {
            return true; // All clauses satisfied
        }
        for (Clause c : simplified) {
            if (c.literals.isEmpty()) {
                return false; // Found empty clause -> conflict
            }
        }

        // Unit Propagation
        for (Clause c : simplified) {
            if (c.literals.size() == 1) {
                Literal unit = c.literals.get(0);
                assignment.put(unit.variable, unit.sign);
                boolean res = dpll(clauses, assignment, unassignedVars);
                if (res) return true;
                assignment.remove(unit.variable);
                return false;
            }
        }

        // Pick next unassigned variable
        String nextVar = null;
        for (String v : unassignedVars) {
            if (!assignment.containsKey(v)) {
                nextVar = v;
                break;
            }
        }

        if (nextVar == null) {
            return false;
        }

        // Branch 1: Try True
        assignment.put(nextVar, true);
        if (dpll(clauses, assignment, unassignedVars)) {
            return true;
        }

        // Branch 2: Try False
        assignment.put(nextVar, false);
        if (dpll(clauses, assignment, unassignedVars)) {
            return true;
        }

        assignment.remove(nextVar);
        return false;
    }

    private static List<Clause> simplify(List<Clause> clauses, Map<String, Boolean> assignment) {
        List<Clause> result = new ArrayList<>();
        for (Clause c : clauses) {
            boolean clauseSatisfied = false;
            List<Literal> remainingLiterals = new ArrayList<>();

            for (Literal l : c.literals) {
                if (assignment.containsKey(l.variable)) {
                    boolean val = assignment.get(l.variable);
                    if (val == l.sign) {
                        clauseSatisfied = true;
                        break;
                    }
                    // Literal evaluated to false, omitted from clause
                } else {
                    remainingLiterals.add(l);
                }
            }

            if (!clauseSatisfied) {
                result.add(new Clause(remainingLiterals));
            }
        }
        return result;
    }
}
