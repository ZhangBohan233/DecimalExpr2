package trashsoftware.decimalExpr;

import trashsoftware.decimalExpr.builder.*;
import trashsoftware.decimalExpr.expression.*;
import trashsoftware.decimalExpr.numbers.Complex;
import trashsoftware.decimalExpr.numbers.Number;
import trashsoftware.decimalExpr.numbers.Rational;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DecimalExpr {

    public static final Map<String, BinaryOperator> BUILTIN_BINARY_OPS = Map.of(
            "+", Operators.ADD,
            "-", Operators.SUB,
            "*", Operators.MUL,
            "/", Operators.DIV,
            "^", Operators.EXP
    );

    public static final Map<String, UnaryOperator> BUILTIN_UNARY_OPS = Map.of(
            "-", Operators.NEG
    );

    public static final Map<String, AbstractFunction> BUILTIN_FUNCTIONS = Map.of(
            "abs", Functions.ABS,
            "sqrt", Functions.SQRT,
            "sum", Functions.SUM
    );

    /**
     * Instant field
     */
    private final Map<String, UnaryOperator> unaryOperators;
    private final Map<String, BinaryOperator> binaryOperators;
    private final Map<String, AbstractFunction> functions;
    private final Values values;
    private Node.BlockStmt root;

    /**
     * Whether to deal decimal numbers as rational.
     * <p>
     * For example, if this value is set to {@code true}, then 0.999 would be store as 999/1000
     */
    private boolean approxRational = true;

    private DecimalExpr() {
        unaryOperators = new HashMap<>();
        binaryOperators = new HashMap<>();
        functions = new HashMap<>();

        values = new Values.GlobalValues();

        binaryOperators.putAll(BUILTIN_BINARY_OPS);
        unaryOperators.putAll(BUILTIN_UNARY_OPS);
        functions.putAll(BUILTIN_FUNCTIONS);

        values.setVariable("i", Complex.I);
    }

    private DecimalExpr(DecimalExpr parent) {
        unaryOperators = parent.unaryOperators;
        binaryOperators = parent.binaryOperators;
        functions = parent.functions;

        values = new Values.MacroValues(parent.values);

        approxRational = parent.approxRational;
        root = null;
    }

    public Map<String, BinaryOperator> getBinaryOperators() {
        return binaryOperators;
    }

    public Map<String, UnaryOperator> getUnaryOperators() {
        return unaryOperators;
    }

    public Map<String, AbstractFunction> getFunctions() {
        return functions;
    }

    public Set<String> getVarNames() {
        return values.varNames();
    }

    public Set<String> getMacroNames() {
        return values.macroNames();
    }

    public boolean isApproxRational() {
        return approxRational;
    }

    public void setVariable(String varName, Number value) {
        if (values.hasVariable(varName)) {
            values.setVariable(varName, value);
        } else {
            throw new BuildException("Unknown variable '" + varName + "'");
        }
    }

    public void setVariable(String varName, long longValue) {
        setVariable(varName, Rational.fromBigInt(BigInteger.valueOf(longValue)));
    }

    public void setMacro(String macroName, String macroContent) {
        if (values.hasMacro(macroName)) {
            Builder subBuilder = new Builder(this);
            subBuilder.expression(macroContent);
            DecimalExpr subExpr = subBuilder.build();
            Macro macro = new Macro(subExpr.root);

            values.setMacro(macroName, macro);
        } else {
            throw new BuildException("Unknown macro name '" + macroName + "'");
        }
    }

    public Number evaluate() {
        return root.eval(values);
    }

    public static class Builder {

        private final DecimalExpr decimalExpr;
        private String expression;
        private boolean showAst;
        private boolean showTokens;

        public Builder() {
            decimalExpr = new DecimalExpr();
        }

        private Builder(DecimalExpr parent) {
            decimalExpr = new DecimalExpr(parent);
        }

        public Builder expression(String expression) {
            this.expression = expression;
            return this;
        }

        public Builder variable(String var) {
            decimalExpr.values.setVariable(var, null);
            return this;
        }

        public Builder function(Function function) {
            decimalExpr.functions.put(function.name, function);
            return this;
        }

        public Builder showAst(boolean showAst) {
            this.showAst = showAst;
            return this;
        }

        public Builder showAst() {
            return showAst(true);
        }

        public Builder showTokens(boolean showTokens) {
            this.showTokens = showTokens;
            return this;
        }

        public Builder showTokens() {
            return showTokens(true);
        }

        public Builder operator(Operator operator) {
            if (operator instanceof UnaryOperator) {
                decimalExpr.unaryOperators.put(operator.symbol, (UnaryOperator) operator);
            } else if (operator instanceof BinaryOperator) {
                decimalExpr.binaryOperators.put(operator.symbol, (BinaryOperator) operator);
            } else {
                throw new BuildException("Unexpected operator type");
            }
            return this;
        }

        public Builder macro(String macroName) {
            decimalExpr.values.setMacro(macroName, null);
            return this;
        }

        /**
         * Whether to convert decimal input to rational.
         * <p>
         * For example, when set to {@code true}, the expression "2.5" will be evaluate to 5/2.
         * Otherwise, it is a decimal number 2.5
         * Set this to {@code true} also enables recurring number approximation.
         * For example, "0.{142857}" will be evaluate to 1/7
         *
         * @param approxRational whether to convert decimal input to rational
         * @return builder itself
         */
        public Builder approxRational(boolean approxRational) {
            decimalExpr.values.setApproxRational(approxRational);
            return this;
        }

        public DecimalExpr build() {
            Tokenizer tokenizer = new Tokenizer(expression);
            Element.CollectiveElement rootEle = tokenizer.tokenize();

            if (showTokens) System.out.println(rootEle);

            Parser parser = new Parser(rootEle, decimalExpr);
            decimalExpr.root = parser.parse();

            if (showAst) System.out.println(decimalExpr.root);

            return decimalExpr;
        }
    }
}
