package trashsoftware.decimalExpr.expression;

import trashsoftware.decimalExpr.numbers.Number;

public abstract class BinaryOperator extends Operator {
    public BinaryOperator(String symbol, int precedence) {
        super(symbol, precedence);
    }

    public abstract Number eval(Number left, Number right);
}
