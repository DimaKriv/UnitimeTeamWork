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
                            .attribute("year", "2011")
                            .attribute("campus", "TTUTEST")
                            .attribute("dateFormat", "yyyy/M/d")
                            .attribute("created", "Fri Jun 23 15:21:28 CEST 2117");

            XMLBuilder session = xmlSessionSetup.element("session")
                    .attribute("startDate", getDateInFormat(0, 0))
                    .attribute("endDate", getDateInFormat(4, 18))
                    .attribute("classEndDate", getDateInFormat(4, 15))
                    .attribute("examStartDate", getDateInFormat(0, 16))
                    .attribute("eventStartDate", getDateInFormat(0, -1))
                    .attribute("eventEndDate", getDateInFormat(1, -1));


//            holidays/managers are not important
//            XMLBuilder holidays = session.element("holidays");
//            XMLBuilder holiday = holidays.element("holiday")
//                    .attribute("date", "2118/9/6");

            //add managers
//            XMLBuilder managers = xmlSessionSetup.element("managers")
//                    .attribute("incremental", "true");
//
//            XMLBuilder manager = managers.element("manager")
//                    .attribute("externalId", "7")
//                    .attribute("firstName", "Mister")
//                    .attribute("lastName", "Manager")
//                    .attribute("email", "testManager@test.org");
//
//            XMLBuilder departmentCode = manager.element("department").attr("code", "1001");
//            XMLBuilder role = departmentCode.element("role")
//                    .attribute("reference", "testROLE")
//                    .attribute("primary", "true")
//                    .attribute("emails", "true");


            XMLBuilder departments = xmlSessionSetup.element("departments");

            while (QUERY_INSTITUDID_RESULT_SET.next()) {
                String code = QUERY_INSTITUDID_RESULT_SET.getString("EXTERNAL_ID");
                String abbreviation = QUERY_INSTITUDID_RESULT_SET.getString("ABBV");
                String name = QUERY_INSTITUDID_RESULT_SET.getString("NIMETUS");
//                String externalId = QUERY_INSTITUDID_RESULT_SET.getString("EXTERNALID");

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


            //NEED TO ADD SUBJECT AREAS---NOW DUMMY VALUES

            XMLBuilder subjectAreas = xmlSessionSetup.element("subjectAreas");


            while (QUERY_SUBJECT_AREAS_SET.next()) {

                String abbreviation = QUERY_SUBJECT_AREAS_SET.getString("abbv");
                String title = QUERY_SUBJECT_AREAS_SET.getString("title");
                String department = QUERY_SUBJECT_AREAS_SET.getString("department_id");

                subjectAreas.element("subjectArea")
                        .attribute("abbreviation", abbreviation)
                        .attribute("title", title)
                        .attribute("department", department);
            }


            XMLBuilder solverGroups = xmlSessionSetup.element("solverGroups");
            XMLBuilder solverGroup = solverGroups.element("solverGroup")
                    .attribute("abbreviation", "testSOLVER GROUP")
                    .attribute("name", "Instructional Planning TEST");
            XMLBuilder manager1 = solverGroup.element("manager")
                    .attribute("externalId", "7");
            XMLBuilder department1 = solverGroup.element("department")
                    .attribute("code", "0101");


            //not filled. needed managers done
//            XMLBuilder solverGroups = xmlSessionSetup.element("solverGroups");
//            solverGroups.elem("solverGroups")
//                    .attribute("abbreviation", "DummyName")
//                    .attribute("name","dummyName");

            //ADD TIME/DATE PATTERNS/
            timeDatePatterns.buildDatePatterns();
            timeDatePatterns.buildTimePatternsXML();

            // ADD EXAMINATION PERIODS
            examinationPeriods.buildExaminationPeriods();

//
//            XMLBuilder academicAreas = xmlSessionSetup.element("academicAreas");
//            XMLBuilder academicArea = academicAreas.element("academicAreas")
//                    .attribute("externalId", "ATESTA")
//                    .attribute("abbreviation", "ATESTA")
//                    .attribute("title", "SOMEBODY ONCE TOLD ME");


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

//            XMLBuilder posMajors = xmlSessionSetup.element("posMajors");
//            posMajors.element("posMajor")
//                    .attribute("code", "MAJ1")
//                    .attribute("academicArea", "ATESTA")
//                    .attribute("externalId", "MAJ1")
//                    .attribute("name", "somebudy");
//
//            XMLBuilder posMinors = xmlSessionSetup.element("posMinors");
//            posMinors.element("posMinor")
//                    .attribute("code", "MIN1")
//                    .attribute("academicArea", "ATESTA")
//                    .attribute("externalId", "MIN1")
//                    .attribute("name", "olololo");
//
//
//            XMLBuilder studentGroups = xmlSessionSetup.element("studentGroups");
//            studentGroups.element("studentGroup")
//                    .attribute("code", "G1")
//                    .attribute("name", "GROUP1")
//                    .attribute("externalId", "g1");
//
//
//            XMLBuilder studAccom = xmlSessionSetup.element("studentAccomodations");
//            studAccom.element("studentAccomodation")
//                    .attribute("code","EC")
//                    .attribute("name","Ergonomic Chair")
//                    .attribute("externalId","ACC-EC");

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
        System.out.println("Academic session setup XML is successfully created");
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
                " WHERE fk_tunn_sessioon_id = 161" +
                " AND paev = " + day +
                " AND nadal = " + week);
    }

    ResultSet getResultSetDatesBySessioonId() {

        return ParserUtility.queryDataFromDatabase(getSQLQueryDatesBySessioonId());
    }

    //1123 - 2018 spring semester default
    String getSQLQueryDatesBySessioonId() {
        return "SELECT * FROM sessioon_ajad" +
                "WHERE fk_tunn_sessioon_id = '161'";
    }


}
