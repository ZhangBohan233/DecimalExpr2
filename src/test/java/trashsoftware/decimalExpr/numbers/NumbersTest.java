package trashsoftware.decimalExpr.numbers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import trashsoftware.decimalExpr.util.Calculations;

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

    @Test
    void testFactorial() {
        Number fact = Calculations.factorial(Rational.valueOf(30));
        System.out.println(fact);
    }

    @Test
    void testPermutation() {
        Number per = Calculations.permutation(Rational.valueOf(9), Rational.valueOf(4));
        System.out.println(per);
        Number comb = Calculations.combination(Rational.valueOf(9), Rational.valueOf(8));
        System.out.println(comb);
        double d = Calculations.combination(50, 50);
        System.out.println(d);
    }

    @Test
    void testComplexPower() {
        Complex a = (Complex) Complex.createComplex(2, 2);

//        Number pow1 = a.complexPowerIntBinomial(20);
//        System.out.println(pow1);
        System.out.println(a.complexPowerIntBinomial(21));
        System.out.println(a.complexPowIntLoop(21));
//        System.out.println(pow1.mul(a));
    }

    @Test
    void testComplexPowerTwoWay() {
        Complex a = (Complex) Complex.createComplex(3, 2);

//        System.out.println(Calculations.combination(21, 15));

        long t1 = System.currentTimeMillis();
        System.out.println(a.complexPowIntLoop(50));
        long t2 = System.currentTimeMillis();
        System.out.println(a.complexPowerIntBinomial(50));
        long t3 = System.currentTimeMillis();

        System.out.println(t2 - t1);
        System.out.println(t3 - t2);
    }

    @Test
    void testRationalRecurring() {
        Number a = Rational.fromDecimalString("0.{142857}");
        System.out.println(a);
    }
}
