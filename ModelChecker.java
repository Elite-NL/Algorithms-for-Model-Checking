/*
 * @name(s) Sander van der Leek (1564226), Linda Geraets (1565834), Kenna Janssens (1577271)
 * @date    13-12-2024
 */

import java.util.*;

public class ModelChecker {
    LTS lts;
    Formula formula;

    public static void main(String[] args) throws Exception {
        ModelChecker modelChecker = new ModelChecker();
        Parsing parsing = new Parsing(); // helper class to read LTS and formula from files

        String lts_filename = "dining/dining_11.aut";
        String formula_filename = "dining/invariantly_inevitably_eat.mcf";
        boolean EmersonLei = true;

        if (args.length < 2) { // java ModelChecker
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter the LTS filename (.aut): ");
            // lts_filename = scanner.nextLine();

            System.out.print("Enter the mu-calculus formula filename (.mcf): ");
            // formula_filename = scanner.nextLine();

            System.out.print("Evaluate using the Emerson-Lei algorithm? (True/False): ");
            // EmersonLei = !scanner.nextLine().toLowerCase().startsWith("f"); // if the user doesn't type "False", we default to the E-L algorithm

            scanner.close();
        } else if (args.length == 2) { // java ModelChecker <lts_filename> <formula_filename>
            lts_filename = args[0];
            formula_filename = args[1];
            EmersonLei = true; // if no algorithm is specified, we default to Emerson-Lei
        } else if (args.length >= 3) { // java ModelChecker <lts_filename> <formula_filename> <EmersonLei>
            lts_filename = args[0];
            formula_filename = args[1];
            EmersonLei = !args[2].toLowerCase().startsWith("f"); // if the user doesn't type "False", we default to the E-L algorithm
        }

        System.out.println("Parsing the LTS...");
        modelChecker.lts = parsing.readLTS(lts_filename);
        System.out.println(modelChecker.lts);

        System.out.println("Parsing the formula...");
        modelChecker.formula = parsing.readFormula(formula_filename);
        // modelChecker.formula = parsing.parse("<tau>true");
        System.out.println(modelChecker.formula);

        System.out.println("Evaluating using the " + ((EmersonLei)?"Emerson-Lei" : "naive") + " algorithm...");

        // evaluate the formula on the LTS (and time it)
        long startTime = System.nanoTime();
        Set<State> satisfying_states = modelChecker.formula.evaluate(modelChecker.lts, EmersonLei);
        System.out.println("Time taken: " + (System.nanoTime() - startTime) / 1e9 + " seconds");
        System.out.println("Total number of fixpoint iterations: " + modelChecker.formula.iterationCount());

        // we sort the states to make the output more readable
        List<State> satisfying_states_list = new ArrayList<>(satisfying_states);
        satisfying_states_list.sort((state1, state2) -> Integer.compare(state1.id, state2.id));

        // print conclusion in the console
        System.out.println("The formula: is satisfied by the following states of the LTS:");
        System.out.println((satisfying_states_list.size() <= 128) ? satisfying_states_list : "Too many states to display (128 < "+ satisfying_states_list.size()+")");
        // System.out.println(satisfying_states_list.contains(modelChecker.lts.first_state));
        System.out.println("The first state of the LTS: " + modelChecker.lts.first_state + " is "
            + (satisfying_states_list.contains(modelChecker.lts.first_state)?"" : "not ")
            + "in the satisfying states");

    }
}
