package trashsoftware.decimalExpr.builder;

import trashsoftware.decimalExpr.expression.BinaryOperator;
import trashsoftware.decimalExpr.expression.Operators;
import trashsoftware.decimalExpr.expression.UnaryOperator;

import java.util.ArrayList;
import java.util.List;

public class AstBuilder {

    private List<Node> stack = new ArrayList<>();
    private final Node.BlockStmt root = new Node.BlockStmt();

    void addNode(Node node) {
        stack.add(node);
    }

    void finishLine() {
        if (!stack.isEmpty()) {
            stack = proceedDirectMultiply(stack);
            boolean hasExpr = false;
            for (Node node : stack) {
                if (node instanceof Node.TreeExpr && ((Node.TreeExpr) node).notFulfilled())
                    hasExpr = true;
            }
            if (hasExpr) buildExpr();
            root.getNodes().addAll(stack);
            stack.clear();
        }
    }

    /**
     * Proceed situations like "2x", "ib", "2xy"
     *
     * Precondition: {@code oldStack} is not empty
     */
    private static List<Node> proceedDirectMultiply(List<Node> oldStack) {
        assert !oldStack.isEmpty();
        List<Node> newStack = new ArrayList<>();
        for (int i = 0; i < oldStack.size() - 1; i++) {
            Node front = oldStack.get(i);
            Node back = oldStack.get(i + 1);
            newStack.add(front);
            if (front instanceof Node.LeafExpr && back instanceof Node.LeafExpr) {
                newStack.add(new Node.BinaryOperatorNode(Operators.MUL));
            }

            // 2 x y
            // 2 * x y
        }
        newStack.add(oldStack.get(oldStack.size() - 1));

        return newStack;
    }

    private void buildExpr() {
        while (true) {
//                System.out.println(list);
            int maxPre = -1;
            int index = 0;

            for (int i = 0; i < stack.size(); i++) {
//                for (int i = list.size() - 1; i >= 0; i--) {
                Node node = stack.get(i);
                if (node instanceof Node.TreeExpr && ((Node.TreeExpr) node).notFulfilled()) {
                    int pre = ((Node.TreeExpr) node).precedence();
                    if (node instanceof Node.UnaryOperatorNode) {

                        // eval right side unary operator first
                        // for example, "- -3" is -(-3)
                        if (pre >= maxPre) {
                            maxPre = pre;
                            index = i;
                        }
                    } else if (node instanceof Node.BinaryOperatorNode) {

                        // eval left side binary operator first
                        // for example, "2 * 8 / 4" is (2 * 8) / 4
                        if (pre > maxPre) {
                            maxPre = pre;
                            index = i;
                        }
                    }
                }
            }

            if (maxPre == -1) break;  // no expr found

            Node.TreeExpr expr = (Node.TreeExpr) stack.get(index);
            if (expr instanceof Node.UnaryOperatorNode) {
                Node.UnaryOperatorNode ue = (Node.UnaryOperatorNode) expr;
                Node value;
                if (ue.operator.operatorAtLeft) {
                    value = stack.remove(index + 1);
                } else {
                    value = stack.remove(index - 1);
                }
                ue.setOperand((Node.Expression) value);
            } else if (expr instanceof Node.BinaryOperatorNode) {
                Node right = stack.remove(index + 1);
                Node left = stack.remove(index - 1);
                ((Node.BinaryOperatorNode) expr).setLeft((Node.Expression) left);
                ((Node.BinaryOperatorNode) expr).setRight((Node.Expression) right);
            }
        }
    }

    Node.BlockStmt getRoot() {
        return root;
    }
}
