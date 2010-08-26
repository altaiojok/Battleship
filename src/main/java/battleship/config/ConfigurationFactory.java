package battleship.config;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class ConfigurationFactory {

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        new ConfigurationGeneratorNaiveImpl().generate();
    }

}
