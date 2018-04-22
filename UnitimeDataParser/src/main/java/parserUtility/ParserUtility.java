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

    // SQLite connection string
    public final static String DATABASE_URL = "jdbc:sqlite:../Tunniplaan.db";
    // Directory name, in which xml file are saved
    public final static String XML_FILES_DIRECTORY = "XMLFiles";

    /**
     * Connect to the database
     * @return the Connection object
     */
    public static Connection connectToDatabase() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Make query from database and return queried data in ResultSet object
     * @param querySQL SQL statement to query from database
     * @return queried data in ResultSet object.
     */
    public static ResultSet queryDataFromDatabase(String querySQL){
        ResultSet resultSet = null;
        try {
            Connection connection = connectToDatabase();
            Statement statement  = connection.createStatement();
            resultSet = statement.executeQuery(querySQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    /**
     * Write content of XMLBuilder to file with given name.
     * @param xmlBuilder with xml file content to save (write).
     * @param fileName in which xml content from XMLBuilder will be saved.
     * @throws FileNotFoundException is thrown if file where is needed to write xml content does not exist.
     * @throws TransformerException occurred on transformation XMLBuilder toWriter function.
     */
    public static void writeToXMLFile(XMLBuilder xmlBuilder, String fileName) throws FileNotFoundException, TransformerException {
        // create XMLFiles directory
        new File("XMLFiles").mkdirs();
        // create write for saving xml content in file with given name
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream("XMLFiles/" + fileName), Charset.forName("UTF-8").newEncoder()));
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
