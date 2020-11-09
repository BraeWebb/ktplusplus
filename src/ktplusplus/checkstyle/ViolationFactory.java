package ktplusplus.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;

public class ViolationFactory {
    private final String root;

    public ViolationFactory(String root) {
        this.root = root;
    }

    public Violation newViolation(AuditEvent event) {
        return Violation.fromAudit(this.root, event);
    }
}
