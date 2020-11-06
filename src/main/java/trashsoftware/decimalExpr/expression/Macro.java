package trashsoftware.decimalExpr.expression;

import trashsoftware.decimalExpr.builder.Node;

public class Macro {

    private final Node.BlockStmt root;

    public Macro(Node.BlockStmt root) {
        this.root = root;
    }

    public Node.BlockStmt getRoot() {
        return root;
    }
}
