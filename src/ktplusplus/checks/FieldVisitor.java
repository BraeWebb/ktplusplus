package ktplusplus.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import ktplusplus.util.FieldFrame;
import ktplusplus.util.FieldFrame.*;

public abstract class FieldVisitor extends AbstractCheck {
    protected final FieldFrame frame = new FieldFrame();

    @Override
    public int[] getDefaultTokens() {
        int[] defaultTokens = new int[] {
                TokenTypes.METHOD_DEF,
                TokenTypes.CLASS_DEF,
                TokenTypes.INTERFACE_DEF,
                TokenTypes.ANNOTATION_DEF,
                TokenTypes.CTOR_DEF,

                TokenTypes.VARIABLE_DEF,
                TokenTypes.PARAMETER_DEF,
                TokenTypes.PATTERN_VARIABLE_DEF,
                TokenTypes.RECORD_COMPONENT_DEF,

                TokenTypes.METHOD_CALL,

                TokenTypes.IDENT,
        };
        int[] additional = getTokens();

        int aLen = defaultTokens.length;
        int bLen = additional.length;
        int[] result = new int[aLen + bLen];

        System.arraycopy(defaultTokens, 0, result, 0, aLen);
        System.arraycopy(additional, 0, result, aLen, bLen);

        return result;
    }

    public abstract int[] getTokens();

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }

    @Override
    public void visitToken(DetailAST ast) {
        String ident = null;
        if (ast.findFirstToken(TokenTypes.IDENT) != null) {
            ident = ast.findFirstToken(TokenTypes.IDENT).getText();
        }
        switch (ast.getType()) {
            case TokenTypes.CTOR_DEF:
            case TokenTypes.METHOD_DEF:
                frame.push(Scope.METHOD, ident);
                break;
            case TokenTypes.CLASS_DEF:
                frame.push(Scope.CLASS, ident);
                break;
            case TokenTypes.INTERFACE_DEF:
                frame.push(Scope.INTERFACE, ident);
                break;
            case TokenTypes.ANNOTATION_DEF:
                frame.push(Scope.ANNOTATION, ident);
                break;

            case TokenTypes.VARIABLE_DEF:
            case TokenTypes.PARAMETER_DEF:
            case TokenTypes.PATTERN_VARIABLE_DEF:
            case TokenTypes.RECORD_COMPONENT_DEF:
                if (frame.currentScope() != Scope.METHOD) {
                    visitField(ast);
                }
                frame.addField(ident);
                break;

            case TokenTypes.METHOD_CALL:
                DetailAST method = ast.getFirstChild().getLastChild();
                if (method == null) {
                    return;
                }
                visitMethodCall(method);
                break;

            case TokenTypes.IDENT:
                visitIdent(ast);
                break;
        }
    }

    @Override
    public void leaveToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.CTOR_DEF:
            case TokenTypes.METHOD_DEF:
            case TokenTypes.CLASS_DEF:
            case TokenTypes.INTERFACE_DEF:
            case TokenTypes.ANNOTATION_DEF:
                frame.pop();
                break;
        }
    }

    public void visitIdent(DetailAST ast) {
        if (frame.currentScope() != FieldFrame.Scope.METHOD) {
            return;
        }

        DetailAST parent = ast.getParent();

        switch (parent.getType()) {
            case TokenTypes.METHOD_CALL:
            case TokenTypes.METHOD_DEF:
            case TokenTypes.METHOD_REF:
            case TokenTypes.LITERAL_NEW:
            case TokenTypes.TYPE:
            case TokenTypes.ANNOTATION:
            case TokenTypes.VARIABLE_DEF:
            case TokenTypes.PARAMETER_DEF:
            case TokenTypes.LITERAL_THROWS:
            case TokenTypes.TYPE_ARGUMENT:
                return;
        }
        if (parent.getParent() != null) {
            switch (parent.getParent().getType()) {
//                case TokenTypes.METHOD_CALL:
                case TokenTypes.TYPE:
                    return;
            }
        }


        visitReference(ast);
    }

    public void visitMethodCall(DetailAST ast) {}
    public abstract void visitReference(DetailAST ast);
    public abstract void visitField(DetailAST ast);
}
