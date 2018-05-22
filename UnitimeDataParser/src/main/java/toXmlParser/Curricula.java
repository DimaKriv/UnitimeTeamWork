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

    private XMLBuilder xmlBuilder;
    private int classificationEnrollmentNumber = 0;
    private String previousCurriculumName = "undefined";
    private int previousSemester = 0;
    private int curriculumNumber = 0;
    private int choiceSubjectCounter = 0;

    public Curricula() throws SQLException {
        utility = new ParserUtility();
        Connection connection = utility.connectToDatabase();
        Statement statement = utility.createStatement(connection);
        querySql = "SELECT * FROM CURRICULA";
        queryResultSet = utility.queryDataFromDatabase(querySql, statement);
    }

    public Curricula(String querySql, ResultSet queryResultSet) {
        utility = new ParserUtility();
        this.querySql = querySql;
        this.queryResultSet = queryResultSet;
    }

    public XMLBuilder buildXML(String campus, String term, String year) throws ParserConfigurationException, SQLException {
        xmlBuilder = createCurriculaElementBuilder(campus, term, year);

        while (queryResultSet.next()) {
            String currentCurriculumName = queryResultSet.getString("peaeriala_nimetus");
            int currentSemester = Integer.parseInt(queryResultSet.getString("semester_number"));
            String subjectName = queryResultSet.getString("ainekood");
            String subjectType = queryResultSet.getString("aine_tyyp");

            if (!previousCurriculumName.equals(currentCurriculumName)) {
                printCurriculumElement(currentCurriculumName);
            }

            if (previousSemester != currentSemester) {
                printClassificationElement(currentSemester);
            }

            if (previousCurriculumName.equals(currentCurriculumName)) {
                printCourseElement(subjectName, subjectType);
            }
        }

        return xmlBuilder;
    }

    public XMLBuilder createCurriculaElementBuilder(String campus, String term, String year) throws ParserConfigurationException {
        XMLBuilder xmlDocument =
                XMLBuilder.create("curricula")
                        .attribute("campus", campus)
                        .attribute("term", term)
                        .attribute("year", year);

        return xmlDocument;
    }

    private void printCurriculumElement(String currentCurriculumName) {
        classificationEnrollmentNumber++;
        curriculumNumber++;
        if (curriculumNumber > 1) {
            xmlBuilder = xmlBuilder.up().up();
        }
        xmlBuilder = xmlBuilder.element("curriculum")
                .attribute("name", currentCurriculumName)
                .element("academicArea")
                .attribute("abbreviation", "TTUM")
                .up();
        previousCurriculumName = currentCurriculumName;
        choiceSubjectCounter = 0;
    }

    private void printClassificationElement(int currentSemester) {
        if (currentSemester > 1) {
            xmlBuilder = xmlBuilder.up();
        }
        xmlBuilder = xmlBuilder.element("classification")
                .attribute("enrollment", String.valueOf(classificationEnrollmentNumber))
                .element("academicClassification")
                .attribute("code", String.valueOf(currentSemester))
                .up();
        previousSemester = currentSemester;
        choiceSubjectCounter++;
    }

    private void printCourseElement(String subjectName, String subjectType) {
        xmlBuilder = xmlBuilder.element("course")
                .attribute("subject", subjectName)
                .attribute("courseNbr", "1");

        if (subjectType.equals("V")) {
            xmlBuilder.element("group")
                    .attribute("id", String.valueOf(choiceSubjectCounter))
                    .attribute("type", "OPT");
        }

        xmlBuilder = xmlBuilder.up();
    }

    public void writeXML(XMLBuilder xmlBuilder) throws FileNotFoundException, TransformerException {
        utility.writeToXMLFile(xmlBuilder, "curricula_refactored.xml");
    }

    public void createXMLFile(String campus, String term, String year) {
        try {
            XMLBuilder xmlDocument = buildXML(campus, term, year);
            writeXML(xmlDocument);
        } catch (ParserConfigurationException | SQLException | FileNotFoundException | TransformerException e) {
            e.printStackTrace();
        }
    }
}
