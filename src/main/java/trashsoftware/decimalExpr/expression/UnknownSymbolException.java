package trashsoftware.decimalExpr.expression;

public class UnknownSymbolException extends RuntimeException {

    public UnknownSymbolException() {

    }

    public UnknownSymbolException(String msg) {
        super(msg);
    }
}
