/*
 * @name(s) Sander van der Leek (1564226), Linda Geraets (1565834), Kenna Janssens (1577271)
 * @date    13-12-2024
 */

import java.io.*;
import java.util.*;

public class Parsing {
    /**
     * Read the input LTS in Aldebaran format from an .aut file.
     * @throws IOException
     */
    LTS readLTS(String filename) throws IOException {
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

        LTS lts = new LTS(Integer.parseInt(header[0]), Integer.parseInt(header[1]), Integer.parseInt(header[2]));

        for (int j = 0; j < lts.nr_of_transitions; j++) {
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
            int start_state_id = Integer.parseInt(parts[0]);
            // strip off the quotes from the action name
            String action = parts[1].substring(1, parts[1].length() - 1);
            int end_state_id = Integer.parseInt(parts[2]);
            lts.addTransition(start_state_id, action, end_state_id);
        }

        scanner.close();
        return lts;
    }

    int i = 0; // index for parsing the formula, this needs to be global so it can be incremented in all subfunctions
    static String recursion_variable_first_set = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // first set of characters in a recursion variable
    static String formula_first_set = recursion_variable_first_set + "tf(mn<["; // first set of characters in a formula
    static String operator_first_set = "|&"; // first set of characters in an operator
    static String action_name_first_set = "abcdefghijklmnopqrstuvwxyz"; // first set of characters in an action name
    static String action_name_set = action_name_first_set + "0123456789_"; // followup set of characters in an action name

    /**
     * Reads a mu/nu-calculus formula from an .mcf file.
     * @throws IOException
     */
    Formula readFormula(String filename) throws IOException {
        File file = new File(filename);

        StringBuilder sb = new StringBuilder(); // we use a StringBuilder to concatenate the lines

        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            // Remove comments, everything after a '%' is a comment
            // and strip whitespace
            line = line.split("%")[0].trim();

            sb.append(line); // append each line to the StringBuilder
        }
        scanner.close();

        String formula_string = sb.toString(); // convert the StringBuilder to a String

