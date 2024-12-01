
/*
 * @name(s) Sander van der Leek (1564226), Linda Geraets (1565834), Kenna Janssens (1577271)
 * @date    13-12-2024
 */

import java.io.*;
import java.util.*;

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

class LTS {
    List<State> states;
    Set<Transition> transitions;
    State first_state;

    int nr_of_states;
    int nr_of_transitions;

    public LTS(int first_state_id, int nr_of_transitions, int nr_of_states) {
        this.nr_of_states = nr_of_states;
        this.nr_of_transitions = nr_of_transitions;

        this.states = new ArrayList<>(nr_of_states);
        for (int i = 0; i < nr_of_states; i++) {
            this.states.add(new State(i));
        }

        this.transitions = new HashSet<>(nr_of_transitions);

        this.first_state = this.states.get(first_state_id);
    }

    public void addTransition(int start_state_id, String label, int end_state_id) {
        State startState = this.states.get(start_state_id);
        State endState = this.states.get(end_state_id);
        this.transitions.add(new Transition(startState, label, endState));
    }

    public String toString() {
        return "LTS(Start="+ this.first_state + ", #transitions=" + this.nr_of_transitions + ", #states=" + this.nr_of_states + ")";
    }
}

public class ModelChecker {
    LTS lts;

    /**
     * Read the input LTS in Aldebaran format from an .aut file.
     * @throws IOException
     */
    void readLTS(String filename) throws IOException {

        File file = new File(filename);
        Scanner scanner = new Scanner(file);

        String line = scanner.nextLine();
        String[] parts = line.split(" "); // Split on space
        if (parts.length != 2 || !parts[0].equals("des")) {
            scanner.close();
            throw new IOException("Invalid file format");
        }
        // strip off the parentheses and split on comma
        String[] header = parts[1].substring(1, parts[1].length() - 1).split(",");

        this.lts = new LTS(Integer.parseInt(header[0]), Integer.parseInt(header[1]), Integer.parseInt(header[2]));

        for (int i = 0; i < lts.nr_of_transitions; i++) {
            if (!scanner.hasNextLine()) {
                scanner.close();
                throw new IOException("The input file does not contain enough transitions");
            }
            line = scanner.nextLine();
            // strip off the parentheses and split on commas (that are not inside "quotes")
            // (hopefully) this regex matches commas that are not inside the label with "quotes"
            parts = line.substring(1, line.length() - 1).split(",(?=(([^\"]*\"){2})*[^\"]*$)");
            if (parts.length != 3) {
                scanner.close();
                throw new IOException("Invalid file format");
            }
            lts.addTransition(Integer.parseInt(parts[0]), parts[1], Integer.parseInt(parts[2]));
        }

        scanner.close();
    }

    /**
     * Reads a mu-calculus formula from an .mcf file.
     * @throws IOException
     */
    void readFormula(String filename) throws IOException { // TODO: implement
        File file = new File(filename);
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(line);
        }

        scanner.close();
    }

    public static void main(String[] args) throws Exception {
        ModelChecker modelChecker = new ModelChecker();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the LTS filename (.aut): ");
        String lts_filename = scanner.nextLine();
        lts_filename = "dining/dining_2.aut"; // TODO remove hardcode before submission

        modelChecker.readLTS(lts_filename);
        System.out.println(modelChecker.lts);

        System.out.println("Enter the mu-calculus formula filename (.mcf): ");
        String formula_filename = scanner.nextLine();
        formula_filename = "dining/invariantly_inevitably_eat.mcf"; // TODO remove hardcode before submission

        modelChecker.readFormula(formula_filename);

        scanner.close();
    }
}
