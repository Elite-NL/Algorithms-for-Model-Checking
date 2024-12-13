package mc;
/*
 * @name(s) Sander van der Leek (1564226), Linda Geraets (1565834), Kenna Janssens (1577271)
 * @date    13-12-2024
 */

import java.util.*;

abstract class Formula {
    // returns true if the formula is satisfied by the first state of the LTS, and false otherwise
    public boolean satisfiesLTS(LTS lts, boolean EmersonLei) {
        Set<State> satisfying_states = evaluate(lts, EmersonLei);
        return satisfying_states.contains(lts.first_state);
    }

    // evaluate the formula on the LTS, this is the entrypoint of the recursive evaluation
    public Set<State> evaluate(LTS lts, boolean EmersonLei) {
        Map<String, Set<State>> variable_values = new HashMap<>(); // initially there is no defined value for any variable
        resetMuNu(true); // this is to ensure the top-level nu formula is reset before evaluation
        resetMuNu(false); // this is to ensure the top-level mu formula is reset before evaluation
        return evaluate(lts, variable_values, EmersonLei);
    }

    // every subclass of Formula should implement this method separately
    // if EmersonLei == true, we do not always need to reset the variables of the mu/nu formulas
    public abstract Set<State> evaluate(LTS lts, Map<String, Set<State>> variable_values, boolean EmersonLei);

    // this method is used to reset the stored set of states of all open mu/nu subformulas
    // if resetNu == false, we should reset all variables defined in all mu subformulas
    // if resetNu == true, we should reset all variables defined in all nu subformulas
    public abstract void resetMuNu(boolean resetNu);

    // this function should return the set of unbound variables in the formula
    // this is used to determine if the formula is closed or not
    public abstract Set<String> unboundVariables();

    // this function should return the total number of iterations in all fixed-point formulas in the formula
    public abstract int iterationCount();

    public abstract String toString();
}

class TrueFormula extends Formula {
    public Set<State> evaluate(LTS lts, Map<String, Set<State>> variable_values, boolean EmersonLei) {
        return lts.getStates();
    }

    // there are no unbound variables in this formula
    public Set<String> unboundVariables() {
        return new HashSet<>();
    }

    public void resetMuNu(boolean resetNu) {
        // there is nothing to reset in a true formula
    }

    public int iterationCount() {
        return 0;
    }

    public String toString() {
        return "true";
    }
}

class FalseFormula extends Formula {
    public Set<State> evaluate(LTS lts, Map<String, Set<State>> variable_values, boolean EmersonLei) {
        return new HashSet<>();
    }

    // there are no unbound variables in this formula
    public Set<String> unboundVariables() {
        return new HashSet<>();
    }

    public void resetMuNu(boolean resetNu) {
        // there is nothing to reset in a false formula
    }

    public int iterationCount() {
        return 0;
    }

    public String toString() {
        return "false";
    }
}

class VariableFormula extends Formula {
    String variable;

    public VariableFormula(String variable) {
        this.variable = variable;
    }

    public Set<State> evaluate(LTS lts, Map<String, Set<State>> variable_values, boolean EmersonLei) {
        return new HashSet<>(variable_values.get(this.variable));
    }

    // there is one unbound variable in this formula
    public Set<String> unboundVariables() {
        return new HashSet<>(Collections.singletonList(variable));
    }

    public void resetMuNu(boolean resetNu) {
        // there is nothing to reset in a variable formula, this function only resets mu/nu formulas
    }

    public int iterationCount() {
        return 0;
    }

    public String toString() {
        return variable;
    }
}

class AndFormula extends Formula {
    Formula leftSubFormula;
    Formula rightSubFormula;

    public AndFormula(Formula left, Formula right) {
        this.leftSubFormula = left;
        this.rightSubFormula = right;
    }

    public Set<State> evaluate(LTS lts, Map<String, Set<State>> variable_values, boolean EmersonLei) {
        Set<State> leftstates = this.leftSubFormula.evaluate(lts, variable_values, EmersonLei);
        Set<State> rightstates = this.rightSubFormula.evaluate(lts, variable_values, EmersonLei);
        leftstates.retainAll(rightstates); // compute the intersection of the two sets
        return leftstates;
    }

    // propagate the resetNu call to the subformulas
    public void resetMuNu(boolean resetNu) {
        leftSubFormula.resetMuNu(resetNu);
        rightSubFormula.resetMuNu(resetNu);
    }

    // the unbound variables of an and formula are the union of the unbound variables of the two subformulas
    public Set<String> unboundVariables() {
        Set<String> unboundVariables = leftSubFormula.unboundVariables();
        unboundVariables.addAll(rightSubFormula.unboundVariables());
        return unboundVariables;
    }

