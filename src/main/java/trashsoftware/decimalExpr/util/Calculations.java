package trashsoftware.decimalExpr.util;

import trashsoftware.decimalExpr.numbers.Rational;

public class Calculations {

    public static Rational factorial(Rational n) {
        if (n.signum() < 0) throw new ArithmeticException("Factorial only takes non-negative value.");
        if (!n.isInt()) throw new ArithmeticException("Only integer has factorial");
        Rational res = Rational.ONE;

        // use int because the range of java int is way bigger than the computation ability of computer
        int limit = n.intValue();

        for (int i = 1; i <= limit; i++) {
            res = (Rational) res.mul(Rational.valueOf(i));
        }
        return res;
    }

    public static double factorial(int n) {
        if (n < 0) throw new ArithmeticException("Factorial only takes non-negative value.");
        double res = 1.0;

        for (int i = 1; i <= n; i++) {
            res *= i;
        }
        return res;
    }

    public static Rational permutation(Rational n, Rational r) {
        if (n.compareTo(r) < 0) return Rational.ZERO;
        Rational nFact = factorial(n);
        Rational nSubRFact = factorial((Rational) n.sub(r));
        return (Rational) nFact.div(nSubRFact);
    }

    public static double permutation(int n, int r) {
        if (n < 0) return 0;
        double nFact = factorial(n);
        double nSubRFact = factorial(n - r);
        return nFact / nSubRFact;
    }

    public static Rational combination(Rational n, Rational r) {
        return (Rational) permutation(n, r).div(factorial(r));
    }

    public static double combination(int n, int r) {
        return permutation(n, r) / factorial(r);
    }
}
