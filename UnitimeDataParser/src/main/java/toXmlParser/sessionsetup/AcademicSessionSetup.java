package toXmlParser.sessionsetup;

import com.jamesmurty.utils.XMLBuilder;
import parserUtility.ParserUtility;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;


public class AcademicSessionSetup {

    TimeDatePatterns timeDatePatterns;
    ExaminationPeriods examinationPeriods;


    public AcademicSessionSetup() {
        this.timeDatePatterns = new TimeDatePatterns(this);
        this.examinationPeriods = new ExaminationPeriods(this);
    }


    private final String QUERY_SQL_TUNN_AINE = "SELECT * FROM TUNN_AINE";
    private final ResultSet QUERY_TUNN_AINE_RESULT_SET = ParserUtility.queryDataFromDatabase(QUERY_SQL_TUNN_AINE);

    private final String QUERY_SQL_INSITUDID = "SELECT * FROM INSTITUDID";
    private final ResultSet QUERY_INSTITUDID_RESULT_SET = ParserUtility.queryDataFromDatabase(QUERY_SQL_INSITUDID);


    final String QUERY_SQL_TIME_PATTERNS = "SELECT * FROM TIME_PATTERN";
    final ResultSet QUERY_TIME_PATTERNS_RESULT_SET = ParserUtility.queryDataFromDatabase(QUERY_SQL_TIME_PATTERNS);

    final String QUERY_SQL_SUBJECT_AREAS = "SELECT * FROM SUBJECT_AREAS_TTU";
    final ResultSet QUERY_SUBJECT_AREAS_SET = ParserUtility.queryDataFromDatabase(QUERY_SQL_SUBJECT_AREAS);

    final String[] FINAL_EXAM_TIMES = new String[]{"1000", "1200", "1400", "1600", "1800"};
    XMLBuilder xmlSessionSetup;