    public int iterationCount() {
        return leftSubFormula.iterationCount() + rightSubFormula.iterationCount();
    }

    public String toString() {
        return "(" + leftSubFormula + " && " + rightSubFormula + ")";
    }
}

class OrFormula extends Formula {
    Formula leftSubFormula;
    Formula rightSubFormula;

    public OrFormula(Formula left, Formula right) {
        this.leftSubFormula = left;
        this.rightSubFormula = right;
    }

    public Set<State> evaluate(LTS lts, Map<String, Set<State>> variable_values, boolean EmersonLei) {
        Set<State> leftstates = this.leftSubFormula.evaluate(lts, variable_values, EmersonLei);
        Set<State> rightstates = this.rightSubFormula.evaluate(lts, variable_values, EmersonLei);
        leftstates.addAll(rightstates); // compute the union of the two sets
        return leftstates;
    }

    // propagate the resetNu call to the subformulas
    public void resetMuNu(boolean resetNu) {
        leftSubFormula.resetMuNu(resetNu);
        rightSubFormula.resetMuNu(resetNu);
    }

    // the unbound variables of an or formula are the union of the unbound variables of the two subformulas
    public Set<String> unboundVariables() {
        Set<String> unboundVariables = leftSubFormula.unboundVariables();
        unboundVariables.addAll(rightSubFormula.unboundVariables());
        return unboundVariables;
    }

    public int iterationCount() {
        return leftSubFormula.iterationCount() + rightSubFormula.iterationCount();
    }

    public String toString() {
        return "(" + leftSubFormula + " || " + rightSubFormula + ")";
    }
}

class DiamondFormula extends Formula {
    String action;
    Formula subFormula;

    public DiamondFormula(String action, Formula subFormula) {
        this.action = action;
        this.subFormula = subFormula;
    }

    public Set<State> evaluate(LTS lts, Map<String, Set<State>> variable_values, boolean EmersonLei) {
        Set<State> subformulastates = this.subFormula.evaluate(lts, variable_values, EmersonLei);
        Set<State> satisfyingStates = new HashSet<>(); // by default, the result is empty.
        for (Transition transition : lts.getTransitions(this.action)) { // get all outgoing transitions with the correct action
            if (subformulastates.contains(transition.end_state)) { // if the end state of the transition is in the set of states that satisfy the subformula
                satisfyingStates.add(transition.start_state); // add the start state of the transition to the result
            }
        }
        return satisfyingStates;
    }

    // propagate the resetNu call to the subformula
    public void resetMuNu(boolean resetNu) {
        subFormula.resetMuNu(resetNu);
    }

    public Set<String> unboundVariables() {
        return subFormula.unboundVariables();
    }

    public int iterationCount() {
        return subFormula.iterationCount();
    }

    public String toString() {
        return "<" + action + ">" + subFormula;
    }
}

class BoxFormula extends Formula {
    String action;
    Formula subFormula;

    public BoxFormula(String action, Formula subFormula) {
        this.action = action;
        this.subFormula = subFormula;
    }

    public Set<State> evaluate(LTS lts, Map<String, Set<State>> variable_values, boolean EmersonLei) {
        Set<State> subformulastates = this.subFormula.evaluate(lts, variable_values, EmersonLei);
        Set<State> satisfyingStates = lts.getStates(); // by default all states satisfy the formula, we will remove the states that do not satisfy the formula
        for (Transition transition : lts.getTransitions(this.action)) { // get all outgoing transitions with the correct action
            if (!subformulastates.contains(transition.end_state)) { // if the end state of the transition is not in the set of states that satisfy the subformula
                satisfyingStates.remove(transition.start_state); // remove the start state of the transition from the result
            }
        }
        return satisfyingStates;
    }

    // propagate the resetNu call to the subformula
    public void resetMuNu(boolean resetNu) {
        subFormula.resetMuNu(resetNu);
    }

    public Set<String> unboundVariables() {
        return subFormula.unboundVariables();
    }

    public int iterationCount() {
        return subFormula.iterationCount();
    }

    public String toString() {
        return "[" + action + "]" + subFormula;
    }
}

class MuFormula extends Formula {
    String recursionVariable;
    Formula subFormula;

    boolean isOpen; // stores if the formula is open or closed
    Set<State> satisfyingStates = null;

    int iterationCount = 0; // used to count the number of iterations in the fixed-point algorithm

    public MuFormula(String recursionVariable, Formula subFormula) {
        this.recursionVariable = recursionVariable;
        this.subFormula = subFormula;
        this.isOpen = !this.unboundVariables().isEmpty(); // if there exists some unbound variables in the formula, the formula is open
    }

