package ktplusplus.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import ktplusplus.util.CheckUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

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

    public String getBasename() {
        Path file = Paths.get(event.getFileName());
        return file.getName(file.getNameCount() - 1).toString();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Violation violation = (Violation) o;

        return violation.getLineNo() == getLineNo()
                && violation.getColumn() == getColumn()
                && violation.getMessage().equals(getMessage())
                && violation.getCheck().equals(getCheck())
                && violation.getBasename().equals(getBasename());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLineNo(), getColumn(),
                getMessage(), getCheck(), getBasename());
    }
}
