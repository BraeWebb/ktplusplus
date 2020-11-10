package ktplusplus.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;

import java.util.ArrayList;
import java.util.List;

public class ViolationListener implements AuditListener {
    private List<Violation> violations = new ArrayList<>();

    public List<Violation> getViolations() {
        return new ArrayList<>(violations);
    }

    @Override
    public void auditStarted(AuditEvent event) {

    }

    @Override
    public void auditFinished(AuditEvent event) {

    }

    @Override
    public void fileStarted(AuditEvent event) {

    }

    @Override
    public void fileFinished(AuditEvent event) {

    }

    @Override
    public void addError(AuditEvent event) {
        violations.add(Violation.fromAudit(event));
    }

    @Override
    public void addException(AuditEvent event, Throwable throwable) {

    }
}
