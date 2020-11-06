package trashsoftware.decimalExpr.builder;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    /**
     * Static final field
     */
    public static final String[] ALLOWED_OPERATORS = {"+", "-", "*", "/", "\\", "%", "^", "$", "#", "@", "!", "&",
            "?", "<", ">", "|"};

    public static final String[] EXTRA_IDENTIFIERS = ALLOWED_OPERATORS;

    public static final String[] OTHERS = {"(", ")", "[", "]", "{", "}"};

//    public static final String IMAGINARY = "i";

    /**
     * Instant field
     */
    private final String text;

    public Tokenizer(String text) {
        this.text = text;
    }

    static Element.CollectiveElement makeTreeListRec(Element.CollectiveElement currentActive,
                                                     List<Token> tokenList,
                                                     int index) {
        Token tk = tokenList.get(index);
        if (tk instanceof Token.IdToken) {
            String symbol = ((Token.IdToken) tk).identifier;
            switch (symbol) {
                case "(":
                    return new Element.CollectiveElement(Element.BRACKET, currentActive);
                case ")":
                    if (Element.isBracket(currentActive)) {
                        currentActive.parentElement.add(currentActive);
                        return currentActive.parentElement;
                    } else {
                        throw new SyntaxError("')' must close a '(', not a '" + symbol + "'. ");
                    }
                case "[":
                    return new Element.CollectiveElement(Element.SQR_BRACKET, currentActive);
                case "]":
                    if (Element.isSqrBracket(currentActive)) {
                        currentActive.parentElement.add(currentActive);
                        return currentActive.parentElement;
                    } else {
                        throw new SyntaxError("']' must close a '[', not a '" + symbol + "'. ");
                    }
                case "{":
                    return new Element.CollectiveElement(Element.BRACE, currentActive);
                case "}":
                    if (Element.isBrace(currentActive)) {
                        currentActive.parentElement.add(currentActive);
                        return currentActive.parentElement;
                    } else {
                        throw new SyntaxError("'}' must close a '{', not a '" + symbol + "'. ");
                    }
                default:
                    currentActive.add(new Element.AtomicElement(tk, currentActive));
                    return currentActive;
            }
        } else {
            currentActive.add(new Element.AtomicElement(tk, currentActive));
            return currentActive;
        }
    }

    public Element.CollectiveElement tokenize() {
        List<Token> tokens = tokenizeToList();

        return treeRecursive(tokens);
    }

    private List<Token> tokenizeToList() {
        List<Token> result = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        CharTypeIdentifier cur = new CharTypeIdentifier('\0');
        CharTypeIdentifier last;
        int len = text.length();
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            last = cur;
            cur = new CharTypeIdentifier(c);
            if (!CharTypeIdentifier.concatenateAble(last, cur)) {
                addToken(result, builder.toString());
                builder.setLength(0);
            }
            builder.append(c);
        }
        if (builder.length() > 0) addToken(result, builder.toString());

        return result;
    }

    private void addToken(List<Token> result, String content) {
        if (StringTypes.isInteger(content)) {
            result.add(new Token.IntToken(content));
        } else if (StringTypes.isDecimal(content)) {
            result.add(new Token.DecimalToken(content));
        } else if (StringTypes.isIdentifier(content)) {
            result.add(new Token.IdToken(content));
        } else if (Utilities.arrayContains(EXTRA_IDENTIFIERS, content)) {
            result.add(new Token.IdToken(content));
        } else if (Utilities.arrayContains(OTHERS, content)) {
            result.add(new Token.IdToken(content));
        }
    }

    private Element.CollectiveElement treeRecursive(List<Token> tokens) {
        Element.CollectiveElement root = new Element.CollectiveElement(Element.BRACKET, null);
        Element.CollectiveElement currentActive = root;
        for (int i = 0; i < tokens.size(); ++i) {
            currentActive = makeTreeListRec(currentActive, tokens, i);
        }
        return root;
    }

    private static class CharTypeIdentifier {

        private static final int DIGIT = 1;
        private static final int LETTER = 2;
        private static final int L_BRACE = 3;
        private static final int R_BRACE = 4;
        private static final int L_BRACKET = 5;
        private static final int R_BRACKET = 6;
        private static final int L_SQR_BRACKET = 7;
        private static final int R_SQR_BRACKET = 8;
        private static final int EOL = 9;
        private static final int NEW_LINE = 10;
        private static final int GT = 11;
        private static final int LT = 12;
        private static final int EQ = 13;
        private static final int AND = 14;
        private static final int OR = 15;
        private static final int XOR = 16;
        private static final int DOT = 17;
        private static final int COMMA = 18;
        private static final int UNDERSCORE = 19;
        private static final int NOT = 20;
        private static final int PLUS = 21;
        private static final int MINUS = 22;
        private static final int OTHER_ARITHMETIC = 23;
        private static final int UNDEFINED = 0;

        private static final int[] SELF_CONCATENATE = {
                DIGIT, LETTER, GT, EQ, LT, AND, OR, UNDERSCORE, PLUS, MINUS, DOT
        };
        private static final int[][] CROSS_CONCATENATE = {
                {LETTER, UNDERSCORE},
                {UNDERSCORE, LETTER},
                {DIGIT, UNDERSCORE},
                {UNDERSCORE, DIGIT},
                {DIGIT, DOT},
                {DOT, DIGIT},
                {MINUS, GT},
                {LT, MINUS},
                {LETTER, DIGIT},
                {GT, EQ},
                {LT, EQ},
                {NOT, EQ},
                {PLUS, EQ},
                {MINUS, EQ},
                {OTHER_ARITHMETIC, EQ},
        };

        private final int type;

        CharTypeIdentifier(char ch) {
            this.type = identify(ch);
        }

        static boolean concatenateAble(CharTypeIdentifier left, CharTypeIdentifier right) {
            int leftType = left.type;
            int rightType = right.type;
            return (leftType == rightType && Utilities.arrayContains(SELF_CONCATENATE, leftType)) ||
                    Utilities.arrayContains2D(CROSS_CONCATENATE, new int[]{leftType, rightType});
        }

        private static int identify(char ch) {
            if (Character.isDigit(ch)) return DIGIT;
            else if (Character.isAlphabetic(ch)) return LETTER;

            switch (ch) {
                case '{':
                    return L_BRACE;
                case '}':
                    return R_BRACE;
                case '(':
                    return L_BRACKET;
                case ')':
                    return R_BRACKET;
                case '[':
                    return L_SQR_BRACKET;
                case ']':
                    return R_SQR_BRACKET;
                case ';':
                    return EOL;
                case '\n':
                    return NEW_LINE;
                case '>':
                    return GT;
                case '<':
                    return LT;
                case '=':
                    return EQ;
                case '&':
                    return AND;
                case '|':
                    return OR;
                case '^':
                    return XOR;
                case '.':
                    return DOT;
                case ',':
                    return COMMA;
                case '_':
                    return UNDERSCORE;
                case '!':
                    return NOT;
                case '+':
                    return PLUS;
                case '-':
                    return MINUS;
                case '*':
                case '/':
                case '%':
                    return OTHER_ARITHMETIC;
                default:
                    return UNDEFINED;
            }
        }
    }

    public static class StringTypes {

        private static boolean isInteger(String s) {
            for (char c : s.toCharArray()) {
                if (!(Character.isDigit(c) || c == '_')) return false;
            }
            return s.length() > 0 && s.charAt(0) != '_';
        }

        private static boolean isDecimal(String s) {
            int dotIndex = s.indexOf('.');
            if (dotIndex == -1) return false;
            if (dotIndex == 0) return StringTypes.isInteger(s.substring(1));  // situation ".123"
            String[] parts = s.split("\\.");
            if (parts.length == 2) {
                return StringTypes.isInteger(parts[0]) && StringTypes.isInteger(parts[1]);
            } else return false;
        }

        public static boolean isIdentifier(String s) {
            if (s.length() > 0) {
                char lead = s.charAt(0);
                if (!(Character.isAlphabetic(lead) || lead == '_')) return false;
                int len = s.length();
                for (int i = 1; i < len; ++i) {
                    char ch = s.charAt(i);
                    if (!(Character.isAlphabetic(ch) || Character.isDigit(ch) || ch == '_' || ch == '?'))
                        return false;
                }
                return true;
            }
            return false;
        }
    }
}
