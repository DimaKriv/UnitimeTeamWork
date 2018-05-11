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

public class Curricula {

    private ParserUtility utility;
    private String querySql;
    private ResultSet queryResultSet;

    public Curricula() throws SQLException {
        utility = new ParserUtility();
        Connection connection = utility.connectToDatabase();
        Statement statement = utility.createStatement(connection);
        querySql = "";
        queryResultSet = utility.queryDataFromDatabase(querySql, statement);
    }

    public Curricula(ParserUtility utility, String querySql, ResultSet queryResultSet) {
        this.utility = utility;
        this.querySql = querySql;
        this.queryResultSet = queryResultSet;
    }

    public XMLBuilder createCurriculaElementBuilder(String campus, String term, String year) throws ParserConfigurationException {
        XMLBuilder xmlBuilder =
                XMLBuilder.create("curricula")
                        .attribute("campus", campus)
                        .attribute("term", term)
                        .attribute("year", year);

        return xmlBuilder;
    }

    public XMLBuilder buildXML(String campus, String term, String year) throws ParserConfigurationException, SQLException {
        XMLBuilder xmlBuilder = createCurriculaElementBuilder(campus, term, year);

        while (queryResultSet.next()) {

        }
        return xmlBuilder;
    }

    public void writeXML(XMLBuilder xmlBuilder) throws FileNotFoundException, TransformerException {
        utility.writeToXMLFile(xmlBuilder, "departments.xml");
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
