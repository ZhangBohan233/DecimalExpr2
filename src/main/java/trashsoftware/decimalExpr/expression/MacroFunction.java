package trashsoftware.decimalExpr.expression;

import trashsoftware.decimalExpr.builder.Values;
import trashsoftware.decimalExpr.numbers.Number;

public abstract class MacroFunction extends AbstractFunction {

    protected Values subValues;

    public MacroFunction(String name, int minArgCount, int maxArgCount) {
        super(name, minArgCount, maxArgCount);
    }

    public MacroFunction(String name, int argCount) {
        this(name, argCount, argCount);
    }

    public Number eval(Values outerEnv, String invariant, Macro macro, Number... arguments) {
        if (arguments.length > maxArgCount || arguments.length < minArgCount) {
            throw new IllegalArgumentException(String.format("Function '%s' expects %s arguments, got %d.",
                    name,
                    (maxArgCount == minArgCount ?
                            String.valueOf(maxArgCount) : String.format("%d to %d", minArgCount, maxArgCount)),
                    arguments.length));
        }
        subValues = new Values.MacroValues(outerEnv);
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
        subValues.setVariable(varName, value);
    }

    /**
     * Evaluate macro, using values that set by {@code setValue()}
     *
     * @param macro the macro to be evaluated
     * @return the evaluation result
     */
    protected Number evalMacro(Macro macro) {
        return macro.eval(subValues);
    }

//    protected Values getMacroValues() {
//        return subValues;
//    }
}
