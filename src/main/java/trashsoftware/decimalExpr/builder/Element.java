package trashsoftware.decimalExpr.builder;

import java.util.ArrayList;
import java.util.List;

public abstract class Element {

    public static final int BRACKET = 1;
    public static final int SQR_BRACKET = 2;
    public static final int BRACE = 3;

    final CollectiveElement parentElement;

    Element(CollectiveElement parentElement) {
        this.parentElement = parentElement;
    }

    static boolean isBracket(Element element) {
        return element instanceof CollectiveElement && ((CollectiveElement) element).type == BRACKET;
    }

    static boolean isSqrBracket(Element element) {
        return element instanceof CollectiveElement && ((CollectiveElement) element).type == SQR_BRACKET;
    }

    static boolean isBrace(Element element) {
        return element instanceof CollectiveElement && ((CollectiveElement) element).type == BRACE;
    }

    public static class AtomicElement extends Element {
        final Token token;

        AtomicElement(Token token, CollectiveElement parentElement) {
            super(parentElement);
            this.token = token;
        }

        @Override
        public String toString() {
            return "Atom{" + token + "}";
        }
    }

    public static class CollectiveElement extends Element {
        public final int type;
        final List<Element> children = new ArrayList<>();

        CollectiveElement(int type, CollectiveElement parentElement) {
            super(parentElement);
            this.type = type;
        }

        void add(Element element) {
            children.add(element);
        }

        Element get(int index) {
            return children.get(index);
        }

        int size() {
            return children.size();
        }

        @Override
        public String toString() {
            return "Collective{" + children + "}";
        }
    }
}
