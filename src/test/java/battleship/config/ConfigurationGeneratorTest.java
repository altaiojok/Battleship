package battleship.config;

import junit.framework.TestCase;

public class ConfigurationGeneratorTest extends TestCase {

    public void testReadXml() throws Exception {
        assertNotNull(new ConfigurationGeneratorNaiveImpl().readXml());
    }

    public void testBuildJava() throws Exception {
        final String rawXml =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<configs xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:noNamespaceSchemaLocation=\"config.xsd\">\n" +
                "\n" +
                "    <config>\n" +
                "        <name>debugMode</name>\n" +
                "        <value>false</value>\n" +
                "        <type>boolean</type>\n" +
                "    </config>\n" +
                "\n" +
                "    <config>\n" +
                "        <name>gridSize</name>\n" +
                "        <value>10</value>\n" +
                "        <type>int</type>\n" +
                "    </config>\n" +
                "</configs>";

        final String expectedJava =
                "package battleship.config;\n" +
                "\n" +
                "public class Configuration {\n" +
                "    final public static boolean debugMode = false;\n" +
                "    final public static int gridSize = 10;\n" +
                "}";


        assertEquals(expectedJava, new ConfigurationGeneratorNaiveImpl().buildJava(rawXml));
    }
}
