package ktplusplus.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.*;

public class MemberUsage extends FieldVisitor {
    private HashMap<String, Set<String>> usedInMethods;
    private HashMap<String, DetailAST> fieldDetails;

    @Override
    public int[] getTokens() {
        return new int[] {
                TokenTypes.CLASS_DEF,
        };
    }

    @Override
    public void visitToken(DetailAST ast) {
        if (ast.getType() == TokenTypes.CLASS_DEF) {
            usedInMethods = new HashMap<>();
            fieldDetails = new HashMap<>();
        }
        super.visitToken(ast);
    }

    @Override
    public void leaveToken(DetailAST ast) {
        if (ast.getType() != TokenTypes.CLASS_DEF) {
            super.leaveToken(ast);
            return;
        }

        for (Map.Entry<String, Set<String>> entry : usedInMethods.entrySet()) {
            if (entry.getValue().size() == 0) {
                log(fieldDetails.get(entry.getKey()), "member.unused", entry.getKey());
            }
            if (entry.getValue().size() == 1) {
                log(fieldDetails.get(entry.getKey()), "member.local",
                        entry.getKey(), entry.getValue().stream().findAny().get());
            }
        }

        super.leaveToken(ast);
    }

    @Override
    public void visitReference(DetailAST ast) {
        if (!usedInMethods.containsKey(ast.getText())) {
            return;
        }

        usedInMethods.get(ast.getText()).add(frame.currentScopeID());
    }

    @Override
    public void visitField(DetailAST ast) {
        String name = ast.findFirstToken(TokenTypes.IDENT).getText();
        usedInMethods.put(name, new HashSet<>());
        fieldDetails.put(name, ast);
    }
}
