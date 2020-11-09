package trashsoftware.decimalExpr.expression;

import trashsoftware.decimalExpr.builder.Node;
import trashsoftware.decimalExpr.builder.Values;
import trashsoftware.decimalExpr.numbers.Number;

public class Macro {

    private final Node root;

    public Macro(Node root) {
        this.root = root;
    }

//    public Node.BlockStmt getRoot() {
//        return root;
//    }

    public Number eval(Values values) {
        return root.eval(values);
    }

    @Override
    public String toString() {
        return "Macro{" + root + '}';
    }
}
