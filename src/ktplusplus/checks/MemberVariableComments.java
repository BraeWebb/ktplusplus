package ktplusplus.checks;

import com.puppycrawl.tools.checkstyle.api.*;

import java.util.List;

public class MemberVariableComments extends AbstractCheck {
    // todo: make checkstyle config
    private static final int GROUP_LIMIT = 6;

    @Override
    public int[] getDefaultTokens() {
        return new int[]{
                TokenTypes.VARIABLE_DEF,
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
    public boolean isCommentNodesRequired() {
        return true;
    }

    private void debugNode(DetailAST ast, int indent) {
        String i = new String(new char[indent]).replace("\0", "-");
        System.out.println(i + ast.toString());

        DetailAST nextChild = ast.getFirstChild();
        if (nextChild != null) {
            debugNode(nextChild, indent + 2);
        }

        DetailAST sibling = ast.getNextSibling();
        if (sibling != null) {
            debugNode(sibling, indent);
        }
    }

    private TextBlock getSingleLineComment(FileContents contents, int lineNoBefore) {
        int lineNo = lineNoBefore - 2;

        // skip blank lines
        while (lineNo > 0 && (contents.lineIsBlank(lineNo))) {
            lineNo--;
        }

        return contents.getSingleLineComments().get(lineNo);
    }

    private boolean hasBlockComment(FileContents contents, int lineNoBefore) {
        int lineNo = lineNoBefore - 2;

        // skip blank lines
        while (lineNo > 0 && (contents.lineIsBlank(lineNo)
                || contents.lineIsComment(lineNo))) {
            lineNo--;
        }

        return contents.hasIntersectionWithComment(lineNo, 0, lineNo, 0);
    }

    private boolean hasComment(FileContents contents, DetailAST ast) {
        if (contents.getJavadocBefore(ast.getLineNo()) != null) {
            return true;
        }
        if (hasBlockComment(contents, ast.getLineNo())) {
            return true;
        }
        return getSingleLineComment(contents, ast.getLineNo()) != null;
    }

    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.VARIABLE_DEF:
                visitMemberVariable(ast);
                break;
            case TokenTypes.METHOD_DEF:
                visitMethod(ast);
                break;
        }


    }

    public void visitMemberVariable(DetailAST ast) {
        // don't look at local variable definitions, only member
        if (ast.getParent().getType() != TokenTypes.OBJBLOCK) {
            return;
        }

        FileContents contents = getFileContents();
        if (hasComment(contents, ast)) {
            return;
        }

        // variables grouped together are okay, unless it exists GROUP_LIMIT
        int groupSize = 1;
        DetailAST previous = ast.getPreviousSibling();
        while (previous != null && previous.getType() == TokenTypes.VARIABLE_DEF) {
            if (hasComment(contents, previous)) {
                return;
            }

            groupSize++;
            if (groupSize > GROUP_LIMIT) {
                log(ast, "Too many member variables");
            }

            previous = previous.getPreviousSibling();
        }

        String ident = ast.findFirstToken(TokenTypes.IDENT).getText();
        log(ast, "comment.missing.member", ident);
    }

    public void visitMethod(DetailAST ast) {
        FileContents contents = getFileContents();
        if (hasComment(contents, ast)) {
            return;
        }

        if (ast.findFirstToken(TokenTypes.MODIFIERS).findFirstToken(TokenTypes.LITERAL_PRIVATE) == null) {
            return;
        }

        String ident = ast.findFirstToken(TokenTypes.IDENT).getText();
        log(ast, "comment.missing.method", ident);
    }
}
