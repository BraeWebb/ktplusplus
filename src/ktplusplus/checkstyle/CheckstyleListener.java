package ktplusplus.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import ktplusplus.feedback.Feedback;

import java.util.HashMap;
import java.util.Map;

public class CheckstyleListener implements AuditListener {
    private Feedback feedback;

    public static CheckstyleListener listener(Feedback feedback) {
        CheckstyleListener listener = new CheckstyleListener();
        listener.feedback = feedback;
        return listener;
    }

    private Map<String, Integer> violationCount = new HashMap<>();

    public Map<String, Integer> getViolationCount() {
        return violationCount;
    }

    @Override
    public void auditStarted(AuditEvent auditEvent) {

    }

    @Override
    public void auditFinished(AuditEvent auditEvent) {

    }

    @Override
    public void fileStarted(AuditEvent auditEvent) {

    }

    @Override
    public void fileFinished(AuditEvent auditEvent) {

    }

    @Override
    public void addError(AuditEvent auditEvent) {
        feedback.addViolation(Violation.fromAudit(auditEvent));
        int count = violationCount.getOrDefault(auditEvent.getSourceName(), 0);
        violationCount.put(auditEvent.getSourceName(), count + 1);
    }

    @Override
    public void addException(AuditEvent auditEvent, Throwable throwable) {
        System.out.println(auditEvent.getMessage());
    }
}