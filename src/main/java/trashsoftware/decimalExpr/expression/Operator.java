package trashsoftware.decimalExpr.expression;

public class Operator {

    /**
     * Static final field
     */
    public static final int PRECEDENCE_ADDITION = 10;
    public static final int PRECEDENCE_SUBTRACTION = 10;
    public static final int PRECEDENCE_NEGATION = 11;
    public static final int PRECEDENCE_POSITIVE_SIGN = 11;
    public static final int PRECEDENCE_MULTIPLICATION = 20;
    public static final int PRECEDENCE_DIVISION = 20;
    public static final int PRECEDENCE_MODULO = 20;
    public static final int PRECEDENCE_POWER = 30;

    public final int precedence;
    public final String symbol;

    public Operator(String symbol, int precedence) {
        this.symbol = symbol;
        this.precedence = precedence;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
