package trashsoftware.decimalExpr.expression;

import trashsoftware.decimalExpr.BuildException;
import trashsoftware.decimalExpr.builder.Node;
import trashsoftware.decimalExpr.builder.Values;
import trashsoftware.decimalExpr.numbers.Number;

public class Macro {

    public static final Macro UNDEFINED = new Macro(null, null);

    private final Node root;
    private final Values macroValues;

    public Macro(Node root, Values macroValues) {
        this.root = root;
        this.macroValues = macroValues;
    }

    public Number eval() {
        if (root == null) throw new BuildException("Macro is declared but not set.");
        return root.eval(macroValues);
    }

    public Values getMacroValues() {
        return macroValues;
    }

    @Override
    public String toString() {
        return "Macro{" + root + '}';
    }
}
