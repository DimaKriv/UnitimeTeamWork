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
        querySql = "SELECT * FROM CURRICULA";
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

        int classificationEnrollmentNumber = 0;
        String previousCurriculumName = "undefined";
        int previousSemester = 0;
        int curriculumNumber = 0;
        int choiceSubjectCounter = 0;

        while (queryResultSet.next()) {

            String currentCurriculumName = queryResultSet.getString("peaeriala_nimetus");
            int currentSemester = Integer.parseInt(queryResultSet.getString("semester_number"));
            String subjectName = queryResultSet.getString("ainekood");
            String subjectType = queryResultSet.getString("aine_tyyp");

            if (!previousCurriculumName.equals(currentCurriculumName)) {
                classificationEnrollmentNumber++;
                curriculumNumber++;
                if (curriculumNumber > 1) {
                    xmlBuilder = xmlBuilder.up().up();
                }
                xmlBuilder =  xmlBuilder.element("curriculum")
                        .attribute("name", currentCurriculumName)
                        .element("academicArea")
                        .attribute("abbreviation", "TTUM")
                        .up();
                previousCurriculumName = currentCurriculumName;
                choiceSubjectCounter = 0;
            }
            if (previousSemester != currentSemester) {
                if (currentSemester > 1) {
                    xmlBuilder = xmlBuilder.up();
                }
                xmlBuilder =  xmlBuilder.element("classification")
                        .attribute("enrollment", String.valueOf(classificationEnrollmentNumber))
                        .element("academicClassification")
                        .attribute("code", String.valueOf(currentSemester))
                        .up();
                previousSemester = currentSemester;
                choiceSubjectCounter++;
            }
            if (previousCurriculumName.equals(currentCurriculumName)) {
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

        }

        return xmlBuilder;
    }

    public void writeXML(XMLBuilder xmlBuilder) throws FileNotFoundException, TransformerException {
        utility.writeToXMLFile(xmlBuilder, "curricula.xml");
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
