package toXmlParser.sessionsetup;

import com.jamesmurty.utils.XMLBuilder;
import parserUtility.ParserUtility;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


public class AcademicSessionSetup {


    ParserUtility utility;
    Connection connection;
//    Statement statement;

    MainData mainData;

    ResultSet resultSetDepartments;
    ResultSet resultSetTimePatterns;
    ResultSet resultSetSubjectAreas;

    TimeDatePatterns timeDatePatterns;
    ExaminationPeriods examinationPeriods;

    String sessionID;

    XMLBuilder xmlSessionSetup = XMLBuilder.create(("sessionSetup"))
            .attribute("term", "Fal")
            .attribute("year", "2018")
            .attribute("campus", "TTU_SESSION_TEST_")
            .attribute("dateFormat", "yyyy/M/d")
            .attribute("created", "Fri Jun 23 15:21:28 CEST 2117");


    public AcademicSessionSetup(String sessionID) throws SQLException, ParserConfigurationException {


        this.mainData = new MainData(this);


        String queryDepartments = "SELECT * FROM INSTITUDID";
        String queryTimePatters = "SELECT * FROM TIME_PATTERN";
        String queryAreas = "SELECT * FROM SUBJECT_AREAS_TTU";

        this.sessionID = sessionID;


        utility = new ParserUtility();
        connection = utility.connectToDatabase();
//        statement = utility.createStatement(connection);


        this.timeDatePatterns = new TimeDatePatterns(this);
        this.examinationPeriods = new ExaminationPeriods(this);


        resultSetDepartments = utility.queryDataFromDatabase(queryDepartments, getNewStatement());
        resultSetTimePatterns = utility.queryDataFromDatabase(queryTimePatters, getNewStatement());
        resultSetSubjectAreas = utility.queryDataFromDatabase(queryAreas, getNewStatement());


    }


    public Statement getNewStatement() throws SQLException {
        return utility.createStatement(connection);
    }


    String[] FINAL_EXAM_TIMES = new String[]{"1000", "1200", "1400", "1600", "1800"};


    public void buildXML() {
        try {
            mainData.buildXML();


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
            examinationPeriods.buildExaminationPeriods();

            new File("XMLFiles").mkdirs();

            PrintWriter writer = new PrintWriter(new FileOutputStream("XMLFiles/academicSessionSetup.xml"));
            Properties outputProperties = new Properties();
            outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            outputProperties.put(javax.xml.transform.OutputKeys.INDENT, "yes");
            outputProperties.put("{http://xml.apache.org/xslt}indent-amount", "2");
            xmlSessionSetup.toWriter(writer, outputProperties);
            System.out.println("Academic session setup XML is successfully created");


        } catch (ParserConfigurationException | SQLException | FileNotFoundException | TransformerException e) {
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
