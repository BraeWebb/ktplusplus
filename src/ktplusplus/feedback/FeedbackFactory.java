package ktplusplus.feedback;

import ktplusplus.configuration.model.Category;

import java.util.List;

public class FeedbackFactory {
    private List<Category> categories;

    public FeedbackFactory(List<Category> categories) {
        this.categories = categories;
    }

    public Feedback getFeedback(String sid) {
        return new Feedback(categories);
    }
}
