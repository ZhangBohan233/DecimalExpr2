package trashsoftware.decimalExpr.numbers;

public enum Infinity implements Number {
    POSITIVE_INFINITY,
    NEGATIVE_INFINITY;

    public boolean exact() {
        return true;
    }

    @Override
    public Number add(Number operand) {
        return this;
    }

    @Override
    public Number sub(Number right) {
        return this;
    }

    @Override
    public Number mul(Number right) {
        return this;
    }

    @Override
    public Number div(Number right) {
        return this;
    }

    @Override
    public Number pow(Number exp) {
        if (exp instanceof Real) if (((Real) exp).signum() == 0) return Rational.ONE;
        return this;
    }

    @Override
    public Number sqrt() {
        return this;
    }

    @Override
    public String toString() {
        return this == POSITIVE_INFINITY ? "inf" : "-inf";
    }
}