        return parse(formula_string);
    }

    Formula parse(String line) throws IOException {
        i = 0; // index for parsing the formula, this needs to be global so it can be incremented in all subfunctions
        skipWhiteSpace(line);
        // first 'real' character should be 't', 'f', 'A...Z', '(', 'm', 'n', '<' or '['
        if (!formula_first_set.contains(line.substring(i, i+1))) {
            throw new IOException("Parse error: invalid first character in formula");
        }
        return parseFormula(line);
    }

    Formula parseFormula(String line) throws IOException {
        char c = line.charAt(i);
        switch (c) {
            case 't':
                // True Literal
                return parseTrueLiteral(line);
            case 'f':
                // False Literal
                return parseFalseLiteral(line);
            case '(':
                // Logic Formula
                return parseLogicFormula(line);
            case 'm':
                // Mu Formula
                return parseMuNuFormula(line, "mu");
            case 'n':
                // Nu Formula
                return parseMuNuFormula(line, "nu");
            case '<':
                // Diamond Formula
                return parseDiamondBoxFormula(line, "<>");
            case '[':
                // Box Formula
                return parseDiamondBoxFormula(line, "[]");
            default:
                // Recursion Variable
                return parseRecursionVariable(line);
        }
    }

    Formula parseTrueLiteral(String line) throws IOException {
        expect(line, "true");
        skipWhiteSpace(line);
        return new TrueFormula();
    }

    Formula parseFalseLiteral(String line) throws IOException {
        expect(line, "false");
        skipWhiteSpace(line);
        return new FalseFormula();
    }

    VariableFormula parseRecursionVariable(String line) {
        String variable = line.substring(i, i+1);
        i++;
        skipWhiteSpace(line);
        return new VariableFormula(variable);
    }

    Formula parseLogicFormula(String line) throws IOException {
        expect(line, "(");
        skipWhiteSpace(line);
        // check if the next character is in formula_first_set
        if (!formula_first_set.contains(line.substring(i, i+1))) {
            throw new IOException("Parse error: " + line.substring(i, i+1) +" is not a valid character in a formula");
        }
        Formula f = parseFormula(line);

        // check if the next character is in operator_first_set
        if (!operator_first_set.contains(line.substring(i, i+1))) {
            throw new IOException("Parse error: " + line.substring(i, i+1) +" is not a valid character in a binary operator");
        }
        String operator = parseOperator(line);

        // check if the next character is in formula_first_set
        if (!formula_first_set.contains(line.substring(i, i + 1))) {
            throw new IOException("Parse error: " + line.substring(i, i+1) +" is not a valid character in a formula");
        }
        Formula g = parseFormula(line);

        expect(line, ")");
        skipWhiteSpace(line);

        if (operator.equals("&&")) {
            return new AndFormula(f, g);
        } else { // operator.equals("||")
            return new OrFormula(f, g);
        }
    }

    String parseOperator(String line) throws IOException {
        if (line.charAt(i) == '&') {
            return parseLogicAndOperator(line);
        } else{ // line.charAt(i) == '|'
            return parseLogicOrOperator(line);
        }
    }

    String parseLogicAndOperator(String line) throws IOException {
        expect(line, "&&");
        skipWhiteSpace(line);
        return "&&";
    }

    String parseLogicOrOperator(String line) throws IOException {
        expect(line, "||");
        skipWhiteSpace(line);
        return "||";
    }

    Formula parseMuNuFormula(String line, String fixedPoint) throws IOException {
        expect(line, fixedPoint);
        requireWhiteSpace(line);

        if (!recursion_variable_first_set.contains(line.substring(i, i+1))) {
            throw new IOException("Parse error: expected recursion variable");
        }
        VariableFormula r = parseRecursionVariable(line);

        expect(line, ".");
        skipWhiteSpace(line);

        // check if the next character is in formula_first_set
        if (!formula_first_set.contains(line.substring(i, i+1))) {
            throw new IOException("Parse error: invalid character in formula");
        }
        Formula f = parseFormula(line);

        if (fixedPoint.equals("mu")) {
            return new MuFormula(r.variable, f);
        } else { // fixedPoint.equals("nu")
            return new NuFormula(r.variable, f);
        }
    }

    Formula parseDiamondBoxFormula(String line, String modalOperator) throws IOException {
        expect(line, modalOperator.substring(0, 1));
        skipWhiteSpace(line);

        if (!action_name_first_set.contains(line.substring(i, i+1))) {
            throw new IOException("Parse error: expected action name");
        }
        String a = parseActionName(line);

        expect(line, modalOperator.substring(1, 2));
        skipWhiteSpace(line);

        // check if the next character is in formula_first_set
        if (!formula_first_set.contains(line.substring(i, i + 1))) {
            throw new IOException("Parse error: invalid character in formula");
        }
        Formula f = parseFormula(line);

        if (modalOperator.equals("<>")) {
            return new DiamondFormula(a, f);
        } else { // modalOperator.equals("[]")
            return new BoxFormula(a, f);
        }
    }

    String parseActionName(String line) {
        StringBuilder action = new StringBuilder();
        while (action_name_set.contains(line.substring(i, i+1))) {
            action.append(line.charAt(i));
            i++;
        }
        return action.toString();
    }

    void expect(String line, String expected) throws IOException {
        if (!line.substring(i, i + expected.length()).equals(expected)) {
            throw new IOException("Parse error: expected '" + expected + "'");
        } else {
            i += expected.length();
        }
    }

    void skipWhiteSpace(String line) {
        while (i < line.length() && line.substring(i, i+1).equals(" ")) {
            i++;
        }
    }

    void requireWhiteSpace(String line) throws IOException {
        if (!Character.isWhitespace(line.charAt(i))) {
            throw new IOException("Parse error: expected whitespace");
        }
        skipWhiteSpace(line);
    }
}
