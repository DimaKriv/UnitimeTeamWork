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

    private static final int FIRST_DEPARTMENT = 1001;
    private static final int LAST_DEPARTMENT = 1020;

    private ParserUtility utility;
    private String querySql;
    private ResultSet queryResultSet;

    public SubjectAreas() throws SQLException {
        utility = new ParserUtility();
        Connection connection = utility.connectToDatabase();
        Statement statement = utility.createStatement(connection);
        querySql = "SELECT * FROM SUBJECT_AREAS_TTU";
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

        int currentDepartment = FIRST_DEPARTMENT;

        while (queryResultSet.next() && currentDepartment <= LAST_DEPARTMENT) {
            String externalID = queryResultSet.getString("EXTERNAL_ID");
            String abbreviation = queryResultSet.getString("ABBV");
            String title = queryResultSet.getString("TITLE");
            String department = String.valueOf(currentDepartment);

            xmlBuilder.element("subjectArea")
                    .attribute("externalId", externalID)
                    .attribute("abbreviation", abbreviation)
                    .attribute("title", title)
                    .attribute("department", department);

            currentDepartment++;
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
