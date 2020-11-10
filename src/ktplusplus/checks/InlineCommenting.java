package ktplusplus.checks;

import com.puppycrawl.tools.checkstyle.api.*;

import java.util.Map;

public class InlineCommenting extends AbstractCheck {
    private static final int GAP_DEFAULT = 15;

    private int gap = GAP_DEFAULT;

    public void setGap(int gap) {
        this.gap = gap;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
                TokenTypes.METHOD_DEF,
                TokenTypes.CTOR_DEF,
                TokenTypes.COMPACT_CTOR_DEF,
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
    public boolean isCommentNodesRequired() {
        return true;
    }

    @Override
    public void visitToken(DetailAST ast) {
        final DetailAST openingBrace = ast.findFirstToken(TokenTypes.SLIST);
        final String name = ast.findFirstToken(TokenTypes.IDENT).getText();
        if (openingBrace == null) {
            return;
        }

        final DetailAST closingBrace = openingBrace.findFirstToken(TokenTypes.RCURLY);
        if (closingBrace == null) {
            return;
        }

        Map<Integer, TextBlock> comments = getFileContents().getSingleLineComments();

        int startLine = openingBrace.getLineNo();
        int endLine = closingBrace.getLineNo();

        int gap = 0;
        for (int i = startLine; i < endLine; i++) {
            if (comments.get(i) == null) {
                gap++;
            } else {
                gap = 0;
            }

            if (gap >= this.gap) {
                this.log(i, "comment.missing.inline", name);
                break;
            }
        }
    }
}
