package trashsoftware.decimalExpr.expression;

import trashsoftware.decimalExpr.builder.Values;
import trashsoftware.decimalExpr.numbers.Number;

public abstract class MacroFunction extends AbstractFunction {

    protected Macro macro;

    public MacroFunction(String name, int minArgCount, int maxArgCount) {
        super(name, minArgCount, maxArgCount);
    }

    public MacroFunction(String name, int argCount) {
        this(name, argCount, argCount);
    }

    public Number eval(String invariant, Macro macro, Number... arguments) {
        if (arguments.length > maxArgCount || arguments.length < minArgCount) {
            throw new IllegalArgumentException(String.format("Function '%s' expects %s arguments, got %d.",
                    name,
                    (maxArgCount == minArgCount ?
                            String.valueOf(maxArgCount) : String.format("%d to %d", minArgCount, maxArgCount)),
                    arguments.length));
        }
        this.macro = macro;
        return evaluate(invariant, macro, arguments);
    }

    /**
     * Evaluates the function.
     * <p>
     * Number of arguments is already checked.
     *
     * @param invariant loop invariant
     * @param macro     macro expression
     * @param arguments arguments of this function call, number of arguments already checked.
     * @return the return value of this function
     */
    protected abstract Number evaluate(String invariant, Macro macro, Number... arguments);

    /**
     * Sets the variable to a value.
     * <p>
     * This method is usually used to set the invariant.
     *
     * @param varName name of variable
     * @param value   value
     */
    protected void setValue(String varName, Number value) {
        macro.getMacroValues().setVariable(varName, value);
    }

    /**
     * Evaluate macro, using values that set by {@code setValue()}
     *
     * @return the evaluation result
     */
    protected Number evalMacro() {
        return macro.eval();
    }

//    protected Values getMacroValues() {
//        return subValues;
//    }
}
