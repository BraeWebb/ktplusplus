package ktplusplus;

import com.esotericsoftware.yamlbeans.YamlException;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import ktplusplus.checkstyle.CheckstyleConfig;
import ktplusplus.checkstyle.CheckstyleListener;
import ktplusplus.configuration.*;
import ktplusplus.configuration.files.AssessmentFile;
import ktplusplus.configuration.files.CheckFile;
import ktplusplus.configuration.files.ConfigFile;
import ktplusplus.configuration.model.Check;
import ktplusplus.feedback.Feedback;
import ktplusplus.feedback.FeedbackFactory;
import ktplusplus.feedback.StandardFeedbackFormat;
import ktplusplus.util.FileLoader;
import ktplusplus.util.StudentFolder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger("kt++");

    private static CheckstyleConfig buildConfig(CheckFile config) {
        CheckstyleConfig checks = new CheckstyleConfig();

        for (Check check : config.checks) {
            checks.addCheck(check);
        }

        return checks;
    }

    private static ConfigFile readYaml(String path, Parser parser) {
        try {
            return parser.parse(path);
        } catch (FileNotFoundException e) {
            LOGGER.severe("unable to find configuration file: " + path);
            return null;
        } catch (YamlException e) {
            LOGGER.severe("unable to parse config file: " + path);
            LOGGER.log(Level.SEVERE, "", e);
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("usage: ktplusplus <checks.yml> <assessment.yml> <submissions>");
            System.exit(1);
        }

        AssessmentFile config = (AssessmentFile) readYaml(args[0],
                ConfigParser::parseAssessmentFile);
        if (config == null) {
            return;
        }

        CheckFile checks = (CheckFile) readYaml(args[1],
                ConfigParser::parseCheckFile);
        if (checks == null) {
            return;
        }


        LOGGER.log(Level.INFO, "Running kt++ on {0} for {1} in {2}",
                new String[]{config.rubric, config.course, config.semester});


        DefaultConfiguration configuration = new DefaultConfiguration("Checker");
        configuration.addChild(buildConfig(checks));

        FeedbackFactory factory = new FeedbackFactory(config.categories);

        Checker checker = new Checker();

        checker.setModuleClassLoader(Checker.class.getClassLoader());

        try {
            checker.configure(configuration);
        } catch (CheckstyleException e) {
            LOGGER.log(Level.SEVERE, "unable to load checkstyle configuration", e);
        }

        Path submissions = Paths.get(args[2]);
        FileLoader loader = FileLoader.load(submissions, config.files);
        for (StudentFolder folder : loader.getFolders()) {
            Feedback feedback = factory.getFeedback(folder.getStudent());

            CheckstyleListener listener = CheckstyleListener.listener(feedback);
            checker.addListener(listener);
            try {
                checker.process(folder.getFiles());
            } catch (CheckstyleException e) {
                LOGGER.log(Level.SEVERE, "", e);
            }
            checker.removeListener(listener);

            System.out.println(feedback.format(new StandardFeedbackFormat(submissions)));
        }
    }
}
    