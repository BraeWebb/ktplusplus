package ktplusplus.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import ktplusplus.util.FieldFrame;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class MagicNumber extends FieldVisitor {

    private Set<Double> ignore = new HashSet<>();
    private Set<String> ignoredMethods = new HashSet<>();

    public MagicNumber() {
        super();
        ignore.add((double) -1);
        ignore.add((double) 0);
        ignore.add((double) 1);
        ignore.add((double) 2);
    }

    public void setIgnore(double[] ignore) {
        this.ignore = DoubleStream.of(ignore).boxed().collect(Collectors.toSet());
    }

    public void setIgnoredMethods(String[] ignore) {
        this.ignoredMethods = new HashSet<>(Arrays.asList(ignore));
    }

    @Override
    public int[] getTokens() {
        return new int[]{TokenTypes.NUM_INT, TokenTypes.NUM_FLOAT,
                TokenTypes.NUM_LONG, TokenTypes.NUM_DOUBLE};
    }

    @Override
    public void visitToken(DetailAST ast) {
        super.visitToken(ast);
        if (ast.getType() == TokenTypes.NUM_INT
                || ast.getType() == TokenTypes.NUM_FLOAT
                || ast.getType() == TokenTypes.NUM_LONG
                || ast.getType() == TokenTypes.NUM_DOUBLE) {

        } else {
            return;
        }
        // don't care unless the int is used within a method (i.e. actual code)
        if (frame.currentScope() != FieldFrame.Scope.METHOD) {
            return;
        }

        String[] context = frame.currentScopeID().split("\\.");
        String methodName = context[context.length - 1];
        if (ignoredMethods.contains(methodName)) {
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

        if (ignore.contains(value)) {
            return;
        }

        this.log(ast, "magic", value);
    }

    @Override
    public void visitReference(DetailAST ast) {}

    @Override
    public void visitField(DetailAST ast) {}
}
