package trashsoftware.decimalExpr.numbers;

import trashsoftware.decimalExpr.util.Calculations;

import java.util.Objects;

public class Complex implements Number {

    /**
     * The pure imaginary number i
     */
    public static final Complex I = new Complex(Rational.ZERO, Rational.ONE);

    public final Real real;
    public final Real imaginary;

    private Complex(Real realPart, Real imaginaryPart) {
        this.real = realPart;
        this.imaginary = imaginaryPart;
    }

    /**
     * Creates a probable complex number.
     * <p>
     * Let the complex number be a+bi. If b != 0, return a+bi. Otherwise return a.
     *
     * @param realPart      real part
     * @param imaginaryPart imaginary part
     * @return the newly created complex if imaginary part is not 0, otherwise returns the real part.
     */
    public static Number createComplex(Real realPart, Real imaginaryPart) {
        if (imaginaryPart.signum() == 0) return realPart;
        else return new Complex(realPart, imaginaryPart);
    }

    /**
     * Creates a probable complex with no real part.
     *
     * @param imaginaryPart imaginary part
     * @return the newly created complex if imaginary part is not 0, otherwise returns 0.
     */
    public static Number createPureComplex(Real imaginaryPart) {
        if (imaginaryPart.signum() == 0) return Rational.ZERO;
        else return new Complex(Rational.ZERO, imaginaryPart);
    }

    public static Number createComplex(long realPart, long imaginaryPart) {
        return createComplex(Rational.valueOf(realPart), Rational.valueOf(imaginaryPart));
    }

    public boolean exact() {
        return real.exact() && imaginary.exact();
    }

    @Override
    public Number add(Number right) {
        if (right instanceof Complex) {
            Complex c = (Complex) right;
            return createComplex((Real) real.add(c.real), (Real) imaginary.add(c.imaginary));
        } else {
            return createComplex((Real) real.add(right), imaginary);
        }
    }

    @Override
    public Number sub(Number right) {
        if (right instanceof Complex) {
            Complex c = (Complex) right;
            return createComplex((Real) real.sub(c.real), (Real) imaginary.sub(c.imaginary));
        } else {
            return createComplex((Real) real.sub(right), imaginary);
        }
    }

    @Override
    public Number mul(Number right) {
        if (right instanceof Complex) {
            Complex c = (Complex) right;
            return multiplyComplex(c);
        } else {
            return createComplex((Real) real.mul(right), (Real) imaginary.mul(right));
        }
    }

    @Override
    public Number div(Number right) {
        if (right instanceof Complex) {
            Complex c = (Complex) right;
            return divideComplex(c);
        } else {
            return createComplex((Real) real.div(right), (Real) imaginary.div(right));
        }
    }

    @Override
    public Number pow(Number exp) {
        if (exp instanceof Rational) {
            Rational r = (Rational) exp;
            if (r.isInt()) return complexPowIntLoop(r.intValue());
        }
        throw new ArithmeticException("Only integer power of complex is supported. ");
    }

    @Override
    public Number pow(int exp) {
        return complexPowIntLoop(exp);
    }

    /**
     * Returns the square root of this complex number.
     * <p>
     * Calculation:
     * Let this be a+bi, its square root be x+yi
     * Then a+bi = (x+yi)^2
     * a = x^2 - y^2
     * b = 2xy
     * Then
     * y = b/2x
     * a = x^2 - b^2 / (4x^2)
     * Multiply each term by 4x^2 then
     * 4x^4 - 4ax^2 - b^2 = 0
     * Substitute x^2 by u we get
     * 4u^2 - 4au - b^2 = 0
     * Using quadratic formula
     * u = x^2 = [2a +- sqrt(a^2 + b^2)] / 2
     * Then y^2 = x^2 - a
     *
     * @return the square root of this complex number
     */
    @Override
    public Number sqrt() {
        Number u = real.add(real.pow(Rational.TWO).add(imaginary.pow(Rational.TWO)).sqrt()).div(Rational.TWO);
        Number x = u.sqrt();
        Number y = u.sub(real).sqrt();
        return Complex.createComplex((Real) x, (Real) y);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Complex) {
            Complex c = (Complex) o;
            return Objects.equals(real, c.real) && Objects.equals(imaginary, c.imaginary);
        } else return false;
    }

    @Override
    public String toString() {
        return real.signum() == 0 ?
                (imaginary.equals(Rational.ONE) ? "i" : imaginary + "i") :
                (real + "+" + imaginary + "i");
    }

    private Number multiplyComplex(Complex other) {
        Real realPart = (Real) real.mul(other.real).sub(imaginary.mul(other.imaginary));  // ac - bd
        Real imPart = (Real) imaginary.mul(other.real).add(real.mul(other.imaginary));  // bc + ad
        return createComplex(realPart, imPart);
    }

    private Number divideComplex(Complex other) {
        Real denom = (Real) other.real.pow(Rational.TWO).add(other.imaginary.pow(Rational.TWO));
        // c^2 + d^2
        Real realPartNum = (Real) real.mul(other.real).add(imaginary.mul(other.imaginary));  // ac + bd
        Real imPartNum = (Real) imaginary.mul(other.real).sub(real.mul(other.imaginary));  // bc - ad
        return Complex.createComplex((Real) realPartNum.div(denom), (Real) imPartNum.div(denom));
    }

    /**
     * Integer power of complex, using naive algorithm.
     *
     * @param exp the integer exponential
     * @return the complex power
     */
    Number complexPowIntLoop(int exp) {
        if (exp == 0) return Rational.ONE;
        int posExp = exp > 0 ? exp : -exp;
        Number res = this;
        for (int i = 1; i < posExp; i++) {
            res = res.mul(this);
        }
        return exp > 0 ? res : Rational.ONE.div(res);
    }

    /**
     * Integer power of complex, using binomial theorem.
     * <p>
     * This algorithm is faster than direct power, but the double version might not be accurate when exp is large.
     *
     * @param exp the integer exponential
     * @return the complex power
     */
    Number complexPowerIntBinomial(int exp) {
        if (exp == 0) return Rational.ONE;
        int n = exp > 0 ? exp : -exp;

        Number realPart = Rational.ZERO;
        Number imPart = Rational.ZERO;

        for (int r = 0; r <= n; r++) {
            long comb = (long) Calculations.combination(n, r);
            Rational combR = Rational.valueOf(comb);
            Number aPow = real.pow(n - r);
            Number bPow = imaginary.pow(r);
            Number mulRes = combR.mul(aPow).mul(bPow);
//            System.out.println(aPow + " " + bPow + " " + combR);

            int rMod4 = r % 4;  // i^(4k) = 1, i^(4k+1) = i, i^(4k+2) = -1, i^(4k+3) = -i
            switch (rMod4) {
                case 0:
                    realPart = realPart.add(mulRes);
                    break;
                case 1:
                    imPart = imPart.add(mulRes);
                    break;
                case 2:
                    realPart = realPart.sub(mulRes);
                    break;
                default:
                    imPart = imPart.sub(mulRes);
                    break;
            }
        }

        Number res = createComplex((Real) realPart, (Real) imPart);
        return exp > 0 ? res : Rational.ONE.div(res);
    }
}
