package trashsoftware.decimalExpr.numbers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class NumbersTest {

    @Test
    void testComplex() {
        Number a = Complex.createComplex(Rational.fromFraction(3, 4),
                Rational.fromFraction(1, 2));
        System.out.println(a);
    }

    @Test
    void testComplexPure() {
        Number a = Complex.createPureComplex(Rational.fromFraction(1, 2));
        System.out.println(a);
        Number b = a.add(Rational.valueOf(2));
        System.out.println(b);
        Number c = Complex.I.mul(Rational.valueOf(3));
        System.out.println(c);
    }

    @Test
    void testRationalIntPow() {
        Rational a = Rational.fromFraction(3, 4).integerPower(0);
        System.out.println(a);
        Rational b = Rational.fromFraction(-3, 4).integerPower(3);
        System.out.println(b);
    }

    @Test
    void testImaginaryMultiply() {
        Number a = Complex.I.mul(Complex.I);
        Assertions.assertEquals(a, Rational.NEG_ONE);
    }

    @Test
    void testRationalSqrtNeg() {
        Rational r = Rational.valueOf(-9);
        Assertions.assertEquals(r.sqrt().pow(Rational.TWO), r);
    }

    @Test
    void testIrrationalToRational() {
        Number n = Rational.TWO.sqrt().sub(Rational.TWO.sqrt());
        Number m = Decimal.E.sub(Decimal.E);
        Assertions.assertEquals(n, Rational.ZERO);
        Assertions.assertEquals(m, Rational.ZERO);
    }

    @Test
    void testComplexSqrt() {
        Number square = Complex.createComplex(3, 2).pow(Rational.TWO);
        System.out.println(square);
        Number sqrt = square.sqrt();
        System.out.println(sqrt);
    }
}
