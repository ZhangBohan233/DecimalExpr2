package trashsoftware.decimalExpr.numbers;

import trashsoftware.decimalExpr.BuildException;

public enum NaN implements Number {
    NaN;

    @Override
    public boolean exact() {
        throw new BuildException("Undefined number or variable");
    }

    @Override
    public Number add(Number right) {
        throw new BuildException("Undefined number or variable");
    }

    @Override
    public Number sub(Number right) {
        throw new BuildException("Undefined number or variable");
    }

    @Override
    public Number mul(Number right) {
        throw new BuildException("Undefined number or variable");
    }

    @Override
    public Number div(Number right) {
        throw new BuildException("Undefined number or variable");
    }

    @Override
    public Number pow(Number exp) {
        throw new BuildException("Undefined number or variable");
    }

    @Override
    public Number pow(int exp) {
        throw new BuildException("Undefined number or variable");
    }

    @Override
    public Number sqrt() {
        throw new BuildException("Undefined number or variable");
    }

    @Override
    public String toDecimalString() {
        return toString();
    }

    @Override
    public String toString() {
        return "NaN";
    }
}
