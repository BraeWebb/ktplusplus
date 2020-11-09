package ktplusplus.configuration.files;

import ktplusplus.configuration.model.Category;

import java.util.List;

public class AssessmentFile implements ConfigFile {
    public String rubric;
    public String course;
    public String semester;

    public List<String> files;

    public List<Category> categories;
}
