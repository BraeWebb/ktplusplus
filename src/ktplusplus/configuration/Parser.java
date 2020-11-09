package ktplusplus.configuration;

import com.esotericsoftware.yamlbeans.YamlException;

import java.io.FileNotFoundException;

@FunctionalInterface
public interface Parser {
    ConfigFile parse(String path) throws FileNotFoundException, YamlException;
}
