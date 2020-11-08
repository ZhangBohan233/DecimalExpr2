package trashsoftware.decimalExpr.numbers;

import java.math.BigDecimal;

public abstract class Real implements Number, Comparable<Real> {

    public abstract BigDecimal bigDecimalValue();

    public abstract Real neg();

    /**
     * @return -1 if this number is negative, 0 if this number is 0, 1 if this number is positive.
     */
    public abstract int signum();

    /**
     * Returns 1 if this > o, 0 if this == o, -1 if this < o.
     *
     * @param o the other real number
     * @return the comparison result
     */
    @Override
    public int compareTo(Real o) {
        return ((Real) sub(o)).signum();
    }
}
