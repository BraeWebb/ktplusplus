package ktplusplus.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class MagicNumber extends AbstractCheck {

    @Override
    public int[] getDefaultTokens() {
        return new int[]{TokenTypes.NUM_INT, TokenTypes.NUM_FLOAT,
                TokenTypes.NUM_LONG, TokenTypes.NUM_DOUBLE};
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
    public void visitToken(DetailAST ast) {
        // don't care unless the int is used within a method (i.e. actual code)
        boolean isInMethod = false;
        DetailAST parent = ast;
        while (parent != null) {
            if (parent.getType() == TokenTypes.METHOD_DEF
                    || parent.getType() == TokenTypes.CTOR_DEF) {
                isInMethod = true;
            }
            parent = parent.getParent();
        }
        if (!isInMethod) {
            return;
        }

        double value;
        try {
            value = Double.parseDouble(ast.getText());
        } catch (NumberFormatException e) {
            System.err.println(ast.getText());
            System.err.println("dunno");
            return;
        }

        // constants between -1 and 2 are fairly forgivable
        // TODO: Make this a setting
        if (value >= -1 && value <= 2) {
            return;
        }

        this.log(ast, "magic", value);
    }
}
