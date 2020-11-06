package trashsoftware.decimalExpr.expression;

import trashsoftware.decimalExpr.builder.Node;
import trashsoftware.decimalExpr.numbers.Number;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Values {

    private final Map<String, Number> variables = new HashMap<>();
    private final Map<String, Macro> macros = new HashMap<>();

    public Number getVariable(String name) {
        Number n = variables.get(name);
        if (n == null) throw new UnknownSymbolException("Variable '" + name + "' not set.");
        return n;
    }

    public Macro getMacro(String name) {
        Macro macro = macros.get(name);
        if (macro == null) throw new UnknownSymbolException("Macro '" + name + "' not set.");
        return macro;
    }

    public void putVariable(String name, Number value) {
        variables.put(name, value);
    }

    public void putMacro(String name, Macro macro) {
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
}
