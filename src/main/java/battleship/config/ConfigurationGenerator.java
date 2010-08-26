package battleship.config;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public interface ConfigurationGenerator {

    static final String XML_CONFIG = "src/main/resources/config.xml";
    static final String JAVA_CONFIG = "target/generated-sources/java/battleship/config/Configuration.java";

    void generate() throws IOException, SAXException, ParserConfigurationException;
}
