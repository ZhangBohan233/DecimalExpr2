package trashsoftware.decimalExpr.expression;

import trashsoftware.decimalExpr.numbers.Number;

public abstract class Function extends AbstractFunction {

    public Function(String name, int minArgCount, int maxArgCount) {
        super(name, minArgCount, maxArgCount);
    }

    public Function(String name, int argCount) {
        this(name, argCount, argCount);
    }

    public Number eval(Number... arguments) {
        if (arguments.length > maxArgCount || arguments.length < minArgCount) {
            throw new IllegalArgumentException(String.format("Function '%s' expects %s arguments, got %d.",
                    name,
                    (maxArgCount == minArgCount ?
                            String.valueOf(maxArgCount) : String.format("%d to %d", minArgCount, maxArgCount)),
                    arguments.length));
        }
        return evaluate(arguments);
    }

    /**
     * Evaluates the function.
     * <p>
     * Number of arguments is already checked.
     *
     * @param arguments arguments of this function call, number of arguments already checked.
     * @return the return value of this function
     */
    protected abstract Number evaluate(Number... arguments);
}
