package parserUtility;


import com.jamesmurty.utils.XMLBuilder;

import javax.xml.transform.TransformerException;
import java.io.*;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ParserUtility {

    // SQLite connection url
    private String databaseUrl;
    // Directory name, in which xml file are saved
    private String xmlFilesDirectory;

    public ParserUtility() {
        databaseUrl = "jdbc:sqlite:../Tunniplaan.db";
        xmlFilesDirectory = "XMLFiles";
    }

    public ParserUtility(String databaseUrl, String xmlFilesDirectory) {
        this.databaseUrl = databaseUrl;
        this.xmlFilesDirectory = xmlFilesDirectory;
    }

    /**
     * Connect to the database
     * @return the Connection object
     */
    public Connection connectToDatabase() throws SQLException {
        return DriverManager.getConnection(databaseUrl);
    }

    /**
     * Create Statement object, which can process SQL string, using given connection object.
     * @param connection from which will be created Statement object
     * @return Statement object for executing given SQL query in database to which it is connected.
     */
    public Statement createStatement(Connection connection) throws SQLException {
        return connection.createStatement();
    }

    /**
     * Make query from database and return queried data in ResultSet object.
     * @param querySQL SQL statement to query from database.
     * @param statement is Statement object which will process given SQL string.
     * @return queried data in ResultSet object.
     */
    public ResultSet queryDataFromDatabase(String querySQL, Statement statement) throws SQLException {
        return statement.executeQuery(querySQL);
    }

    /**
     * Write content of XMLBuilder to file with given name.
     * @param xmlBuilder with xml file content to save (write).
     * @param fileName in which xml content from XMLBuilder will be saved.
     * @throws FileNotFoundException is thrown if file where is needed to write xml content does not exist.
     * @throws TransformerException occurred on transformation XMLBuilder toWriter function.
     */
    public void writeToXMLFile(XMLBuilder xmlBuilder, String fileName) throws FileNotFoundException, TransformerException {
        // create XMLFiles directory
        new File(xmlFilesDirectory).mkdirs();
        // create write for saving xml content in file with given name
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(xmlFilesDirectory + "/" + fileName), Charset.forName("UTF-8").newEncoder()));
        // set properties of indents for file
        Properties outputProperties = new Properties();
        outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
        outputProperties.put(javax.xml.transform.OutputKeys.INDENT, "yes");
        outputProperties.put("{http://xml.apache.org/xslt}indent-amount", "2");
        // write XMLBuilder content to file using given properties
        xmlBuilder.toWriter(writer, outputProperties);
        // send notification about file creation
        System.out.println("FILE " + fileName + " WAS CREATED");
    }
}
