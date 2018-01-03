package ktplusplus;

import com.esotericsoftware.yamlbeans.YamlException;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import ktplusplus.checkstyle.CheckstyleConfig;
import ktplusplus.checkstyle.CheckstyleListener;
import ktplusplus.configuration.Category;
import ktplusplus.configuration.Check;
import ktplusplus.configuration.ConfigParser;
import ktplusplus.configuration.Configuration;
import ktplusplus.configuration.Wrong;
import ktplusplus.feedback.Feedback;
import ktplusplus.feedback.FeedbackFactory;
import ktplusplus.util.FileLoader;
import ktplusplus.util.StudentFolder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final String SAMPLE = "/Users/brae/work/csse2002/2020s2/ass1/brae01";

    private static final Logger LOGGER = Logger.getLogger("kt++");

    private static CheckstyleConfig buildConfig(Configuration config) {
        CheckstyleConfig checks = new CheckstyleConfig();

        for (Category category : config.categories) {
            if (category.wrongs == null) {
                continue;
            }

            for (Wrong wrong : category.wrongs) {
                if (wrong.checks == null) {
                    continue;
                }

                for (Check check : wrong.checks) {
                    checks.addCheck(check);
                }
            }
        }

        return checks;
    }

    public static void main(String[] args) throws IOException {
        String configFile = args[0];
        Configuration config;
        try {
            config = ConfigParser.parse(configFile);
        } catch (FileNotFoundException e) {
            LOGGER.severe("unable to find configuration file: " + configFile);
            return;
        } catch (YamlException e) {
            LOGGER.severe("unable to parse config file: " + configFile);
            LOGGER.log(Level.SEVERE, "", e);
            return;
        }

        LOGGER.log(Level.INFO, "Running kt++ on {0} for {1} in {2}",
                new String[]{config.rubric, config.course, config.semester});


        DefaultConfiguration configuration = new DefaultConfiguration("Checker");
        configuration.addChild(buildConfig(config));

        FeedbackFactory factory = new FeedbackFactory(config.categories);

        Checker checker = new Checker();

        checker.setModuleClassLoader(Checker.class.getClassLoader());

        try {
            checker.configure(configuration);
        } catch (CheckstyleException e) {
            LOGGER.log(Level.SEVERE, "unable to load checkstyle configuration", e);
        }

        FileLoader loader = FileLoader.load(Paths.get(SAMPLE), config.files);
        for (StudentFolder folder : loader.getFolders()) {
            System.out.println(folder.getFiles());
            Feedback feedback = factory.getFeedback(folder.getStudent());

            CheckstyleListener listener = CheckstyleListener.listener(feedback);
            checker.addListener(listener);
            try {
                checker.process(folder.getFiles());
            } catch (CheckstyleException e) {
                LOGGER.log(Level.SEVERE, "", e);
            }
            checker.removeListener(listener);

            System.out.println(feedback.format());
        }
    }
}
    