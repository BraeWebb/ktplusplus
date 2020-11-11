package ktplusplus;

import com.esotericsoftware.yamlbeans.YamlException;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.FilterSet;
import ktplusplus.checkstyle.*;
import ktplusplus.configuration.*;
import ktplusplus.configuration.files.AssessmentFile;
import ktplusplus.configuration.files.CheckConfig;
import ktplusplus.configuration.files.CheckFile;
import ktplusplus.configuration.files.ConfigFile;
import ktplusplus.configuration.model.Check;
import ktplusplus.feedback.Feedback;
import ktplusplus.feedback.FeedbackFactory;
import ktplusplus.feedback.FeedbackFormatter;
import ktplusplus.feedback.StandardFeedbackFormat;
import ktplusplus.util.FileLoader;
import ktplusplus.util.StudentFolder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Main {
    private static final Logger LOGGER = Logger.getLogger("kt++");

    private static final String USAGE = "usage: ktplusplus <assessment.yml> <checks.yml> <submissions> [--provided <provided>] [--prompt] [--grades <output>]";

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
            System.err.println(USAGE);
            System.exit(1);
        }

        String provided = null;
        String grades = null;
        boolean prompt = false;

        List<String> params = Arrays.stream(args).skip(3).collect(Collectors.toList());
        Stack<String> arguments = new Stack<>();
        Collections.reverse(params);
        arguments.addAll(params);

        while (!arguments.empty()) {
            String flag = arguments.pop();
            switch (flag) {
                case "--provided":
                    if (arguments.empty()) {
                        System.err.println("--provided flag needs an argument");
                        System.err.println(USAGE);
                        System.exit(1);
                    }
                    provided = arguments.pop();
                    break;
                case "--grades":
                    if (arguments.empty()) {
                        System.err.println("--grades flag needs an argument");
                        System.err.println(USAGE);
                        System.exit(1);
                    }
                    grades = arguments.pop();
                    break;
                case "--prompt":
                    prompt = true;
                    break;
                default:
                    System.err.println("unrecognised flag: " + flag);
                    System.err.println(USAGE);
                    System.exit(1);
            }
        }

        AssessmentFile config = (AssessmentFile) readYaml(args[0],
                ConfigParser::parseAssessmentFile);
        if (config == null) {
            return;
        }

        CheckFile checkFile = (CheckFile) readYaml(args[1],
                ConfigParser::parseCheckFile);
        if (checkFile == null) {
            return;
        }
        CheckConfig checks = CheckConfig.fromFile(checkFile);

        LOGGER.log(Level.INFO, "Running kt++ on {0} for {1} in {2}",
                new String[]{config.rubric, config.course, config.semester});


        Configuration configuration = checks.build();

        FeedbackFactory factory = new FeedbackFactory(config.categories);

        Checker checker = new Checker();
        checker.setModuleClassLoader(Checker.class.getClassLoader());

        FilterSet filers = new OrderedFilterSet();
        checker.addFilter(filers);

        if (provided != null) {
            StudentFolder providedFolder = StudentFolder.load(
                    Paths.get(provided), config.files
            );
            filers.addFilter(ProvidedFilter.fromProvided(providedFolder, configuration));
        }

        try {
            checker.configure(configuration);
        } catch (CheckstyleException e) {
            LOGGER.log(Level.SEVERE, "unable to load checkstyle configuration", e);
        }

        Path submissions = Paths.get(args[2]);
        FileLoader loader = FileLoader.load(submissions, config.files);
        for (StudentFolder folder : loader.getFolders()) {
            System.out.println("*********** " + folder.getStudent() + " ***********");
            Feedback feedback = factory.getFeedback(folder.getStudent());

            CheckstyleListener listener = CheckstyleListener.listener(feedback);
            checker.addListener(listener);
            PromptFilter filter = new PromptFilter(checks);
            if (prompt) {
                filers.addFilter(filter);
            }
            try {
                checker.process(folder.getFiles());
            } catch (CheckstyleException e) {
                LOGGER.log(Level.SEVERE, "", e);
            }
            checker.removeListener(listener);
            if (prompt) {
                filers.removeFilter(filter);
            }

            FeedbackFormatter formatter = new StandardFeedbackFormat(submissions, config);
            if (grades != null) {
                Path feedbackFile = Paths.get(grades, folder.getStudent() + ".style");
                Files.write(
                        feedbackFile,
                        Arrays.asList(feedback.format(formatter).split("\n")),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE
                );
            } else {
                System.out.println(feedback.format(formatter));
            }
        }
    }
}
    