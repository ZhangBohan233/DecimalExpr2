package trashsoftware.decimalExpr.builder;

import trashsoftware.decimalExpr.BuildException;
import trashsoftware.decimalExpr.expression.*;
import trashsoftware.decimalExpr.numbers.Decimal;
import trashsoftware.decimalExpr.numbers.Number;
import trashsoftware.decimalExpr.numbers.Rational;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Node {

    public abstract Number eval(Values values);

    public static abstract class Expression extends Node {

    }

    public static class BlockStmt extends Node {

        private final List<Node> nodes = new ArrayList<>();

        public void add(Node node) {
            nodes.add(node);
        }

        public Node get(int index) {
            return nodes.get(index);
        }

        public int size() {
            return nodes.size();
        }

        public List<Node> getNodes() {
            return nodes;
        }

        @Override
        public Number eval(Values values) {
            return nodes.get(0).eval(values);
        }

        @Override
        public String toString() {
            return "BlockStmt" + nodes;
        }
    }

    static abstract class TreeExpr extends Expression {
        abstract int precedence();

        abstract boolean notFulfilled();
    }

    static abstract class LeafExpr extends Expression {

    }

    static class UnaryOperatorNode extends TreeExpr {

        final UnaryOperator operator;
        Expression operand;

        UnaryOperatorNode(UnaryOperator operator) {
            this.operator = operator;
        }

        void setOperand(Expression operand) {
            this.operand = operand;
        }

        @Override
        int precedence() {
            return operator.precedence;
        }

        @Override
        boolean notFulfilled() {
            return operand == null;
        }

        @Override
        public Number eval(Values values) {
            return operator.eval(operand.eval(values));
        }

        @Override
        public String toString() {
            return operator.operatorAtLeft ?
                    String.format("Unary(%s%s)", operator, operand) :
                    String.format("Unary(%s%s)", operand, operator);
        }
    }

    static class BinaryOperatorNode extends TreeExpr {
        final BinaryOperator operator;
        Expression left;
        Expression right;

        BinaryOperatorNode(BinaryOperator operator) {
            this.operator = operator;
        }

        void setLeft(Expression left) {
            this.left = left;
        }

        void setRight(Expression right) {
            this.right = right;
        }

        @Override
        int precedence() {
            return operator.precedence;
        }

        @Override
        boolean notFulfilled() {
            return left == null || right == null;
        }

        @Override
        public Number eval(Values values) {
            return operator.eval(left.eval(values), right.eval(values));
        }

        @Override
        public String toString() {
            return String.format("Binary(%s %s %s)", left, operator, right);
        }
    }

    abstract static class NameNode extends LeafExpr {
        final String name;

        NameNode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    static class VarNameNode extends NameNode {
        VarNameNode(String name) {
            super(name);
        }

        @Override
        public Number eval(Values values) {
            return values.getVariable(name);
        }
    }

    static class MacroNameNode extends NameNode {
        MacroNameNode(String name) {
            super(name);
        }

        @Override
        public Number eval(Values values) {
            return values.getMacro(name).eval();
        }
    }

    static class UndefinedNameNode extends NameNode {
        UndefinedNameNode(String name) {
            super(name);
        }

        @Override
        public Number eval(Values values) {
            throw new BuildException("Unexpected name " + name + ".");
        }
    }

    static class IntNode extends LeafExpr {
        final Rational value;

        IntNode(String literal) {
            value = Rational.fromBigInt(new BigInteger(literal));
        }

        @Override
        public Number eval(Values values) {
            return value;
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }

    static class DecimalNode extends LeafExpr {
        final String literal;

        DecimalNode(String literal) {
            this.literal = literal;
        }

        @Override
        public Number eval(Values values) {
            if (values.isApproxRational()) {
                return Rational.fromDecimalString(literal);
            } else {
                return Decimal.fromDecimalString(literal);
            }
        }

        @Override
        public String toString() {
            return "DecimalNode{" + literal + '}';
        }
    }

    static class FunctionCall extends Expression {
        final Function function;
        final BlockStmt args;

        FunctionCall(Function function, BlockStmt args) {
            this.function = function;
            this.args = args;
        }

        @Override
        public Number eval(Values values) {
            Number[] args = evalArgs(values);
            return function.eval(args);
        }

        private Number[] evalArgs(Values values) {
            Number[] res = new Number[args.size()];
            for (int i = 0; i < res.length; i++) {
                res[i] = args.get(i).eval(values);
            }
            return res;
        }

        @Override
        public String toString() {
            return function.name + "(" + args + ")";
        }
    }

    static class MacroFunctionCall extends Expression {
        final MacroFunction function;
        final BlockStmt args;

        MacroFunctionCall(MacroFunction function, BlockStmt args) {
            this.function = function;
            this.args = args;
        }

        @Override
        public Number eval(Values values) {
            Node invariantNode = args.get(0);
            if (!(invariantNode instanceof NameNode)) {
                throw new BuildException("Invariant must be name, got " + invariantNode.getClass().getName());
            }
            String name = ((NameNode) invariantNode).name;

            Values.MacroValues macroValues = new Values.MacroValues(values);
            Node macroNode = args.get(1);
            Macro macro = new Macro(macroNode, macroValues);

            Number[] args = evalArgs(values);
            return function.eval(name, macro, args);
        }

        private Number[] evalArgs(Values values) {
            Number[] res = new Number[args.size() - 2];
            for (int i = 0; i < res.length; i++) {
                res[i] = args.get(i + 2).eval(values);
            }
            return res;
        }

        @Override
        public String toString() {
            return function.name + "(" + args + ")";
        }
    }
}
