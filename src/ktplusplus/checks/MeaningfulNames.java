package ktplusplus.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MeaningfulNames extends AbstractCheck {
    private static Set<String> words = new HashSet<>();
    static {
        try {
            words = new HashSet<>(Files.readAllLines(Paths.get("resources/words.txt")));
            words = words.stream().map(String::toLowerCase).collect(Collectors.toSet());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String CAMEL_CASE_PATTERN = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])";

    @Override
    public int[] getDefaultTokens() {
        return new int[]{TokenTypes.VARIABLE_DEF};
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
        String identifier = ast.findFirstToken(TokenTypes.IDENT).getText();

        if (identifier.length() == 1) {
            char id = identifier.charAt(0);
            if (id != 'i' && id != 'j' && id != 'k') {
                this.log(ast, "single.letter", identifier);
            }
        }

        for (String part : identifier.split(CAMEL_CASE_PATTERN)) {
            part = part.toLowerCase().replace('_', ' ').trim();
            if (!words.contains(part)) {
                this.log(ast, "meaningful", identifier, part);
            }
        }
    }
}
