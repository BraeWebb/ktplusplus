package ktplusplus.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import ktplusplus.util.FieldFrame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HashCodeImplementation extends FieldVisitor {
    private Map<String, Integer> fieldUsageCount;
    private Set<String> superCalledIn;

    private int requiredUsages = 2;

    public void setRequiredUsages(int requiredUsages) {
        this.requiredUsages = requiredUsages;
    }

    @Override
    public int[] getTokens() {
        return new int[0];
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        fieldUsageCount = new HashMap<>();
        superCalledIn = new HashSet<>();
    }

    @Override
    public void visitReference(DetailAST ast) {
        if (!frame.currentScopeID().endsWith("hashCode")) {
            return;
        }

        FieldFrame.FrameEntry field = frame.lookup(ast.getText());
        if (field != null && field.getScope() == FieldFrame.Scope.CLASS) {
            fieldUsageCount.put(frame.currentScopeID(),
                    fieldUsageCount.getOrDefault(frame.currentScopeID(), 0) + 1);
        }
    }

    @Override
    public void visitField(DetailAST ast) {
        // do nothing
    }

    @Override
    public void visitMethodCall(DetailAST ast) {
        if (!frame.currentScopeID().endsWith("hashCode")) {
            return;
        }

        if (ast.getText().endsWith("hashCode")) {
            superCalledIn.add(frame.currentScopeID());
        }
    }

    @Override
    public void leaveToken(DetailAST ast) {
        if (ast.getType() != TokenTypes.METHOD_DEF) {
            super.leaveToken(ast);
            return;
        }

        if (!frame.currentScopeID().endsWith("hashCode")) {
            super.leaveToken(ast);
            return;
        }

        int usages = fieldUsageCount.getOrDefault(frame.currentScopeID(), 0);
        boolean superCalled = superCalledIn.contains(frame.currentScopeID());
        if (usages < requiredUsages && !superCalled) {
            log(ast, "hashcode.complexity", frame.currentScopeID(),
                    requiredUsages, usages);
        }
        super.leaveToken(ast);
    }
}
