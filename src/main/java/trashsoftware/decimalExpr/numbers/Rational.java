package trashsoftware.decimalExpr.numbers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Objects;

public class Rational extends Real {

    public static final Rational ZERO = Rational.fromBigInt(BigInteger.ZERO);
    public static final Rational ONE = Rational.fromBigInt(BigInteger.ONE);
    public static final Rational TWO = Rational.fromBigInt(BigInteger.TWO);
    public static final Rational NEG_ONE = ONE.negateRational();

    public final BigInteger numerator;
    public final BigInteger denominator;

    private Rational(BigInteger numerator, BigInteger denominator) {
        if (denominator.equals(BigInteger.ZERO)) throw new ArithmeticException("Divide by zero");
        BigInteger[] numDenom = simplify(numerator, denominator);
        this.numerator = numDenom[0];
        this.denominator = numDenom[1];
    }

    public static Rational fromBigInt(BigInteger bigInteger) {
        return new Rational(bigInteger, BigInteger.ONE);
    }

    public static Rational fromFraction(BigInteger numerator, BigInteger denominator) {
        return new Rational(numerator, denominator);
    }

    public static Rational fromFraction(long numerator, long denominator) {
        return new Rational(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }

    public static Rational valueOf(long value) {
        return fromBigInt(BigInteger.valueOf(value));
    }

    /**
     * Creates a {@code Rational} instance from decimal string representation.
     *
     * @param decimal string representation, radix 10.
     * @return new instance
     */
    public static Rational fromDecimalString(String decimal) {
        decimal = decimal.replace(Number.SPLITTER_STRING, "");
        int pointIndex = decimal.indexOf('.');
        BigInteger numerator, denominator;  // 被除数和除数，可能不是最简
        if (pointIndex == -1) {  // it is an integer
            numerator = new BigInteger(decimal);
            denominator = BigInteger.ONE;
        } else if (pointIndex == decimal.length() - 1) {
            numerator = new BigInteger(decimal.substring(0, decimal.length() - 1));
            denominator = BigInteger.ONE;
        } else {
            int frontRecurIndex = decimal.indexOf(FRONT_REPEAT_CHAR);
            int backRecurIndex = decimal.indexOf(BACK_REPEAT_CHAR);
            if (frontRecurIndex == -1) {
                if (backRecurIndex == -1) {
                    int decimalPlaceCount = decimal.length() - pointIndex - 1;
                    denominator = BigInteger.TEN.pow(decimalPlaceCount);
                    String bigNumber = decimal.substring(0, pointIndex) + decimal.substring(pointIndex + 1);
                    numerator = new BigInteger(bigNumber);
                } else {
                    throw new NumberFormatException("Recurring interval not closed");
                }
            } else {
                if (backRecurIndex == -1) {
                    throw new NumberFormatException("Recurring interval not closed");
                } else {
                    String repeatPart = decimal.substring(frontRecurIndex + 1, backRecurIndex);
                    int repeatLength = repeatPart.length();
                    int nonRepeatDecimalPlaces = frontRecurIndex - pointIndex - 1;
                    String decimalNoRepeat = decimal.substring(0, frontRecurIndex) + repeatPart;
                    String bigPart = appendRepeatPart(decimalNoRepeat, repeatPart,
                            repeatLength + nonRepeatDecimalPlaces);
                    String smallPart = appendRepeatPart(decimalNoRepeat, repeatPart, nonRepeatDecimalPlaces);
                    BigDecimal bigShift = BigDecimal.TEN.pow(repeatLength + nonRepeatDecimalPlaces);
                    BigDecimal smallShift = BigDecimal.TEN.pow(nonRepeatDecimalPlaces);
                    BigDecimal bigNumber = new BigDecimal(bigPart).multiply(bigShift);
                    BigDecimal smallNumber = new BigDecimal(smallPart).multiply(smallShift);
                    numerator = bigNumber.subtract(smallNumber).toBigIntegerExact();
                    denominator = bigShift.subtract(smallShift).toBigIntegerExact();
                }
            }
        }
        return new Rational(numerator, denominator);
    }

    /**
     * This method simplifies the numerator and denominator.
     * <p>
     * After this simplification, the gcd(numerator, denominator) = 1, and the denominator will be a natural number.
     *
     * @param numerator   the numerator
     * @param denominator the denominator
     * @return the array of numerator, denominator
     */
    private static BigInteger[] simplify(BigInteger numerator, BigInteger denominator) {
        int sig = numerator.signum() * denominator.signum();
        BigInteger gcd = gcd(numerator, denominator);
        if (sig < 0) {
            return new BigInteger[]{numerator.divide(gcd).abs().negate(), denominator.divide(gcd).abs()};
        } else {
            return new BigInteger[]{numerator.divide(gcd).abs(), denominator.divide(gcd).abs()};
        }
    }

    /**
     * Returns the greatest common divisor of two {@code BigInteger}s.
     * <p>
     * This method performs the classic Euclidean Algorithm.
     *
     * @param x a number
     * @param y another number
     * @return the greatest common divisor
     */
    public static BigInteger gcd(BigInteger x, BigInteger y) {
        BigInteger q, b;
        if (x.abs().compareTo(y.abs()) < 0) {  // a < b
            q = y;
            b = x;
        } else {
            q = x;
            b = y;
        }
        if (b.equals(BigInteger.ZERO)) return q;
        BigInteger[] dr;  // divisor and remainder
        while (!(dr = q.divideAndRemainder(b))[1].equals(BigInteger.ZERO)) {
            q = b;
            b = dr[1];
        }
        return b;
    }

    private static String appendRepeatPart(String original, String repeat, int appendCount) {
        StringBuilder stringBuilder = new StringBuilder(original);
        for (int i = 0; i < appendCount; i++) {
            char c = repeat.charAt(i % repeat.length());
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    @Override
    public BigDecimal bigDecimalValue() {
        return ratio(Decimal.DEFAULT_CONTEXT);
    }

    public long longValue() {
        if (isInt()) return numerator.longValue();
        else throw new ArithmeticException("Cannot convert non-int rational to integer");
    }

    public int intValue() {
        return (int) longValue();
    }

    private BigDecimal ratio(MathContext roundingMode) {
        return new BigDecimal(numerator).divide(new BigDecimal(denominator), roundingMode);
    }

    public boolean isInt() {
        return denominator.equals(BigInteger.ONE);
    }

    public boolean exact() {
        return true;
    }

    @Override
    public Number add(Number right) {
        if (right instanceof Rational) {
            return addRational((Rational) right);
        } else if (right instanceof Decimal) {
            return Decimal.createDecimal(bigDecimalValue().add(((Decimal) right).bigDecimalValue()));
        } else if (right instanceof Complex) {
            Complex c = (Complex) right;
            return Complex.createComplex((Real) add(c.real), c.imaginary);
        }
        throw new ArithmeticException("Unsupported operation between rational and " + right.getClass().getName());
    }

    @Override
    public Number sub(Number right) {
        if (right instanceof Rational) {
            return subtractRational((Rational) right);
        } else if (right instanceof Decimal) {
            return Decimal.createDecimal(bigDecimalValue().subtract(((Decimal) right).bigDecimalValue()));
        } else if (right instanceof Complex) {
            Complex c = (Complex) right;
            return Complex.createComplex((Real) sub(c.real), c.imaginary);
        }
        throw new ArithmeticException("Unsupported operation between rational and " + right.getClass().getName());
    }

    @Override
    public Number mul(Number right) {
        if (right instanceof Rational) {
            return multiplyRational((Rational) right);
        } else if (right instanceof Decimal) {
            return Decimal.createDecimal(bigDecimalValue().multiply(((Decimal) right).bigDecimalValue()));
        } else if (right instanceof Complex) {
            Complex c = (Complex) right;
            return Complex.createComplex((Real) mul(c.real), (Real) mul(c.imaginary));
        }
        throw new ArithmeticException("Unsupported operation between rational and " + right.getClass().getName());
    }

    @Override
    public Number div(Number right) {
        if (right instanceof Rational) {
            return divideRational((Rational) right);
        } else if (right instanceof Decimal) {
            return Decimal.createDecimal(bigDecimalValue().divide(((Decimal) right).bigDecimalValue(),
                    Decimal.DEFAULT_CONTEXT));
        } else if (right instanceof Complex) {
            Complex c = (Complex) right;
            return Complex.createComplex((Real) div(c.real), (Real) div(c.imaginary));
        }
        throw new ArithmeticException("Unsupported operation between rational and " + right.getClass().getName());
    }

    @Override
    public Number pow(Number exp) {
        if (exp instanceof Rational) {
            Rational r = (Rational) exp;
            if (r.isInt()) return pow(r.numerator.intValue());
        }
        return null;
    }

    @Override
    public Number pow(int exp) {
        return integerPower(exp);
    }

    @Override
    public Real neg() {
        return negateRational();
    }

    @Override
    public Number sqrt() {
        if (signum() < 0) {
            Rational pos = negateRational();
            return Complex.createPureComplex(pos.nonNegSqrt());
        } else {
            return nonNegSqrt();
        }
    }

    @Override
    public int signum() {
        return numerator.signum();
    }

//    @Override
//    public int compareTo(Real o) {
//        if (o instanceof Rational) {
//            Rational diff = subtractRational((Rational) o);
//            return diff.signum();
//        } else {
//            return bigDecimalValue().compareTo(o.bigDecimalValue());
//        }
//    }

    @Override
    public String toString() {
        return isInt() ? numerator.toString() : (numerator.toString() + "/" + denominator.toString());
    }

    @Override
    public String toDecimalString() {
        return bigDecimalValue().toPlainString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rational rational = (Rational) o;

        return Objects.equals(numerator, rational.numerator) &&
                Objects.equals(denominator, rational.denominator);
    }

    private Rational addRational(Rational val) {
        BigInteger denom = denominator.multiply(val.denominator);
        BigInteger num = numerator.multiply(val.denominator).add(val.numerator.multiply(denominator));
        return new Rational(num, denom);
    }

    private Rational subtractRational(Rational b) {
        Rational negativeB = b.negateRational();
        return addRational(negativeB);
    }

    private Rational multiplyRational(Rational multiplier) {
        BigInteger denom = denominator.multiply(multiplier.denominator);
        BigInteger num = numerator.multiply(multiplier.numerator);
        return new Rational(num, denom);
    }

    private Rational divideRational(Rational divisor) {
        BigInteger denom = denominator.multiply(divisor.numerator);
        BigInteger num = numerator.multiply(divisor.denominator);
        return new Rational(num, denom);
    }

    private Rational moduloRational(Rational divisor) {
        BigInteger denom = denominator.multiply(divisor.denominator);
        BigInteger thisNum = numerator.multiply(divisor.denominator);
        BigInteger otherNum = divisor.numerator.multiply(denominator);
        BigInteger rem = thisNum.remainder(otherNum);
        return new Rational(rem, denom);
    }

    private Rational negateRational() {
        return new Rational(numerator.negate(), denominator);
    }

    private Real nonNegSqrt() {
        BigInteger[] numeratorSqrtRem = numerator.sqrtAndRemainder();
        BigInteger[] denominatorSqrtRem = denominator.sqrtAndRemainder();
        if (numeratorSqrtRem[1].equals(BigInteger.ZERO) && denominatorSqrtRem[1].equals(BigInteger.ZERO)) {
            return Rational.fromFraction(numeratorSqrtRem[0], denominatorSqrtRem[0]);
        } else {
            return Decimal.createDecimal(bigDecimalValue().sqrt(Decimal.DEFAULT_CONTEXT));
        }
    }

    Rational integerPower(int pow) {
        int nonNegPow = pow;
        if (pow < 0) nonNegPow = -pow;
        BigInteger newNumerator = numerator.pow(nonNegPow);
        BigInteger newDenominator = denominator.pow(nonNegPow);
        Rational res = fromFraction(newNumerator, newDenominator);
        if (pow < 0) {
            return (Rational) ONE.div(res);
        } else return res;
    }
}