    public Set<State> evaluate(LTS lts, Map<String, Set<State>> variable_values, boolean EmersonLei) {
        // if we are in the Emerson-Lei algorithm, we do not always need to reset the variables of the mu formulas
        // if there is a set of satisfying states defined (and EmersonLei == true), we reuse it by default
        if (this.satisfyingStates == null || !EmersonLei) {
            this.satisfyingStates = new HashSet<>();
        }

        do {
            iterationCount++; // increment the iteration count for each iteration of the fixed-point algorithm
            // update the value of the recursion variable (overwriting the previous value)
            variable_values.put(this.recursionVariable, satisfyingStates);

            if (EmersonLei) {
                this.subFormula.resetMuNu(true); // reset all open nu subformulas (if they exist) inside this mu formula
            }

            // evaluate the subformula with the new value of the recursion variable
            this.satisfyingStates = this.subFormula.evaluate(lts, variable_values, EmersonLei);
            // continue until the result is equal to the value of the recursion variable
        } while (!satisfyingStates.equals(variable_values.get(this.recursionVariable)));

        return this.satisfyingStates;
    }

    // resetNu == false means that we should reset this mu formula and all following mu formulas
    // resetNu == true means that we should do nothing (this is not a NuFormula)
    public void resetMuNu(boolean resetNu) {
        if (!resetNu) { // no need to continue propagating the function call if resetNu == true
            if (this.isOpen) { // if the mu formula is open, we should reset this variable
                this.satisfyingStates = null;
            }
            subFormula.resetMuNu(false);
        }
    }

    // the unbound variables of a mu formula are the unbound variables of the subformula, except for the recursion variable (which is now bound)
    public Set<String> unboundVariables() {
        Set<String> unboundVariables = subFormula.unboundVariables();
        unboundVariables.remove(this.recursionVariable);
        return unboundVariables;
    }

    public int iterationCount() {
        return iterationCount + subFormula.iterationCount();
    }

    public String toString() {
        return "mu " + recursionVariable + "." + subFormula;
    }
}

class NuFormula extends Formula {
    String recursionVariable;
    Formula subFormula;

    boolean isOpen; // stores if the formula is open or closed
    Set<State> satisfyingStates = null;

    int iterationCount = 0; // used to count the number of iterations in the fixed-point algorithm

    public NuFormula(String recursionVariable, Formula subFormula) {
        this.recursionVariable = recursionVariable;
        this.subFormula = subFormula;
        this.isOpen = !this.unboundVariables().isEmpty(); // if there exists some unbound variables in the formula, the formula is open
    }

    public Set<State> evaluate(LTS lts, Map<String, Set<State>> variable_values, boolean EmersonLei) {
        // if we are in the Emerson-Lei algorithm, we do not always need to reset the variables of the mu formulas
        // if there is a set of satisfying states defined (and EmersonLei == true), we reuse it by default
        if (this.satisfyingStates == null || !EmersonLei) {
            this.satisfyingStates = lts.getStates();
        }

        do {
            iterationCount++; // increment the iteration count for each iteration of the fixed-point algorithm
            // update the value of the recursion variable (overwriting the previous value)
            variable_values.put(this.recursionVariable, satisfyingStates);

            if (EmersonLei) {
                this.subFormula.resetMuNu(false); // reset all open mu subformulas (if they exist) inside this nu formula
            }

            // evaluate the subformula with the new value of the recursion variable
            satisfyingStates = this.subFormula.evaluate(lts, variable_values, EmersonLei);
            // continue until the result is equal to the value of the recursion variable
        } while (!satisfyingStates.equals(variable_values.get(this.recursionVariable)));

        return satisfyingStates;
    }

    // resetNu == false means that we should do nothing (this is not a MuFormula)
    // resetNu == true means that we should reset this nu formula and all following nu formulas
    public void resetMuNu(boolean resetNu) {
        if (resetNu) { // no need to continue propagating the function call if resetNu == false
            if (this.isOpen) { // if the nu formula is open, we should reset this variable
                this.satisfyingStates = null;
            }
            subFormula.resetMuNu(true);
        }
    }

    // the unbound variables of a nu formula are the unbound variables of the subformula, except for the recursion variable (which is now bound)
    public Set<String> unboundVariables() {
        Set<String> unboundVariables = subFormula.unboundVariables();
        unboundVariables.remove(this.recursionVariable);
        return unboundVariables;
    }

    public int iterationCount() {
        return iterationCount + subFormula.iterationCount();
    }

    public String toString() {
        return "nu " + recursionVariable + "." + subFormula;
    }
}