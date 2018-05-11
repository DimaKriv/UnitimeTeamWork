package toXmlParser;


import com.jamesmurty.utils.XMLBuilder;
import parserUtility.ParserUtility;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class SubjectAreas {

    private static final int DEFAULT_INITIAL_EXTERNAL_ID = 1000;

    private ParserUtility utility;
    private String querySql;
    private ResultSet queryResultSet;

    public SubjectAreas() throws SQLException {
        utility = new ParserUtility();
        Connection connection = utility.connectToDatabase();
        Statement statement = utility.createStatement(connection);
        querySql = "SELECT DISTINCT ainekood, department_id, nimetus FROM SUBJECT_DEPARTMENT WHERE Length(ainekood)=7 GROUP BY ainekood";
        queryResultSet = utility.queryDataFromDatabase(querySql, statement);
    }

    public SubjectAreas(String querySql, ResultSet queryResultSet) {
        utility = new ParserUtility();
        this.querySql = querySql;
        this.queryResultSet = queryResultSet;
    }

    public XMLBuilder createSubjectAreasElementBuilder(String campus, String term, String year)
            throws ParserConfigurationException {

        return XMLBuilder.create("subjectAreas")
                .attribute("campus", campus)
                .attribute("term", term)
                .attribute("year", year);
    }

    public XMLBuilder buildXML(String campus, String term, String year) throws ParserConfigurationException,
            SQLException {

        XMLBuilder xmlBuilder = createSubjectAreasElementBuilder(campus, term, year);

        int i = DEFAULT_INITIAL_EXTERNAL_ID;
        while (queryResultSet.next()) {

            String rawTitle = queryResultSet.getString("nimetus");
            String title;

            if (rawTitle.length() > 100) {
                title = rawTitle.substring(0, 100);
            } else {
                title = rawTitle;
            }

            String externalID = String.valueOf(i);
            String abbreviation = queryResultSet.getString("ainekood");

            String departmentID = queryResultSet.getString("department_id");

            xmlBuilder.element("subjectArea")
                    .attribute("externalId", externalID)
                    .attribute("abbreviation", abbreviation)
                    .attribute("title", title)
                    .attribute("department", departmentID);

            i++;
        }

        return xmlBuilder;
    }

    public void writeXML(XMLBuilder xmlBuilder) throws FileNotFoundException, TransformerException {
        utility.writeToXMLFile(xmlBuilder, "subject_areas.xml");
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
