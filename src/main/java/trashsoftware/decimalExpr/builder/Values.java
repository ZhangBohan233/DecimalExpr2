package trashsoftware.decimalExpr.builder;

import trashsoftware.decimalExpr.BuildException;
import trashsoftware.decimalExpr.expression.Macro;
import trashsoftware.decimalExpr.expression.UnknownSymbolException;
import trashsoftware.decimalExpr.numbers.Number;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Values {

    protected final Map<String, Number> variables = new HashMap<>();
    protected final Map<String, Macro> macros = new HashMap<>();

    public Values() {
    }

    public Number getVariable(String name) {
        Number value = innerGetVariable(name);
        if (value == null) throw new UnknownSymbolException("Variable '" + name + "' not set.");
        return value;
    }

    public Macro getMacro(String name) {
        Macro macro = innerGetMacro(name);
        if (macro == null) throw new UnknownSymbolException("Macro '" + name + "' not set.");
        return macro;
    }

//    public Macro getMacro(String name) {
//        Macro macro = macros.get(name);
//        if (macro == null)
//            if (outer == null)
//                throw new UnknownSymbolException("Macro '" + name + "' not set.");
//            else return outer.getMacro(name);
//        return macro;
//    }

    public void setVariable(String name, Number value) {
        variables.put(name, value);
    }

    public void setMacro(String name, Macro macro) {
        macros.put(name, macro);
    }

    public Set<String> varNames() {
        return variables.keySet();
    }

    public Set<String> macroNames() {
        return macros.keySet();
    }

    public boolean hasVariable(String varName) {
        return innerGetVariable(varName) != null;
    }

//    public boolean hasLocalVariable(String varName) {
//        return variables.containsKey(varName);
//    }

    public boolean hasMacro(String macroName) {
        return innerGetMacro(macroName) != null;
    }

    protected abstract Macro innerGetMacro(String name);

    protected abstract Number innerGetVariable(String name);

    public abstract boolean isApproxRational();

    public abstract void setApproxRational(boolean approxRational);

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "variables=" + variables +
                ", macros=" + macros +
                '}';
    }

    public static class GlobalValues extends Values {
        private boolean approxRational = true;

        @Override
        protected Macro innerGetMacro(String name) {
            return macros.get(name);
        }

        @Override
        protected Number innerGetVariable(String name) {
            return variables.get(name);
        }

        @Override
        public boolean isApproxRational() {
            return approxRational;
        }

        @Override
        public void setApproxRational(boolean approxRational) {
            this.approxRational = approxRational;
        }
    }

    public static class MacroValues extends Values {
        private final Values outer;

        public MacroValues(Values outer) {
            this.outer = outer;
        }

        @Override
        protected Macro innerGetMacro(String name) {
            Macro local = macros.get(name);
            if (local == null) return outer.innerGetMacro(name);
            return local;
        }

        @Override
        protected Number innerGetVariable(String name) {
            Number local = variables.get(name);
            if (local == null) return outer.innerGetVariable(name);
            return local;
        }

        @Override
        public boolean isApproxRational() {
            return outer.isApproxRational();
        }

        @Override
        public void setApproxRational(boolean approxRational) {
            throw new BuildException("Cannot set global pref from macro.");
        }
    }
}
