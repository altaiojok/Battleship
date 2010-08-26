package battleship.config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


class ConfigurationGeneratorNaiveImpl implements ConfigurationGenerator {

    private static class ConfigEntry {
        String name;
        String value;
        String type;
    }

    @Override
    public void generate() throws IOException, SAXException, ParserConfigurationException {
        writeJava(buildJava(readXml()));
    }

    String readXml() throws IOException {
        final StringBuilder xml = new StringBuilder();

        final BufferedReader br = new BufferedReader(new FileReader(XML_CONFIG));
        String line;
        while ((line = br.readLine()) != null) {
            xml.append(line);
        }

        br.close();

        return xml.toString();
    }

    String buildJava(String xml) throws IOException, SAXException, ParserConfigurationException {
        StringBuilder java = new StringBuilder(
                "package battleship.config;\n" +
                        "\n" +
                        "public class Configuration {\n");


        for (ConfigEntry c : parseXml(xml)) {

            java.append("    final public static ")
                    .append(c.type)
                    .append(" ")
                    .append(c.name)
                    .append(" = ")
                    .append(c.value)
                    .append(";\n");
        }

        java.append("}");

        return java.toString();
    }

    private List<ConfigEntry> parseXml(String xml) throws SAXException, ParserConfigurationException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        final List<ConfigEntry> configEntries = new LinkedList<ConfigEntry>();

        DefaultHandler handler = new DefaultHandler() {
            final Stack<String> currentElement = new Stack<String>();
            ConfigEntry configEntry;

            @Override
            public void startDocument() throws SAXException {
                assert currentElement.isEmpty();
            }

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                currentElement.push(qName);

                if("CONFIG".equalsIgnoreCase(currentElement.peek())) {
                    assert configEntry == null;
                    configEntry = new ConfigEntry();
                }

            }

            @Override
            public void characters(char ch[], int start, int length) throws SAXException {
                if("NAME".equalsIgnoreCase(currentElement.peek())) {
                    configEntry.name = new String(ch, start, length);
                } else if("VALUE".equalsIgnoreCase(currentElement.peek())) {
                    configEntry.value = new String(ch, start, length);
                } else if("TYPE".equalsIgnoreCase(currentElement.peek())) {
                    configEntry.type = new String(ch, start, length);
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                if("CONFIG".equalsIgnoreCase(qName)) {
                    if(configEntry.name == null || configEntry.value == null || configEntry.type == null) {
                        throw new IllegalArgumentException("Required field missing in config");
                    }

                    assert configEntry != null;
                    configEntries.add(configEntry);
                    configEntry = null;
                }

                currentElement.pop();
            }

            @Override
            public void endDocument() throws SAXException {
                assert currentElement.isEmpty();
            }
        };

        saxParser.parse(new ByteArrayInputStream(xml.getBytes()), handler);

        return configEntries;
    }

    void writeJava(String java) throws IOException {
        final File javaConfig = new File(JAVA_CONFIG);

        if(!javaConfig.exists()) {
            javaConfig.getParentFile().mkdirs();
        }

        PrintWriter pw = new PrintWriter(javaConfig);
        pw.write(java);
        pw.flush();
        pw.close();
    }
}
