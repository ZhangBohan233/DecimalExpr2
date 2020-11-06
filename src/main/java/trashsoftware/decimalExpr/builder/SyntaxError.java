package trashsoftware.decimalExpr.builder;

public class SyntaxError extends RuntimeException {

    public SyntaxError() {

    }

    public SyntaxError(String msg) {
        super(msg);
    }
}
