package trashsoftware.decimalExpr.expression;

import trashsoftware.decimalExpr.numbers.Number;

public abstract class UnaryOperator extends Operator {

    /**
     * Whether operator is at the left side of the operand.
     *
     * For example, "-1" -> true, "5!" -> false
     */
    public final boolean operatorAtLeft;

    public UnaryOperator(String symbol, int precedence, boolean operatorAtLeft) {
        super(symbol, precedence);

        this.operatorAtLeft = operatorAtLeft;
    }

    public abstract Number eval(Number value);
}
