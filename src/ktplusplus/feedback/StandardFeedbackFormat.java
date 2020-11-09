package ktplusplus.feedback;

import ktplusplus.checkstyle.Violation;
import ktplusplus.configuration.model.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StandardFeedbackFormat implements FeedbackFormatter {
    @Override
    public String format(Feedback feedback) {
        Map<Category, List<Violation>> violations = feedback.getViolations();
        Map<Category, Float> grades = feedback.getGrades();

        StringBuilder builder = new StringBuilder();
        for (Category category : feedback.getCategories()) {
            builder.append(category.name).append(": ");

            if (category.total != 0) {
                builder.append(grades.get(category)).append("/")
                        .append(category.total);
            }

            builder.append(System.lineSeparator());

            for (Violation violation : violations.getOrDefault(category, new ArrayList<>())) {
                builder.append("  - ").append(violation.getMessage());
                builder.append(" (e.g. in ").append(violation.getFilename())
                        .append(":").append(violation.getLineNo()).append(")");

                builder.append(System.lineSeparator());
            }
        }
        return builder.toString();
    }
}
