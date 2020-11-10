package ktplusplus.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.Filter;
import ktplusplus.checkstyle.Violation;
import ktplusplus.configuration.files.CheckConfig;
import ktplusplus.configuration.files.CheckFile;
import ktplusplus.configuration.model.Check;
import ktplusplus.util.CheckUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PromptFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger("kt++");

    private Scanner scanner;
    private CheckConfig checks;

    public PromptFilter(CheckConfig checks) {
        this.checks = checks;
        this.scanner = new Scanner(System.in);
    }

    private void showContext(Path source, int lineNo, int context) {
        try (Stream<String> lines = Files.lines(source)) {
            List<String> block = lines
                    .skip(lineNo - (context/2) - 1)
                    .limit(context)
                    .collect(Collectors.toList());
            for (String line : block) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean accept(AuditEvent event) {
        Check check = checks.getCheck(CheckUtil.checkName(event.getSourceName()));
        if (check == null) {
            LOGGER.warning("unable to find check configuration for " + event.getSourceName());
            return false;
        }

        if (check.prompt == null) {
            return true;
        }
        return true;

//        showContext(Paths.get(event.getFileName()), event.getLine(), check.prompt.context);
//        System.out.println(event.getMessage());
//        System.out.print("Is this correct? (y/n): ");
//        String next = scanner.next();
//        while (!next.equals("y") && !next.equals("n")) {
//            System.out.println("invalid input");
//            System.out.print("Is this correct? (y/n): ");
//            next = scanner.next();
//        }
//
//        return next.equals("y");
    }
}
