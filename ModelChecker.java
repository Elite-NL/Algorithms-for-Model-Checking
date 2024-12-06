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

        Scanner scanner = new Scanner(System.in);

        // choose LTS file
        System.out.print("Enter the LTS filename (.aut): ");
        String lts_filename;
        // lts_filename = scanner.nextLine();

        // TODO remove hardcode before submission
        lts_filename = "dining/dining_11.aut";
        // lts_filename = "testcases/boolean/test.aut";
        // lts_filename = "testcases/modal_operators/test.aut";
        // lts_filename = "testcases/combined/test.aut";

        modelChecker.lts = parsing.readLTS(lts_filename);
        System.out.println(modelChecker.lts);

        // choose mu-calculus formula file (or read it directly from a string)
        System.out.print("Enter the mu-calculus formula filename (.mcf): ");
        String formula_filename;
        // formula_filename = scanner.nextLine();

        // TODO remove hardcode before submission
        formula_filename = "dining/invariantly_inevitably_eat.mcf";
        // formula_filename = "testcases/boolean/form8.mcf";
        // formula_filename = "testcases/modal_operators/form1.mcf";
        // formula_filename = "testcases/combined/form2.mcf";

        modelChecker.formula = parsing.readFormula(formula_filename);
        // modelChecker.formula = parsing.parse("<tau>true");
        System.out.println(modelChecker.formula);

        // choose evaluation algorithm
        System.out.print("Evaluate using the Emerson-Lei algorithm? (Yes/No): ");
        boolean EmersonLei;
        // EmersonLei = scanner.nextLine().toLowerCase().startsWith("y");
        EmersonLei = true; // TODO remove hardcode before submission
        System.out.println("Evaluating using the " + ((EmersonLei)?"Emerson-Lei" : "naive") + " algorithm...");

        // evaluate the formula on the LTS (and time it)
        long startTime = System.nanoTime();
        Set<State> satisfying_states = modelChecker.formula.evaluate(modelChecker.lts, EmersonLei);
        System.out.println("Time taken: " + (System.nanoTime() - startTime) / 1e9 + " seconds");

        // we sort the states to make the output more readable
        List<State> satisfying_states_list = new ArrayList<>(satisfying_states);
        satisfying_states_list.sort((state1, state2) -> Integer.compare(state1.id, state2.id));

        // print conclusion in the console
        System.out.println("The formula: " + modelChecker.formula + " is satisfied by the following states of the LTS:");
        System.out.println((satisfying_states_list.size() <= 128) ? satisfying_states_list : "Too many states to display (> 128)");
        // System.out.println(satisfying_states_list.contains(modelChecker.lts.first_state));
        System.out.println("The first state of the LTS: " + modelChecker.lts.first_state + " is "
            + (satisfying_states_list.contains(modelChecker.lts.first_state)?"" : "not ")
            + "in the satisfying states");

        scanner.close();
    }
}
