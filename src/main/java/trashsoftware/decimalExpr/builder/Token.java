package trashsoftware.decimalExpr.builder;

public abstract class Token {

    static class IdToken extends Token {
        public final String identifier;

        IdToken(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public String toString() {
            return "IdToken{" + identifier + "}";
        }
    }

    static class IntToken extends Token {
        public final String literal;

        IntToken(String literal) {
            this.literal = literal;
        }

        @Override
        public String toString() {
            return "Int{" + literal + "}";
        }
    }

    static class DecimalToken extends Token {
        public final String literal;

        DecimalToken(String literal) {
            this.literal = literal;
        }

        @Override
        public String toString() {
            return "Dec{" + literal + "}";
        }
    }
}
