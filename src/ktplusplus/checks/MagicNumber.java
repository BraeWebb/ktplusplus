package ktplusplus.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class MagicNumber extends AbstractCheck {

    private Set<Double> ignore = new HashSet<>();

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

        if (ignore.contains(value)) {
            return;
        }

        this.log(ast, "magic", value);
    }
}
