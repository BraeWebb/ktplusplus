package ktplusplus.configuration;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ConfigParser {
    public static Configuration parse(String filename)
            throws FileNotFoundException, YamlException {
        YamlReader reader = new YamlReader(new FileReader(filename));
        Configuration config = reader.read(Configuration.class);

        return config;
    }
}
