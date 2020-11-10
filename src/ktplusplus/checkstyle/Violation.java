package ktplusplus.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import ktplusplus.util.CheckUtil;

public class Violation {
    private final AuditEvent event;

    private Violation(AuditEvent event) {
        this.event = event;
    }

    protected static Violation fromAudit(AuditEvent event) {
        return new Violation(event);
    }

    public String getMessage() {
        return event.getMessage();
    }

    public String getFilename() {
        return event.getFileName();
    }

    public int getLineNo() {
        return event.getLine();
    }

    public int getColumn() {
        return event.getColumn();
    }

    public String getCheck() {
        return CheckUtil.checkName(event.getSourceName());
    }
}
