package trashsoftware.decimalExpr.numbers;

import java.math.BigDecimal;

public abstract class Real implements Number {

    public abstract BigDecimal bigDecimalValue();

    public abstract Real neg();

    /**
     * @return -1 if this number is negative, 0 if this number is 0, 1 if this number is positive.
     */
    public abstract int signum();
}
