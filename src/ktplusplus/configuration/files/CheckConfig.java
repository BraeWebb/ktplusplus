package ktplusplus.configuration.files;

import ktplusplus.checkstyle.CheckstyleConfig;
import ktplusplus.configuration.model.Check;
import ktplusplus.util.CheckUtil;

import java.util.HashMap;
import java.util.Map;

public class CheckConfig {
    private Map<String, Check> checks = new HashMap<>();

    private CheckConfig(CheckFile file) {
        for (Check check : file.checks) {
            this.checks.put(CheckUtil.checkName(check.name), check);
        }
    }

    public static CheckConfig fromFile(CheckFile file) {
        return new CheckConfig(file);
    }

    public Check getCheck(String name) {
        return checks.get(name);
    }

    public CheckstyleConfig build() {
        CheckstyleConfig checks = new CheckstyleConfig();

        for (Check check : this.checks.values()) {
            checks.addCheck(check);
        }

        return checks;
    }
}
