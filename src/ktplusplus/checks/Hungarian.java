package ktplusplus.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.coding.VariableDeclarationUsageDistanceCheck;

import java.util.ArrayList;
import java.util.List;

public class Hungarian extends AbstractCheck {
    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.VARIABLE_DEF};
    }

    @Override
    public int[] getAcceptableTokens() {
        return new int[0];
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[0];
    }

    private List<String> getTypeNames(DetailAST type) {
        List<String> result = new ArrayList<>();
        result.add(type.getFirstChild().getText());

        if (type.getChildCount() <= 1) {
            return result;
        }

        if (type.getFirstChild().getNextSibling().getType() != TokenTypes.TYPE_ARGUMENTS) {
            return null; // type with unknown children
        }

        DetailAST subtype = type.getFirstChild().getNextSibling().getFirstChild();
        while (subtype.getNextSibling() != null) {
            if (subtype.getType() == TokenTypes.TYPE_ARGUMENT) {
                result.addAll(getTypeNames(subtype));
            }
            subtype = subtype.getNextSibling();
        }

        return result;
    }

    @Override
    public void visitToken(DetailAST ast) {
        List<String> types = getTypeNames(ast.findFirstToken(TokenTypes.TYPE));
        if (types == null) {
            System.err.println("unable to process types");
        }

        String identifier = ast.findFirstToken(TokenTypes.IDENT).getText();

        for (String type : types) {
            if (identifier.contains(type)) {
                this.log(ast, "hungarian", identifier, type);
            }
        }
    }
}
