package toXmlParser.sessionsetup;

import com.jamesmurty.utils.XMLBuilder;
import parserUtility.ParserUtility;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class AcademicSessionSetup {


    private ParserUtility utility;
    private Connection connection;

    private MainData mainData;

    private ResultSet resultSetDepartments;
    ResultSet resultSetTimePatterns;
    private ResultSet resultSetSubjectAreas;

    private TimeDatePatterns timeDatePatterns;
    private ExaminationPeriods examinationPeriods;

    private String sessionID;

    private XMLBuilder xmlSessionSetup;

    public AcademicSessionSetup(String sessionID) throws SQLException {

        String queryDepartments = "SELECT * FROM INSTITUDID";
        String queryTimePatters = "SELECT * FROM TIME_PATTERN";
        String queryAreas = "SELECT * FROM SUBJECT_AREAS_TTU";

        this.sessionID = sessionID;

        utility = new ParserUtility();
        connection = utility.connectToDatabase();

        this.mainData = new MainData(this);
        this.timeDatePatterns = new TimeDatePatterns(this);
        this.examinationPeriods = new ExaminationPeriods(this);


        resultSetDepartments = utility.queryDataFromDatabase(queryDepartments, getNewStatement());
        resultSetTimePatterns = utility.queryDataFromDatabase(queryTimePatters, getNewStatement());
        resultSetSubjectAreas = utility.queryDataFromDatabase(queryAreas, getNewStatement());

    }

    String[] FINAL_EXAM_TIMES = new String[]{"1000", "1200", "1400", "1600", "1800"};

    public XMLBuilder buildXML(String campus, String term, String year) {

        try {
            createSessionSetupElementBuilder(campus, term, year);

            xmlSessionSetup.importXMLBuilder(mainData.buildXML());

            XMLBuilder departments = xmlSessionSetup.element("departments");
            while (resultSetDepartments.next()) {
                String code = resultSetDepartments.getString("EXTERNAL_ID");
                String abbreviation = resultSetDepartments.getString("ABBV");
                String name = resultSetDepartments.getString("NIMETUS");
//                String externalId = resultSetDepartments.getString("EXTERNALID");

                XMLBuilder department = departments.element("department")
                        .attribute("code", code)
//                        .attribute("externalId", externalId)
                        .attribute("abbreviation", abbreviation)
                        .attribute("name", name);

                department.element("eventManagement")
                        .attribute("enabled", "true");

                department.element("required")
                        .attribute("time", "false")
                        .attribute("room", "false")
                        .attribute("distribution", "false");
            }


            //ADD SUBJECT AREAS

            XMLBuilder subjectAreas = xmlSessionSetup.element("subjectAreas");
            while (resultSetSubjectAreas.next()) {

                String abbreviation = resultSetSubjectAreas.getString("abbv");
                String title = resultSetSubjectAreas.getString("title");
                String department = resultSetSubjectAreas.getString("department_id");

                subjectAreas.element("subjectArea")
                        .attribute("abbreviation", abbreviation)
                        .attribute("title", title)
                        .attribute("department", department);
            }


            //ADD TIME/DATE PATTERNS/
            xmlSessionSetup.importXMLBuilder(timeDatePatterns.buildTimePatterns());
            xmlSessionSetup.importXMLBuilder(timeDatePatterns.buildDatePatterns());

            // ADD EXAMINATION PERIODS
            xmlSessionSetup.importXMLBuilder(examinationPeriods.buildExaminationPeriods());

        } catch (ParserConfigurationException | SQLException e) {
            e.printStackTrace();
        }
        return xmlSessionSetup;
    }

    private void createSessionSetupElementBuilder(String campus, String term, String year) throws ParserConfigurationException {
        xmlSessionSetup = XMLBuilder.create(("sessionSetup"))
                .attribute("term", term)
                .attribute("year", year)
                .attribute("campus", campus)
                .attribute("dateFormat", "yyyy/M/d")
                .attribute("created", "Fri Jun 23 15:21:28 CEST 2117");

    }

    private Statement getNewStatement() throws SQLException {
        return utility.createStatement(connection);
    }

    private void writeXML(XMLBuilder xmlBuilder) throws FileNotFoundException, TransformerException {
        utility.writeToXMLFile(xmlBuilder, "sessionSetup.xml");
    }


    public void createXMLFile(String campus, String term, String year) {
        try {
            writeXML(buildXML(campus, term, year));
        } catch (FileNotFoundException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public String getDateInFormat(int day, int week) {
        String strings1 = null;
        try {
            strings1 = getResultSetDayAndWeek(day, week).getString("kuupaev").substring(0, 10).replaceAll("-", "/");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return strings1;
    }

    public ResultSet getResultSetDayAndWeek(int day, int week) throws SQLException {

        return utility.queryDataFromDatabase("SELECT kuupaev FROM session_ajad" +
                " WHERE fk_tunn_sessioon_id = " + sessionID +
                " AND paev = " + day +
                " AND nadal = " + week, getNewStatement());
    }


}
