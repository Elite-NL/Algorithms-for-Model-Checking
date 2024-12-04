/*
 * @name(s) Sander van der Leek (1564226), Linda Geraets (1565834), Kenna Janssens (1577271)
 * @date    13-12-2024
 */

import java.util.*;

abstract class Formula {
    // evaluate the formula on the LTS, this is the entrypoint of the recursive evaluation
    public Set<State> evaluate(LTS lts) {
        Map<String, Set<State>> variable_values = new HashMap<>(); // initially there is no defined value for any variable
        return evaluate(lts, variable_values);
    }

    // every subclass of Formula should implement this method separately
    public abstract Set<State> evaluate(LTS lts, Map<String, Set<State>> variable_values);
}

class TrueFormula extends Formula {
    public Set<State> evaluate(LTS lts, Map<String, Set<State>> variable_values) {
        return lts.getStates();
    }

    public String toString() {
        return "true";
    }
}

class FalseFormula extends Formula {
    public Set<State> evaluate(LTS lts, Map<String, Set<State>> variable_values) {
        return new HashSet<>();
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

    public Set<State> evaluate(LTS lts, Map<String, Set<State>> variable_values) {
        Set<State> states = variable_values.get(this.variable);
        if (states == null) {
            throw new UnsupportedOperationException("Variable " + this.variable + " not found in variable_values");
        }
        return states;
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

    public Set<State> evaluate(LTS lts, Map<String, Set<State>> variable_values) {
        Set<State> leftstates = this.leftSubFormula.evaluate(lts, variable_values);
        Set<State> rightstates = this.rightSubFormula.evaluate(lts, variable_values);
        leftstates.retainAll(rightstates); // compute the intersection of the two sets
        return leftstates;
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

    public Set<State> evaluate(LTS lts, Map<String, Set<State>> variable_values) {
        Set<State> leftstates = this.leftSubFormula.evaluate(lts, variable_values);
        Set<State> rightstates = this.rightSubFormula.evaluate(lts, variable_values);
        leftstates.addAll(rightstates); // compute the union of the two sets
        return leftstates;
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

    public Set<State> evaluate(LTS lts, Map<String, Set<State>> variable_values) {
        Set<State> subformulastates = this.subFormula.evaluate(lts, variable_values);
        Set<State> result = new HashSet<>();
        for (State state : lts.getStates()) {
            for (Transition transition : state.getOutgoingTransitions(this.action)) { // get all outgoing transitions with the correct action
                if (subformulastates.contains(transition.end_state)) { // if the end state of the transition is in the set of states that satisfy the subformula
                    result.add(state); // add the start state of the transition to the result
                    break; // we only need to find one transition that satisfies the subformula, so move to the next candidate state in the LTS
                }
            }
        }
        return result;
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

    public Set<State> evaluate(LTS lts, Map<String, Set<State>> variable_values) {
        Set<State> subformulastates = this.subFormula.evaluate(lts, variable_values);
        Set<State> result = new HashSet<>();
        for (State state : lts.getStates()) {
            boolean all_transitions_end_in_substates = true;
            for (Transition transition : state.getOutgoingTransitions(this.action)) { // get all outgoing transitions with the correct action
                if (!subformulastates.contains(transition.end_state)) { // if the end state of the transition is not in the set of states that satisfy the subformula
                    all_transitions_end_in_substates = false;
                    break; // we only need to find one transition that does not satisfy the subformula
                }
            }
            if (all_transitions_end_in_substates) {
                result.add(state); // add the start state to the result
            }
        }
        return result;
    }

    public String toString() {
        return "[" + action + "]" + subFormula;
    }
}

class MuFormula extends Formula {
    VariableFormula recursionVariable;
    Formula subFormula;

    public MuFormula(VariableFormula recursionVariable, Formula subFormula) {
        this.recursionVariable = recursionVariable;
        this.subFormula = subFormula;
    }

    public Set<State> evaluate(LTS lts, Map<String, Set<State>> variable_values) {
        // least fixed point, so we start by setting the value of the recursion variable to the empty set
        Set<State> result = new HashSet<>();

        do {
            // update the value of the recursion variable (overwriting the previous value)
            variable_values.put(this.recursionVariable.variable, result);
            // evaluate the subformula with the new value of the recursion variable
            result = this.subFormula.evaluate(lts, variable_values);
            // continue until the result is equal to the value of the recursion variable
        } while (!result.equals(variable_values.get(this.recursionVariable.variable)));

        return result;
    }

    public String toString() {
        return "mu " + recursionVariable + "." + subFormula;
    }
}

class NuFormula extends Formula {
    VariableFormula recursionVariable;
    Formula subFormula;

    public NuFormula(VariableFormula recursionVariable, Formula subFormula) {
        this.recursionVariable = recursionVariable;
        this.subFormula = subFormula;
    }

    public Set<State> evaluate(LTS lts, Map<String, Set<State>> variable_values) {
        // greatest fixed point, so we start by setting the value of the recursion variable to the set of all states
        Set<State> result = lts.getStates();

        do {
            // update the value of the recursion variable (overwriting the previous value)
            variable_values.put(this.recursionVariable.variable, result);
            // evaluate the subformula with the new value of the recursion variable
            result = this.subFormula.evaluate(lts, variable_values);
            // continue until the result is equal to the value of the recursion variable
        } while (!result.equals(variable_values.get(this.recursionVariable.variable)));

        return result;
    }

    public String toString() {
        return "nu " + recursionVariable + "." + subFormula;
    }
}