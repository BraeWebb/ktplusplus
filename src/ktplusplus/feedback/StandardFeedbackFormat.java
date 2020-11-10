package ktplusplus.feedback;

import ktplusplus.checkstyle.Violation;
import ktplusplus.configuration.files.AssessmentFile;
import ktplusplus.configuration.model.Category;
import ktplusplus.util.CheckUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Format;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class StandardFeedbackFormat implements FeedbackFormatter {
    private final Path root;
    private final AssessmentFile config;

    public StandardFeedbackFormat(Path root, AssessmentFile config) {
        this.root = root;
        this.config = config;
    }

    private Path relativePath(String path) {
        Path absPath = Paths.get(path);
        Path relativePath = root.toAbsolutePath().relativize(absPath);
        return relativePath.subpath(1, relativePath.getNameCount());
    }

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

            Map<String, List<Violation>> violationGroup = violations
                    .getOrDefault(category, new ArrayList<>())
                    .stream()
                    .collect(Collectors.groupingBy(Violation::getCheck));

            for (Map.Entry<String, List<Violation>> group : violationGroup.entrySet()) {
                int violationCount = 0;
                for (Violation violation : group.getValue()) {
                    violationCount += 1;

                    if (config.format != null && config.format.maxWarningsPerCheck > 0) {
                        if (violationCount > config.format.maxWarningsPerCheck) {
                            String message = MessageFormat.format(config.format.exceededMessage,
                                    group.getValue().size(),
                                    Math.round(group.getValue().size()/10.0) * 10
                            );
                            builder.append("  - ")
                                    .append(message)
                                    .append(System.lineSeparator());
                            break;
                        }
                    }

                    Path relativePath = relativePath(violation.getFilename());

                    builder.append("  - ").append(violation.getMessage());
                    builder.append(" (").append(relativePath)
                            .append(":").append(violation.getLineNo()).append(")");

                    builder.append(System.lineSeparator());
                }
            }
        }
        return builder.toString();
    }
}
