package toXmlParser;

import com.jamesmurty.utils.XMLBuilder;
import parserUtility.ParserUtility;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Preferences {

    private ParserUtility utility;
    private String querySql;
    private ResultSet queryResultSet;

    public Preferences() throws SQLException {
        utility = new ParserUtility();
        Connection connection = utility.connectToDatabase();
        Statement statement = utility.createStatement(connection);
        querySql = "SELECT * FROM INSTITUDID";
        queryResultSet = utility.queryDataFromDatabase(querySql, statement);
    }

    public Preferences(String querySql, ResultSet queryResultSet) {
        utility = new ParserUtility();
        this.querySql = querySql;
        this.queryResultSet = queryResultSet;
    }

    public XMLBuilder createDepartmentsElementBuilder(String campus, String term, String year) throws ParserConfigurationException {
        XMLBuilder xmlBuilder =
                XMLBuilder.create("departments")
                        .attribute("campus", campus)
                        .attribute("term", term)
                        .attribute("year", year);

        return xmlBuilder;
    }

    public XMLBuilder buildXML(String campus, String term, String year) throws ParserConfigurationException, SQLException {
        XMLBuilder xmlBuilder = createDepartmentsElementBuilder(campus, term, year);

        while (queryResultSet.next()) {
        }
        return xmlBuilder;
    }

    public void writeXML(XMLBuilder xmlBuilder) throws FileNotFoundException, TransformerException {
        utility.writeToXMLFile(xmlBuilder, "preferences.xml");
    }

    public void createXMLFile(String campus, String term, String year) {
        try {
            XMLBuilder xmlBuilder = buildXML(campus, term, year);
            writeXML(xmlBuilder);
        } catch (ParserConfigurationException | SQLException | FileNotFoundException | TransformerException e) {
            e.printStackTrace();
        }
    }
}
