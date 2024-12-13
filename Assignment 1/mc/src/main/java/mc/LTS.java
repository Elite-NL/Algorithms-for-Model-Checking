package mc;
/*
 * @name(s) Sander van der Leek (1564226), Linda Geraets (1565834), Kenna Janssens (1577271)
 * @date    13-12-2024
 */

import java.util.*;
import java.io.*;

class State {
    int id;

    public State(int id) {
        this.id = id;
    }

    // we need to override equals() to be able to compare two State objects (used in the evaluate() function)
    @Override
    public boolean equals(Object object) {
        if (object instanceof State) {
            State otherstate = (State) object;
            return this.id == otherstate.id;
        } else {
            return false;
        }
    }

    // we need to override hashCode() if we override equals()
    @Override
    public int hashCode() {
        return this.id;
    }

    public String toString() {
        return "" + this.id;
    }
}

class Transition {
    State start_state;
    String action;
    State end_state;

    public Transition(State start_state, String action, State end_state) {
        this.start_state = start_state;
        this.action = action;
        this.end_state = end_state;
    }

    public String toString() {
        return this.start_state + " -" + this.action + "-> " + this.end_state;
    }
}

public class LTS {
    Map<Integer, State> states = new HashMap<>(); // we store the states as a Map, so it's easy to find a State based on its id
    Map<String, Set<Transition>> transitions = new HashMap<>(); // we store the transitions as a Map, so it's easy to find all transitions with a certain action
    State first_state;

    int nr_of_states;
    int nr_of_transitions;

    public LTS(int first_state_id, int nr_of_transitions, int nr_of_states) throws IOException {
        this.nr_of_states = nr_of_states;
        this.nr_of_transitions = nr_of_transitions;

        if (!(0 <= first_state_id && first_state_id < nr_of_states)) {
            throw new IOException("Invalid first state id");
        }

        for (int i = 0; i < nr_of_states; i++) {
            State state = new State(i);
            if (i == first_state_id) {
                this.first_state = state;
            }
            this.states.put(i, state);
        }
    }

    public Set<State> getStates() {
        return new HashSet<>(this.states.values());
    }

    public State getState(int id) {
        return this.states.get(id);
    }

    public Set<Transition> getTransitions(String action) {
        Set<Transition> transitionwithlabel = transitions.get(action);
        if (transitionwithlabel != null) {
            return transitionwithlabel;
        } else {
            return new HashSet<>();
        }
    }

    public void addTransition(int start_state_id, String action, int end_state_id) {
        State startState = getState(start_state_id);
        State endState = getState(end_state_id);
        Transition transition = new Transition(startState, action, endState);

        Set<Transition> transitionsWithAction = this.getTransitions(action);
        transitionsWithAction.add(transition);
        this.transitions.put(action, transitionsWithAction);
    }

    public String toString() {
        return "LTS(Start=" + this.first_state + ", #transitions=" + this.nr_of_transitions + ", #states="
                + this.nr_of_states + ")";
    }
}