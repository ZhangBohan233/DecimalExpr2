package trashsoftware.decimalExpr.builder;

import trashsoftware.decimalExpr.BuildException;
import trashsoftware.decimalExpr.DecimalExpr;
import trashsoftware.decimalExpr.expression.*;
import trashsoftware.decimalExpr.util.Utilities;

public class Parser {

    private final Element.CollectiveElement root;
    private final DecimalExpr decimalExpr;

    public Parser(Element.CollectiveElement root, DecimalExpr decimalExpr) {
        this.root = root;
        this.decimalExpr = decimalExpr;
    }

    public Node.BlockStmt parse() {
        AstBuilder builder = parseSomeBlock(root);
        builder.finishLine();
        return builder.getRoot();
    }

    private AstBuilder parseSomeBlock(Element.CollectiveElement collectiveElement) {
        AstBuilder builder = new AstBuilder();
        int i = 0;
        while (i < collectiveElement.size()) {
            i = parseOne(collectiveElement, i, builder);
        }
        return builder;
    }

    private Node.Expression parseOnPart(Element.CollectiveElement collectiveElement) {
        AstBuilder builder = parseSomeBlock(collectiveElement);
        builder.finishLine();
        Node.BlockStmt root = builder.getRoot();
        if (root.size() != 1)
            throw new SyntaxError("Expected 1 part in line, got " + root.size() + ": " + root + ". ");
        return (Node.Expression) root.get(0);
    }

    private Node.BlockStmt parseMultiParts(Element.CollectiveElement collectiveElement) {
        AstBuilder builder = parseSomeBlock(collectiveElement);
        builder.finishLine();
        return builder.getRoot();
    }

    private int parseOne(Element.CollectiveElement parent, int index, AstBuilder astBuilder) {
        Element ele = parent.get(index++);
        if (ele instanceof Element.AtomicElement) {
            Token token = ((Element.AtomicElement) ele).token;
            if (token instanceof Token.IdToken) {
                String identifier = ((Token.IdToken) token).identifier;
                if (Utilities.arrayContains(Tokenizer.ALLOWED_OPERATORS, identifier)) {
                    proceedOperator(identifier, index - 1, astBuilder);
                } else if (decimalExpr.getVarNames().contains(identifier)) {
                    astBuilder.addNode(new Node.VarNameNode(identifier));
                } else if (decimalExpr.getMacroNames().contains(identifier)) {
                    astBuilder.addNode(new Node.MacroNameNode(identifier));
                } else if (decimalExpr.getFunctions().containsKey(identifier)) {
                    //
                } else if (identifier.equals(",")) {
                    astBuilder.finishLine();
                } else {
                    astBuilder.addNode(new Node.UndefinedNameNode(identifier));
                }
            } else if (token instanceof Token.IntToken) {
                astBuilder.addNode(new Node.IntNode(((Token.IntToken) token).literal));
            } else if (token instanceof Token.DecimalToken) {
                astBuilder.addNode(new Node.DecimalNode(((Token.DecimalToken) token).literal));
            } else {
                throw new BuildException("Unexpected token " + token + ".");
            }
            return index;
        } else if (ele instanceof Element.CollectiveElement) {
            Element.CollectiveElement ce = (Element.CollectiveElement) ele;
            if (ce.type == Element.BRACKET) {
                if (index > 1) {
                    Element probCall = parent.get(index - 2);
                    if (probCall instanceof Element.AtomicElement &&
                            ((Element.AtomicElement) probCall).token instanceof Token.IdToken) {
                        String identifier = ((Token.IdToken) ((Element.AtomicElement) probCall).token).identifier;
                        AbstractFunction function = decimalExpr.getFunctions().get(identifier);
                        if (function != null) {
                            Node.BlockStmt args = parseMultiParts(ce);
                            if (function instanceof Function)
                                astBuilder.addNode(new Node.FunctionCall((Function) function, args));
                            else if (function instanceof MacroFunction)
                                astBuilder.addNode(new Node.MacroFunctionCall((MacroFunction) function, args));
                            else
                                throw new BuildException("Unexpected function type");
                            return index;
                        }
                    }
                }
                // parenthesis
                astBuilder.addNode(parseOnPart(ce));
                return index;
            }
        }
        throw new BuildException("Unexpected token element " + ele);
    }

    private void proceedOperator(String operator, int operatorIndex, AstBuilder builder) {
        UnaryOperator uo = decimalExpr.getUnaryOperators().get(operator);
        if (uo != null) {
            // index at the other side of the operator, relative to its value.
            int adjTokenIndex = uo.operatorAtLeft ?
                    operatorIndex - 1 : operatorIndex + 1;
            if (isUnary(adjTokenIndex, uo.operatorAtLeft)) {
                builder.addNode(new Node.UnaryOperatorNode(uo));
                return;
            }
        }
        BinaryOperator bo = decimalExpr.getBinaryOperators().get(operator);
        if (bo != null) {
            builder.addNode(new Node.BinaryOperatorNode(bo));
        } else {
            throw new BuildException("Unsolved operator '" + operator + "'");
        }
    }

    private boolean isUnary(int adjacentTokenIndex, boolean operatorAtLeft) {
        if (adjacentTokenIndex < 0 || adjacentTokenIndex >= root.size()) {
            return true;
        } else {
            Element element = root.get(adjacentTokenIndex);
            if (element instanceof Element.AtomicElement) {
                Token token = ((Element.AtomicElement) element).token;
                if (token instanceof Token.IntToken || token instanceof Token.DecimalToken) return false;
                else if (token instanceof Token.IdToken) {
                    String symbol = ((Token.IdToken) token).identifier;
                    return decimalExpr.getBinaryOperators().containsKey(symbol)
                            || symbol.equals(",");
                } else {
                    throw new SyntaxError("Unexpected token");
                }
            } else if (element instanceof Element.CollectiveElement) {
                return !Element.isBracket(element);
            } else {
                throw new SyntaxError("Unexpected token");
            }
        }
    }
}
