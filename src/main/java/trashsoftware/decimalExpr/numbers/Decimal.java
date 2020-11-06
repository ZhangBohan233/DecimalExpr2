package trashsoftware.decimalExpr.numbers;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * A class that denotes all real numbers that are not exact.
 * <p>
 * This includes irrational numbers and numbers that is "not sure".
 */
public class Decimal extends Real {

    /**
     * Static final field.
     */
    public static final Decimal E = new Decimal(BigDecimal.valueOf(Math.E));
    public static final Decimal PI = new Decimal(BigDecimal.valueOf(Math.PI));

    static final MathContext DEFAULT_CONTEXT = new MathContext(64, RoundingMode.HALF_UP);

    /**
     * Instant field.
     */
    public final BigDecimal value;

    private Decimal(BigDecimal value) {
        this.value = value;
    }

    public static Real createDecimal(BigDecimal value) {
        if (bigDecimalEqualsZero(value)) return Rational.ZERO;
        else return new Decimal(value);
    }

    public static Real createDecimal(double value) {
        if (value == 0) return Rational.ZERO;
        else return new Decimal(BigDecimal.valueOf(value));
    }

    @Override
    public BigDecimal bigDecimalValue() {
        return value;
    }

    public double doubleValue() {
        return value.doubleValue();
    }

    public boolean exact() {
        return false;
    }

    @Override
    public Number add(Number right) {
        if (right instanceof Real) {
            return createDecimal(value.add(((Real) right).bigDecimalValue()));
        } else if (right instanceof Complex) {
            Complex c = (Complex) right;
            return Complex.createComplex(createDecimal(value.add(c.real.bigDecimalValue())),
                    c.imaginary);
        }
        throw new ArithmeticException("Unsupported operation between decimal and " + right.getClass().getName());
    }

    @Override
    public Number sub(Number right) {
        if (right instanceof Real) {
            return createDecimal(value.subtract(((Real) right).bigDecimalValue()));
        } else if (right instanceof Complex) {
            Complex c = (Complex) right;
            return Complex.createComplex(createDecimal(value.subtract(c.real.bigDecimalValue())),
                    c.imaginary);
        }
        throw new ArithmeticException("Unsupported operation between decimal and " + right.getClass().getName());
    }

    @Override
    public Number mul(Number right) {
        if (right instanceof Real) {
            return createDecimal(value.multiply(((Real) right).bigDecimalValue()));
        } else if (right instanceof Complex) {
            Complex c = (Complex) right;
            return Complex.createComplex(createDecimal(value.multiply(c.real.bigDecimalValue())),
                    createDecimal(value.multiply(c.imaginary.bigDecimalValue())));
        }
        throw new ArithmeticException("Unsupported operation between decimal and " + right.getClass().getName());
    }

    @Override
    public Number div(Number right) {
        if (right instanceof Real) {
            return createDecimal(value.divide(((Real) right).bigDecimalValue(), DEFAULT_CONTEXT));
        } else if (right instanceof Complex) {
            Complex c = (Complex) right;
            return Complex.createComplex(createDecimal(value.divide(c.real.bigDecimalValue(), DEFAULT_CONTEXT)),
                    createDecimal(value.divide(c.imaginary.bigDecimalValue(), DEFAULT_CONTEXT)));
        }
        throw new ArithmeticException("Unsupported operation between decimal and " + right.getClass().getName());
    }

    @Override
    public Number pow(Number exp) {
        if (exp instanceof Rational) {
            if (exp.equals(Rational.ZERO)) return Rational.ONE;
            else if (((Rational) exp).isInt()) {
                return createDecimal(value.pow(((Rational) exp).intValue()));
            } else {
                return createDecimal(Math.pow(value.doubleValue(),
                        ((Rational) exp).bigDecimalValue().doubleValue()));
            }
        } else if (exp instanceof Real) {
            return createDecimal(Math.pow(value.doubleValue(),
                    ((Real) exp).bigDecimalValue().doubleValue()));
        } else {
            throw new ArithmeticException("Cannot compute complex exponential");
        }
    }

    @Override
    public Real neg() {
        return createDecimal(value.negate());
    }

    @Override
    public Number sqrt() {
        return createDecimal(value.sqrt(DEFAULT_CONTEXT));
    }

    @Override
    public int signum() {
        return value.signum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Decimal decimal = (Decimal) o;

        return Objects.equals(value, decimal.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public static boolean bigDecimalEqualsZero(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }
}