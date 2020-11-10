package ktplusplus.feedback;

import ktplusplus.checkstyle.Violation;
import ktplusplus.configuration.model.Category;
import ktplusplus.configuration.model.Wrong;
import ktplusplus.util.CheckUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Feedback {
    private Map<Category, List<Violation>> violations = new HashMap<>();
    private List<Category> categories;

    public Feedback(List<Category> categories) {
        this.categories = categories;
        for (Category category : categories) {
            violations.put(category, new ArrayList<>());
        }
    }

    public List<Category> getCategories() {
        return new ArrayList<>(categories);
    }

    public Map<Category, List<Violation>> getViolations() {
        return new HashMap<>(violations);
    }

    public int getViolationCount() {
        return violations.values().stream().mapToInt(List::size).sum();
    }

    public Map<Category, Float> getGrades() {
        Map<Category, Float> grades = new HashMap<>();
        for (Category category : violations.keySet()) {
            if (category.wrongs == null) {
                grades.put(category, (float) category.total);
                continue;
            }

            float grade = category.total;
            List<Violation> violations = this.violations.get(category);
            for (Wrong wrong : category.wrongs) {
                if (wrong.checks == null) {
                    continue;
                }

                long violationCount = violations
                        .stream()
                        .filter(violation -> wrong.checks.contains(violation.getCheck()))
                        .count();

                float deduction = Math.max(0, violationCount - wrong.ignore);
                deduction = deduction * wrong.factor;
                deduction = Math.min(wrong.max, deduction);
                grade -= deduction;

                if (wrong.steps != null) {
                    float max = 0;
                    int biggestStep = 0;
                    for (String stepValue : wrong.steps.keySet()) {
                        int step = Integer.parseInt(stepValue);
                        if (step > biggestStep) {
                            biggestStep = step;
                            max = wrong.steps.get(stepValue);
                        }
                    }
                    grade -= max;
                }
            }
            grades.put(category, grade);
        }
        return grades;
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
                    String checkName = CheckUtil.checkName(check);

                    if (violation.getCheck().equalsIgnoreCase(checkName)) {
                        violations.getOrDefault(category, new ArrayList<>())
                                .add(violation);
                    }
                }
            }
        }
    }

    public String format(FeedbackFormatter formatter) {
        return formatter.format(this);
    }
}