    public void buildXML() {
        try {

            xmlSessionSetup =
                    XMLBuilder.create(("sessionSetup"))
                            .attribute("term", "Fal")
                            .attribute("year", "2018")
                            .attribute("campus", "TTU")
                            .attribute("dateFormat", "yyyy/M/d");

            XMLBuilder session = xmlSessionSetup.element("session")
                    .attribute("startDate", getDateInFormat(0, 0))
                    .attribute("endDate", getDateInFormat(4, 18))
                    .attribute("classEndDate", getDateInFormat(4, 15))
                    .attribute("examStartDate", getDateInFormat(0, 16))
                    .attribute("eventStartDate", getDateInFormat(0, -1))
                    .attribute("eventEndDate", getDateInFormat(1, -1));


            //holidays/managers are not important
            XMLBuilder holidays = session.element("holidays");
            XMLBuilder managers = xmlSessionSetup.element("managers")
                    .attribute("incremental", "true");


            XMLBuilder departments = xmlSessionSetup.element("Departments");

            while (QUERY_INSTITUDID_RESULT_SET.next()) {
                String code = QUERY_INSTITUDID_RESULT_SET.getString("DEPTCODE");
                String abbreviation = QUERY_INSTITUDID_RESULT_SET.getString("ABBREVIATION");
                String name = QUERY_INSTITUDID_RESULT_SET.getString("NAME");
                String externalId = QUERY_INSTITUDID_RESULT_SET.getString("EXTERNALID");

                XMLBuilder department = departments.element("department")
                        .attribute("code", code)
                        .attribute("externalId", externalId)
                        .attribute("abbreviation", abbreviation)
                        .attribute("name", name);

                department.element("eventManagement")
                        .attribute("enabled", "true");

                department.element("required")
                        .attribute("time", "false")
                        .attribute("room", "false")
                        .attribute("distribution", "false");
            }


            //NEED TO ADD SUBJECT AREAS---NOW DUMMY VALUES

            XMLBuilder subjectAreas = xmlSessionSetup.element("subjectAreas");

//            for (int i = 0; i < 10; i++) {
//                String abbreviation = "dummyAB" + i;//QUERY_TUNN_AINE_RESULT_SET.getString("AINEKOOD");
//                String title = "dummyTitle" + i;//QUERY_TUNN_AINE_RESULT_SET.getString("NIMETUS_EST");
//                String department = "dummyDepartment" + i;//QUERY_TUNN_AINE_RESULT_SET.getString("FK_AINE_ID");
//
//                subjectAreas.element("subjectArea")
//                        .attribute("abbrevation", abbreviation)
//                        .attribute("title", title)
//                        .attribute("department", department);
//            }


            while (QUERY_SUBJECT_AREAS_SET.next()) {

                String abbreviation = QUERY_SUBJECT_AREAS_SET.getString("abbv");
                String title = QUERY_SUBJECT_AREAS_SET.getString("title");
                String department = QUERY_SUBJECT_AREAS_SET.getString("department_id");

                subjectAreas.element("subjectArea")
                        .attribute("abbrevation", abbreviation)
                        .attribute("title", title)
                        .attribute("department", department);
            }

            //not filled. needed managers done
//            XMLBuilder solverGroups = xmlSessionSetup.element("solverGroups");
//            solverGroups.elem("solverGroups")
//                    .attribute("abbreviation", "DummyName")
//                    .attribute("name","dummyName");

            //ADD TIME/DATE PATTERNS/
            timeDatePatterns.buildTimePatternsXML();
            timeDatePatterns.buildDatePatterns();
            // ADD EXAMINATION PERIODS
            examinationPeriods.buildExaminationPeriods();


            //ADD ACADEMIC CLASSIFICATIONS. Only bachelor and magistracy yet.
            XMLBuilder academicClassifications = xmlSessionSetup.element("academicClassifications");


            academicClassifications.element("academicClassification")
                    .attribute("externalId", "id1")
                    .attribute("code", "01")
                    .attribute("name", "bachelor");

            academicClassifications.element("academicClassification")
                    .attribute("externalId", "id2")
                    .attribute("code", "02")
                    .attribute("name", "magistracy");


            //ADD POSMAJORRS/POSMINORS/STUDENT GROUPS/STUDENTS ACCOMODATIONS. Not filled yet. Seems, not important...
            xmlSessionSetup.element("posMajors");
            xmlSessionSetup.element("posMinors");
            xmlSessionSetup.element("studentGroups");
            xmlSessionSetup.element("studentAccomodations");


            new File("XMLFiles").mkdirs();


            PrintWriter writer = new PrintWriter(new FileOutputStream("XMLFiles/academicSessionSetup.xml"));
            Properties outputProperties = new Properties();
            outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            outputProperties.put(javax.xml.transform.OutputKeys.INDENT, "yes");
            outputProperties.put("{http://xml.apache.org/xslt}indent-amount", "2");
            xmlSessionSetup.toWriter(writer, outputProperties);


        } catch (ParserConfigurationException | SQLException | FileNotFoundException | TransformerException e) {
            e.printStackTrace();
        }
    }


    String getDateInFormat(int day, int week) {
        String strings1 = null;
        try {
            strings1 = getResultSetDayAndWeek(day, week).getString("kuupaev").substring(0, 10).replaceAll("-", "/");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return strings1;
    }


    ResultSet getResultSetDayAndWeek(int day, int week) {

        return ParserUtility.queryDataFromDatabase("SELECT kuupaev FROM session_ajad" +
                " WHERE fk_tunn_sessioon_id = 1123" +
                " AND paev = " + day +
                " AND nadal = " + week);
    }

    ResultSet getResultSetDatesBySessioonId() {

        return ParserUtility.queryDataFromDatabase(getSQLQueryDatesBySessioonId());
    }

    //1123 - 2018 spring semester default
    String getSQLQueryDatesBySessioonId() {
        return "SELECT * FROM sessioon_ajad" +
                "WHERE fk_tunn_sessioon_id = '1123'";
    }


}
