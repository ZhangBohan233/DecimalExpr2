package trashsoftware.decimalExpr.expression;

import trashsoftware.decimalExpr.numbers.Complex;
import trashsoftware.decimalExpr.numbers.Number;
import trashsoftware.decimalExpr.numbers.Rational;
import trashsoftware.decimalExpr.numbers.Real;

public class Functions {

    public static final Function ABS = new Function("abs", 1) {
        @Override
        protected Number evaluate(Number... arguments) {
            Number arg = arguments[0];
            if (arg instanceof Real) {
                Real real = (Real) arg;
                if (real.signum() < 0) return real.neg();
                else return real;
            } else if (arg instanceof Complex) {  // modulo of complex
                Complex c = (Complex) arg;
                return c.real.pow(Rational.TWO).add(c.imaginary.pow(Rational.TWO)).sqrt();
            } else {
                throw new ArithmeticException("Unexpected number type.");
            }
        }
    };

    public static final Function SQRT = new Function("sqrt", 1) {
        @Override
        protected Number evaluate(Number... arguments) {
            return arguments[0].sqrt();
        }
    };

    // Macro functions

    public static final MacroFunction SUM = new MacroFunction("sum", 2) {
        @Override
        protected Number evaluate(String invariant, Macro macro, Number... arguments) {
            Number cur = arguments[0];
            Number stop = arguments[1];
            Number sum = Rational.ZERO;

            while (true) {
                setValue(invariant, cur);
                sum = sum.add(evalMacro(macro));
                if (cur.equals(stop)) break;
                cur = cur.add(Rational.ONE);
            }
            return sum;
        }
    };
}
