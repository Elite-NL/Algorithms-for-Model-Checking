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

    public String toString() {
        return "s_" + this.id;
    }
}

class Transition {
    State start_state;
    String label;
    State end_state;

    public Transition(State start_state, String label, State end_state) {
        this.start_state = start_state;
        this.label = label;
        this.end_state = end_state;
    }

    public String toString() {
        return this.start_state + " -" + this.label + "-> " + this.end_state;
    }
}

public class LTS {
    Map<Integer, State> states; // we store the states as a Map, so it's easy to find a State based on its id
    Set<Transition> transitions;
    State first_state;

    int nr_of_states;
    int nr_of_transitions;

    public LTS(int first_state_id, int nr_of_transitions, int nr_of_states) throws IOException {
        this.nr_of_states = nr_of_states;
        this.nr_of_transitions = nr_of_transitions;

        if (first_state_id >= nr_of_states){
            throw new IOException("Invalid first state id");
        }

        this.states = new HashMap<>(nr_of_states);
        for (int i = 0; i < nr_of_states; i++) {
            State state = new State(i);
            if (i == first_state_id) {
                this.first_state = state;
            }
            this.states.put(i, state);
        }

        this.transitions = new HashSet<>(nr_of_transitions);
    }

    public State getState(int id) {
        return this.states.get(id);
    }

    public void addTransition(int start_state_id, String label, int end_state_id) {
        State startState = getState(start_state_id);
        State endState = getState(end_state_id);
        this.transitions.add(new Transition(startState, label, endState));
    }

    public String toString() {
        return "LTS(Start=" + this.first_state + ", #transitions=" + this.nr_of_transitions + ", #states="
                + this.nr_of_states + ")";
    }
}