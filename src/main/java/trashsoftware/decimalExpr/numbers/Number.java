package trashsoftware.decimalExpr.numbers;

public interface Number {

    /**
     * Returns {@code true} if this number is exact.
     * <p>
     * A number is exact if and only if:
     * 1. it is a rational
     * 2. it is a complex with exact real and imaginary parts
     *
     * @return {@code true} iff this value is exact, not an approximation.
     */
    boolean exact();

    Number add(Number right);

    Number sub(Number right);

    Number mul(Number right);

    Number div(Number right);

    Number pow(Number exp);

    Number pow(int exp);

    Number sqrt();
}
