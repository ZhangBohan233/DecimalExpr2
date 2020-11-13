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
    public static final Decimal PI = (Decimal) Decimal.fromDecimalString(
            "3.14159_26535_89793_23846_" +
                    "26433_83279_50288_41971_" +
                    "69399_37510_58209_74944_" +
                    "59230_78164_06286_20899_" +
                    "86280_34825_34211_70679");
    public static final Decimal E = (Decimal) Decimal.fromDecimalString(
            "2.71828_18284_59045_23536_" +
                    "02874_71352_66249_77572_" +
                    "47093_69995_95749_66967_" +
                    "62772_40766_30353_54759_" +
                    "45713_82178_52516_64274");

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

    public static Real fromDecimalString(String value) {
        return createDecimal(new BigDecimal(value.replace(Number.SPLITTER_STRING, "")));
    }

    public static boolean bigDecimalEqualsZero(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) == 0;
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
    public Number pow(int exp) {
        return createDecimal(value.pow(exp));
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

    @Override
    public String toDecimalString() {
        return toString();
    }
}
