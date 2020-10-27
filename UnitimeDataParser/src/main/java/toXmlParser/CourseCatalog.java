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

public class CourseCatalog {

    private static final int DEFAULT_FAKE_EAP = 5;

    private ParserUtility utility;
    private String querySql;
    private ResultSet queryResultSet;

    public CourseCatalog() throws SQLException {
        utility = new ParserUtility();
        Connection connection = utility.connectToDatabase();
        Statement statement = utility.createStatement(connection);
        querySql = "SELECT fk_aine_id, ainekood, nimetus_est FROM TUNN_AINE WHERE Length(ainekood)<=10";
        queryResultSet = utility.queryDataFromDatabase(querySql, statement);
    }

    public CourseCatalog(String querySql, ResultSet queryResultSet) {
        utility = new ParserUtility();
        this.querySql = querySql;
        this.queryResultSet = queryResultSet;
    }

    public XMLBuilder createCourseCatalogElementBuilder(String campus, String term, String year) throws ParserConfigurationException {
        return XMLBuilder.create("courseCatalog")
                .attribute("campus", campus)
                .attribute("term", term)
                .attribute("year", year);
    }

    public XMLBuilder buildXML(String campus, String term, String year) throws ParserConfigurationException, SQLException {
        XMLBuilder xmlBuilder = createCourseCatalogElementBuilder(campus, term, year);

        while (queryResultSet.next()) {
            String subject = queryResultSet.getString("ainekood");
            String courseNumber = queryResultSet.getString("fk_aine_id");
            String title = queryResultSet.getString("nimetus_est");

            XMLBuilder course = xmlBuilder.element("course")
                    .attribute("subject", subject)
                    .attribute("courseNumber", courseNumber)
                    .attribute("title", title);

            course.element("courseCredit")
                    .attribute("creditType", "collegiate")
                    .attribute("creditUnitType", "semesterHours")
                    .attribute("creditFormat", "fixedUnit")
                    .attribute("fixedCredit", String.valueOf(DEFAULT_FAKE_EAP));
        }

        return xmlBuilder;
    }

    public void writeXML(XMLBuilder xmlBuilder) throws FileNotFoundException, TransformerException {
        utility.writeToXMLFile(xmlBuilder, "course_catalog.xml");
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
