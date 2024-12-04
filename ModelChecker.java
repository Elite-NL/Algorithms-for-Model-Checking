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
        System.out.print("Enter the LTS filename (.aut): ");
        String lts_filename;
        // lts_filename = scanner.nextLine();

        // TODO remove hardcode before submission
        // lts_filename = "dining/dining_2.aut";
        // lts_filename = "testcases/boolean/test.aut";
        lts_filename = "testcases/modal_operators/test.aut";
        // lts_filename = "testcases/combined/test.aut";

        modelChecker.lts = parsing.readLTS(lts_filename);
        System.out.println(modelChecker.lts);
        System.out.println("test123");
        System.out.println(modelChecker.lts.transitions);
        System.out.println(modelChecker.lts.getTransitionsWithAction("tau"));
        // System.out.println(modelChecker.lts.first_state.getOutgoingTransitions("tau"));

        System.out.print("Enter the mu-calculus formula filename (.mcf): ");
        String formula_filename;
        // formula_filename = scanner.nextLine();

        // TODO remove hardcode before submission
        // formula_filename = "dining/invariantly_inevitably_eat.mcf";
        // formula_filename = "testcases/boolean/form8.mcf";
        formula_filename = "testcases/modal_operators/form1.mcf";
        // formula_filename = "testcases/combined/form2.mcf";

        // modelChecker.formula = parsing.readFormula(formula_filename);
        modelChecker.formula = parsing.parseFormula("<tau>true"); // TODO remove hardcode before submission
        System.out.println(modelChecker.formula);

        // evaluate the formula on the LTS
        Set<State> result = modelChecker.formula.evaluate(modelChecker.lts);
        System.out.println(result);

        scanner.close();
    }
}
