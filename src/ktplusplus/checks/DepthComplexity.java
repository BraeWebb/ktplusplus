package ktplusplus.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class DepthComplexity extends AbstractCheck {
    private String currentMethod;
    private boolean methodChecked = false;
    private int depth = 0;

    private int max = 1;

    public void setMax(int max) {
        this.max = max;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
                TokenTypes.LITERAL_IF,
                TokenTypes.LITERAL_FOR,
                TokenTypes.LITERAL_TRY,

                TokenTypes.METHOD_DEF,
        };
    }

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        depth = 0;
    }

    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.LITERAL_IF:
            case TokenTypes.LITERAL_FOR:
            case TokenTypes.LITERAL_TRY:
                visitNested(ast);
                break;
            case TokenTypes.METHOD_DEF:
                visitMethod(ast);
                break;
        }
    }

    @Override
    public void leaveToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.LITERAL_IF:
            case TokenTypes.LITERAL_FOR:
            case TokenTypes.LITERAL_TRY:
                leaveNested(ast);
                break;
            case TokenTypes.METHOD_DEF:
                leaveMethod(ast);
                break;
        }
    }

    private void visitNested(DetailAST ast) {
        if (depth > max && !methodChecked) {
            log(ast, "depth.complexity", depth, max, currentMethod);
            methodChecked = true;
        }
        ++depth;
    }

    private void leaveNested(DetailAST ast) {
        --depth;
    }

    private void visitMethod(DetailAST ast) {
        depth = 0;
        currentMethod = ast.findFirstToken(TokenTypes.IDENT).getText();
    }

    private void leaveMethod(DetailAST ast) {
        currentMethod = null;
        methodChecked = false;
    }
}
