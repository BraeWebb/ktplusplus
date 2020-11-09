package ktplusplus.feedback;

import ktplusplus.checkstyle.Violation;
import ktplusplus.configuration.model.Category;
import ktplusplus.configuration.model.Wrong;

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

    public List<Category> getCategories() {
        return new ArrayList<>(categories);
    }

    public Map<Category, List<Violation>> getViolations() {
        return new HashMap<>(violations);
    }

    public Map<Category, Float> getGrades() {
        return new HashMap<>(grades);
    }

    private String checkName(String check) {
        String[] checkParts = check.split("\\.");
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

                for (String check : wrong.checks) {
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

    public String format(FeedbackFormatter formatter) {
        return formatter.format(this);
    }
}
