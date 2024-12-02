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
        String lts_filename = scanner.nextLine();
        lts_filename = "dining/dining_2.aut"; // TODO remove hardcode before submission


        modelChecker.lts = parsing.readLTS(lts_filename);
        System.out.println(modelChecker.lts);

        System.out.println("Enter the mu-calculus formula filename (.mcf): ");
        String formula_filename = scanner.nextLine();
        formula_filename = "dining/invariantly_inevitably_eat.mcf"; // TODO remove hardcode before submission
        // formula_filename = "testcases/combined/form5.mcf"; // TODO remove hardcode before submission

        modelChecker.formula = parsing.readFormula(formula_filename);
        System.out.println(modelChecker.formula);

        scanner.close();
    }
}
