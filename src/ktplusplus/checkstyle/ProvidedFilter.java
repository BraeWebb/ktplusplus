package ktplusplus.checkstyle;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.Filter;
import ktplusplus.util.StudentFolder;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProvidedFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger("kt++");

    private Set<Violation> violations;

    private ProvidedFilter() {}

    public static ProvidedFilter fromProvided(StudentFolder folder,
                                              Configuration configuration) {
        Checker checker = new Checker();

        checker.setModuleClassLoader(Checker.class.getClassLoader());

        try {
            checker.configure(configuration);
        } catch (CheckstyleException e) {
            LOGGER.log(Level.SEVERE, "unable to load checkstyle configuration", e);
        }

        ViolationListener listener = new ViolationListener();
        checker.addListener(listener);

        try {
            checker.process(folder.getFiles());
        } catch (CheckstyleException e) {
            LOGGER.log(Level.SEVERE, "checkstyle exception", e);
        }

        ProvidedFilter filter = new ProvidedFilter();
        filter.violations = new HashSet<>(listener.getViolations());
        System.out.println(filter.violations);
        return filter;
    }

    @Override
    public boolean accept(AuditEvent event) {
        Violation violation = Violation.fromAudit(event);
        return !violations.contains(violation);
    }
}
