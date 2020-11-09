package ktplusplus.configuration;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ConfigParser {
    public static AssessmentFile parseAssessmentFile(String filename)
            throws FileNotFoundException, YamlException {
        YamlReader reader = new YamlReader(new FileReader(filename));
        return reader.read(AssessmentFile.class);
    }

    public static CheckFile parseCheckFile(String filename)
            throws FileNotFoundException, YamlException {
        YamlReader reader = new YamlReader(new FileReader(filename));
        return reader.read(CheckFile.class);
    }
}
