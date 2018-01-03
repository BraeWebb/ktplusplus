package ktplusplus.feedback;

import ktplusplus.checkstyle.Violation;
import ktplusplus.configuration.Category;
import ktplusplus.configuration.Check;
import ktplusplus.configuration.Wrong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Feedback {
    private Map<Category, List<Violation>> violations = new HashMap<>();
    private Map<Category, Float> grades = new HashMap<>();
    private List<Category> categories;

    public Feedback(List<Category> categories) {
        this.categories = categories;
        for (Category category : categories) {
            violations.put(category, new ArrayList<>());
            grades.put(category, (float) category.total);
        }
    }

    private String checkName(Check check) {
        String[] checkParts = check.name.split("\\.");
        return checkParts[checkParts.length - 1];
    }

    public void addViolation(Violation violation) {
        for (Category category : categories) {
            if (category.wrongs == null) {
                continue;
            }

            for (Wrong wrong : category.wrongs) {
                if (wrong.checks == null) {
                    continue;
                }

                for (Check check : wrong.checks) {
                    String checkName = checkName(check);

                    if (violation.getCheck().equalsIgnoreCase(checkName)) {
                        violations.getOrDefault(category, new ArrayList<>())
                                .add(violation);
                        grades.put(category, grades.get(category) - 1);
                    }
                }
            }
        }
    }

    public String format() {
        StringBuilder builder = new StringBuilder();
        for (Category category : categories) {
            builder.append(category.name).append(": ");

            if (category.total != 0) {
                builder.append(grades.get(category)).append("/")
                        .append(category.total);
            }

            builder.append(System.lineSeparator());

            for (Violation violation : violations.getOrDefault(category, new ArrayList<>())) {
                builder.append("  - ").append(violation.getMessage());
                builder.append(" (e.g. in ").append(violation.getFilename()).append(")");

                builder.append(System.lineSeparator());
            }
        }
        return builder.toString();
    }
}
