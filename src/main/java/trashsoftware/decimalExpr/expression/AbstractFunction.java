package trashsoftware.decimalExpr.expression;

public abstract class AbstractFunction {

    public final String name;
    protected final int minArgCount;
    protected final int maxArgCount;

    public AbstractFunction(String name, int minArgCount, int maxArgCount) {
        this.name = name;
        this.minArgCount = minArgCount;
        this.maxArgCount = maxArgCount;
    }
}
