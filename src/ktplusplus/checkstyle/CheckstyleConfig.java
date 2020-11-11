package ktplusplus.checkstyle;

import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import ktplusplus.configuration.model.Check;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckstyleConfig implements Configuration {
    static Configuration fromCheck(Check check) {
        DefaultConfiguration checkConfig = new DefaultConfiguration(check.name);
        if (check.id != null) {
            checkConfig.addAttribute("id", check.id);
        }

        if (check.config != null) {
            for (Map.Entry<String, String> attr : check.config.entrySet()) {
                checkConfig.addAttribute(attr.getKey(), attr.getValue());
            }
        }

        if (check.messages != null) {
            for (Map.Entry<String, String> attr : check.messages.entrySet()) {
                checkConfig.addMessage(attr.getKey(), attr.getValue());
            }
        }

        return checkConfig;
    }

    static class TreeWalkerConfig implements Configuration {
        private final List<Configuration> checks = new ArrayList<>();

        public void addCheck(Check check) {
            checks.add(fromCheck(check));
        }

        @Override
        public String[] getAttributeNames() {
            return new String[0];
        }

        @Override
        public String getAttribute(String name) throws CheckstyleException {
            return null;
        }

        @Override
        public Configuration[] getChildren() {
            return checks.toArray(new Configuration[0]);
        }

        @Override
        public String getName() {
            return "TreeWalker";
        }

        @Override
        public Map<String, String> getMessages() {
            return null;
        }
    }

    private final TreeWalkerConfig walker = new TreeWalkerConfig();
    private final List<Configuration> checks = new ArrayList<>();

    public CheckstyleConfig() {
        checks.add(walker);
    }

    public void addCheck(Check check) {
        if (check.name.equals("LineLengthCheck")) {
            checks.add(fromCheck(check));
        } else {
            walker.addCheck(check);
        }
    }

    @Override
    public String[] getAttributeNames() {
        return new String[0];
    }

    @Override
    public String getAttribute(String s) throws CheckstyleException {
        return null;
    }

    @Override
    public Configuration[] getChildren() {
        return checks.toArray(new Configuration[0]);
    }

    @Override
    public String getName() {
        return "Checker";
    }

    @Override
    public Map<String, String> getMessages() {
        return null;
    }
}
