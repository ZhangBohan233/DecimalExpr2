package trashsoftware.decimalExpr.expression;

import trashsoftware.decimalExpr.numbers.Number;
import trashsoftware.decimalExpr.numbers.Real;

public class Operators {

    public static final BinaryOperator ADD = new BinaryOperator("+", Operator.PRECEDENCE_ADDITION) {
        @Override
        public Number eval(Number left, Number right) {
            return left.add(right);
        }
    };

    public static final BinaryOperator SUB = new BinaryOperator("-", Operator.PRECEDENCE_SUBTRACTION) {
        @Override
        public Number eval(Number left, Number right) {
            return left.sub(right);
        }
    };

    public static final BinaryOperator MUL = new BinaryOperator("*", Operator.PRECEDENCE_MULTIPLICATION) {
        @Override
        public Number eval(Number left, Number right) {
            return left.mul(right);
        }
    };

    public static final BinaryOperator DIV = new BinaryOperator("/", Operator.PRECEDENCE_DIVISION) {
        @Override
        public Number eval(Number left, Number right) {
            return left.div(right);
        }
    };

    public static final BinaryOperator EXP = new BinaryOperator("^", Operator.PRECEDENCE_POWER) {
        @Override
        public Number eval(Number left, Number right) {
            return left.pow(right);
        }
    };

    public static final UnaryOperator NEG = new UnaryOperator("-", Operator.PRECEDENCE_NEGATION, true) {
        @Override
        public Number eval(Number value) {
            if (value instanceof Real) {
                return ((Real) value).neg();
            } else throw new ArithmeticException("Only real number supports negation.");
        }
    };
}
