package parserUtility;

import com.jamesmurty.utils.XMLBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ParserUtilityTest {

    private ParserUtility utility;

    @Before
    public void setup() {
        utility = new ParserUtility();
    }

    @After
    public void end() {
        new File("test/directoryCreationTest.xml").delete();
        new File("test/directoryConfirmTest.xml").delete();
        new File("test/fileCreationTest.xml").delete();
        new File("test/indentAmountTest.xml").delete();
        new File("test").delete();
    }

    @Test(expected = SQLException.class)
    public void testCallingGetConnectionMethodFromDriverManagerWithEmptyDatabaseUrl() throws SQLException {
        utility = new ParserUtility("", "");
        utility.connectToDatabase();
    }

    @Test
    public void testCallingCreateStatementFromConnection() throws SQLException {
        Connection connection = mock(Connection.class);
        utility.createStatement(connection);
        verify(connection).createStatement();
    }

    @Test
    public void testCallingExecuteQueryFromStatement() throws SQLException {
        Statement statement = mock(Statement.class);
        String sqlQuery = anyString();
        utility.queryDataFromDatabase(sqlQuery, statement);
        verify(statement.executeQuery(sqlQuery));
    }

    @Test
    public void testNewDirectoryForXMLFilesInWriteToXMLFileIsCreated() throws ParserConfigurationException, FileNotFoundException, TransformerException {
        utility = new ParserUtility("", "test");
        XMLBuilder xmlBuilder = XMLBuilder.create("test");
        utility.writeToXMLFile(xmlBuilder, "directoryCreationTest.xml");
        File directory = new File("test");
        assertTrue(directory.exists());
    }

    @Test
    public void testNewDirectoryForXMLFilesInWriteToXMLFileIsDirectory() throws ParserConfigurationException, FileNotFoundException, TransformerException {
        utility = new ParserUtility("", "test");
        XMLBuilder xmlBuilder = XMLBuilder.create("test");
        utility.writeToXMLFile(xmlBuilder, "directoryConfirmTest.xml");
        File directory = new File("test");
        assertTrue(directory.isDirectory());
    }

    @Test
    public void testNewFileFromXMLBuilderInWriteToXMLFileIsCreated() throws ParserConfigurationException, FileNotFoundException, TransformerException {
        utility = new ParserUtility("", "test");
        XMLBuilder xmlBuilder = XMLBuilder.create("test");
        utility.writeToXMLFile(xmlBuilder, "fileCreationTest.xml");
        File file = new File("test/fileCreationTest.xml");
        assertTrue(file.exists());
    }

    @Test
    public void testIndentAmountInCreatedXMLFileIsTwo() throws ParserConfigurationException, IOException, TransformerException {
        utility = new ParserUtility("", "test");
        XMLBuilder xmlBuilder = XMLBuilder.create("test").element("subTest");
        String expectedFileContent = xmlBuilder.asString();

        utility.writeToXMLFile(xmlBuilder, "indentAmountTest.xml");
        String actualFileContent = new String(Files.readAllBytes(Paths.get("test/indentAmountTest.xml")));

        assertEquals(expectedFileContent, actualFileContent);
    }
}
