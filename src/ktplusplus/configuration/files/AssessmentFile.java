package ktplusplus.configuration.files;

import ktplusplus.configuration.model.Category;
import ktplusplus.configuration.model.Format;

import java.util.List;

public class AssessmentFile implements ConfigFile {
    public String rubric;
    public String course;
    public String semester;

    public Format format;

    public List<String> files;

    public List<Category> categories;
}
