package trashsoftware.decimalExpr;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import trashsoftware.decimalExpr.numbers.Number;
import trashsoftware.decimalExpr.numbers.Rational;

public class BuilderTest {

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
        System.out.println(res);
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
}
