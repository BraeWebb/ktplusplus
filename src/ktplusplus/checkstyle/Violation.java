package ktplusplus.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;

public class Violation {
    private AuditEvent event;

    protected static Violation fromAudit(AuditEvent event) {
        Violation violation = new Violation();
        violation.event = event;
        return violation;
    }

    public String getMessage() {
        return event.getMessage();
    }

    public String getFilename() {
        return event.getFileName();
    }

    public String getCheck() {
        String[] sourceName = event.getSourceName().split("\\.");
        return sourceName[sourceName.length - 1];
    }
}
