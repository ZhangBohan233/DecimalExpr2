package trashsoftware.decimalExpr;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import trashsoftware.decimalExpr.builder.Tokenizer;
import trashsoftware.decimalExpr.expression.BinaryOperator;
import trashsoftware.decimalExpr.expression.Function;
import trashsoftware.decimalExpr.expression.Operator;
import trashsoftware.decimalExpr.expression.UnaryOperator;
import trashsoftware.decimalExpr.numbers.*;
import trashsoftware.decimalExpr.numbers.Number;
import trashsoftware.decimalExpr.util.Calculations;

public class BuilderTest {

    Operator factorial = new UnaryOperator("!", Operator.PRECEDENCE_NEGATION, false) {
        @Override
        public Number eval(Number value) {
            return Calculations.factorial((Rational) value);
        }
    };

    Function cosine = new Function("cos", 1) {
        @Override
        protected Number evaluate(Number... arguments) {
            return Decimal.createDecimal(Math.cos(((Real) arguments[0]).doubleValue()));
        }
    };

    @Test
    void testParser() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("x/8")
                .variable("x")
                .build();
        decimalExpr.setVariable("x", 6);
        Number res = decimalExpr.evaluate();
        System.out.println(res);
    }

    @Test
    void testComplex() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("4x")
                .variable("x")
                .build();
        decimalExpr.setVariable("x", 3);
        Number res = decimalExpr.evaluate();
        System.out.println(res);
    }

    @Test
    void testAutoMultiply() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("2 x y i + 6")
                .variable("x")
                .variable("y")
                .build();
        decimalExpr.setVariable("x", 3);
        decimalExpr.setVariable("y", 5);
        Number res = decimalExpr.evaluate();
        System.out.println(res);
    }

    @Test
    void testMacro() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("x+2+m")
                .variable("x")
                .macro("m")
                .build();
        decimalExpr.setVariable("x", 4);
        decimalExpr.setMacro("m", "x+1");
        Number res = decimalExpr.evaluate();
        Assertions.assertEquals(res, Rational.valueOf(11));
    }

    @Test
    void testMacroOverride() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("x+2+m")
                .variable("x")
                .macro("m")
                .build();
        decimalExpr.setVariable("x", 4);
        decimalExpr.setMacro("m", "x+1");
        Number res = decimalExpr.evaluate();
        Assertions.assertEquals(res, Rational.valueOf(11));
        decimalExpr.setVariable("x", 5);
        decimalExpr.setMacro("m", "x+x*2");
        Number res2 = decimalExpr.evaluate();
        Assertions.assertEquals(res2, Rational.valueOf(22));
    }

    @Test
    void testMacroNested() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("x+2+m")
                .variable("x")
                .macro("m")
                .macro("n")
                .build();
        decimalExpr.setVariable("x", 4);
        decimalExpr.setMacro("m", "(2*n)+1+x");
        decimalExpr.setMacro("n", "3x");
        Number res = decimalExpr.evaluate();
//        System.out.println(res);
        Assertions.assertEquals(res, Rational.valueOf(35));
    }

    @Test
    void testUnary() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("x^2+-2")
                .variable("x")
                .build();
        decimalExpr.setVariable("x", 3);
        Number res = decimalExpr.evaluate();
        System.out.println(res);
    }

    @Test
    void testParenthesis() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("2*(3+6)")
                .showAst()
                .showTokens()
                .build();
        Number res = decimalExpr.evaluate();
        System.out.println(res);
    }

    @Test
    void testBuiltinFunction() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("abs(x)+sqrt(4)")
                .variable("x")
                .build();
        decimalExpr.setVariable("x", -7);
        Number res = decimalExpr.evaluate();
        Assertions.assertEquals(res, Rational.valueOf(9));
    }

    @Test
    void overrideOperator() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("2+5")
                .operator(new BinaryOperator("+", Operator.PRECEDENCE_ADDITION) {
                    @Override
                    public Number eval(Number left, Number right) {
                        return left.mul(right).add(Rational.ONE);
                    }
                })
                .build();
        Assertions.assertEquals(decimalExpr.evaluate(), Rational.valueOf(11));
    }

    @Test
    void testMacroFunction() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("sum(i, 2i, 0, 10)")
                .showAst()
                .showTokens()
                .build();
        Number res = decimalExpr.evaluate();
        Assertions.assertEquals(res, Rational.valueOf(110));
    }

    @Test
    void testMacroFunctionWithVar() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("sum(i, 2i x, 0, 10)")
                .showAst()
                .showTokens()
                .variable("x")
                .build();
        decimalExpr.setVariable("x", 2);
        Number res = decimalExpr.evaluate();
        System.out.println(res);
        Assertions.assertEquals(res, Rational.valueOf(220));
    }

    @Test
    void testMacroFunctionWithShadowedVar() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("i+sum(i, 2i x, 0, 10)+i")
                .variable("x")
                .build();
        decimalExpr.setVariable("x", 2);
        Number res = decimalExpr.evaluate();
        System.out.println(res);
        Assertions.assertEquals(res, Complex.createComplex(220, 2));
    }

    @Test
    void testDecimal() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("0.1234")
                .approxRational(true)
                .showAst()
                .showTokens()
                .build();
        Number res = decimalExpr.evaluate();
        System.out.println(res);
    }

    @Test
    void testRecurringNumber() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("0.{142857}")
                .showAst()
                .showTokens()
                .build();
        Number res = decimalExpr.evaluate();
        Assertions.assertEquals(res, Rational.fromFraction(1, 7));
    }

    @Test
    void testRecurringNumberNotRational() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("0.{142857}")
                .approxRational(false)
                .build();
        Assertions.assertThrows(NumberFormatException.class, decimalExpr::evaluate);
    }

    @Test
    void testRecurringNumberOutput() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("0.{142857}")
                .build();
        Number res = decimalExpr.evaluate();
        Assertions.assertEquals(res, Rational.fromFraction(1, 7));
        System.out.println(res.toDecimalString());
    }

    @Test
    void testRationalWithUnderscore() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("123_45.333_444")
                .approxRational(true)
                .build();
        Number res = decimalExpr.evaluate();
        System.out.println(res);
    }

    @Test
    void testDecimalWithUnderscore() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("123_45.333_444")
                .approxRational(false)
                .build();
        Number res = decimalExpr.evaluate();
        System.out.println(res);
    }

    @Test
    void testCustomRightUnaryOperator() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("2+5!")
                .operator(factorial)
                .build();
        Number res = decimalExpr.evaluate();
        Assertions.assertEquals(res, Rational.valueOf(122));
    }

    @Test
    void testCustomFunction() {
        DecimalExpr decimalExpr = new DecimalExpr.Builder()
                .expression("cos(0)")
                .function(cosine)
                .build();
        Number res = decimalExpr.evaluate();
        System.out.println(res);
    }
}
