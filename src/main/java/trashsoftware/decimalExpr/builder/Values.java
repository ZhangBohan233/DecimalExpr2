package trashsoftware.decimalExpr.builder;

import trashsoftware.decimalExpr.expression.Macro;
import trashsoftware.decimalExpr.expression.UnknownSymbolException;
import trashsoftware.decimalExpr.numbers.Number;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Values {

    private final Map<String, Number> variables = new HashMap<>();
    private final Map<String, Macro> macros = new HashMap<>();
    private final Values outer;

    public Values(Values outer) {
        this.outer = outer;
    }

    public Values() {
        this(null);
    }

    public Number getVariable(String name) {
        Number n = variables.get(name);
        if (n == null) {
            if (outer == null)
                throw new UnknownSymbolException("Variable '" + name + "' not set.");
            else return outer.getVariable(name);
        }
        return n;
    }

    public Macro getMacro(String name) {
        Macro macro = macros.get(name);
        if (macro == null)
            if (outer == null)
                throw new UnknownSymbolException("Macro '" + name + "' not set.");
            else return outer.getMacro(name);
        return macro;
    }

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
        return variables.containsKey(varName);
    }

    public boolean hasMacro(String macroName) {
        return macros.containsKey(macroName);
    }

    @Override
    public String toString() {
        return "Values{" +
                "variables=" + variables +
                ", macros=" + macros +
                ", outer=" + outer +
                '}';
    }
}
