/*
 * @name(s) Sander van der Leek (1564226), Linda Geraets (1565834), Kenna Janssens (1577271)
 * @date    13-12-2024
 */

abstract class Formula {

}

class TrueFormula extends Formula {
    public String toString() {
        return "true";
    }
}

class FalseFormula extends Formula {
    public String toString() {
        return "false";
    }
}

class VariableFormula extends Formula {
    String variable;

    public VariableFormula(String variable) {
        this.variable = variable;
    }

    public String toString() {
        return variable;
    }
}

class AndFormula extends Formula {
    Formula left;
    Formula right;

    public AndFormula(Formula left, Formula right) {
        this.left = left;
        this.right = right;
    }

    public String toString() {
        return "(" + left + " && " + right + ")";
    }
}

class OrFormula extends Formula {
    Formula left;
    Formula right;

    public OrFormula(Formula left, Formula right) {
        this.left = left;
        this.right = right;
    }

    public String toString() {
        return "(" + left + " || " + right + ")";
    }
}

class DiamondFormula extends Formula {
    String action;
    Formula subFormula;

    public DiamondFormula(String action, Formula subFormula) {
        this.action = action;
        this.subFormula = subFormula;
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

    public String toString() {
        return "[" + action + "]" + subFormula;
    }
}

class MuFormula extends Formula {
    Formula recursionVariable;
    Formula subFormula;

    public MuFormula(Formula recursionVariable, Formula subFormula) {
        this.recursionVariable = recursionVariable;
        this.subFormula = subFormula;
    }

    public String toString() {
        return "mu " + recursionVariable + "." + subFormula;
    }
}

class NuFormula extends Formula {
    Formula recursionVariable;
    Formula subFormula;

    public NuFormula(Formula recursionVariable, Formula subFormula) {
        this.recursionVariable = recursionVariable;
        this.subFormula = subFormula;
    }

    public String toString() {
        return "nu " + recursionVariable + "." + subFormula;
    }
}