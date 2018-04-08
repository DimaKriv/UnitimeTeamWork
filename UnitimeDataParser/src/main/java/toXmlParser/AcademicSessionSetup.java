package toXmlParser;

import com.jamesmurty.utils.XMLBuilder;
import parserUtility.ParserUtility;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;


public class AcademicSessionSetup {


    private final String QUERY_SQL_TIME_PATTERNS = "SELECT * FROM TIME_PATTERN";
    private final ResultSet QUERY_TIME_PATTERNS_RESULT_SET = ParserUtility.queryDataFromDatabase(QUERY_SQL_TIME_PATTERNS);


    public AcademicSessionSetup() throws SQLException {
    }

    public void buildXML() {
        try {
            XMLBuilder xmlSessionSetup;

            xmlSessionSetup =
                    XMLBuilder.create(("sessionSetup"));

            //here is some space for @arveske


            //ADD DATE PATTERNS

            //ADDING DATE PATTERNS

            XMLBuilder datePatterns = xmlSessionSetup.element("datePatterns");

            //all weeks separately
            for (int i = 0; i < 15; i++) {
                datePatterns.element("datePattern")
                        .attribute("name", "week " + Integer.toString(i + 1))
                        .attribute("type", "Standard")
                        .attribute("visible", "true")
                        .attribute("default", "true")

                        .element("dates")
                        .attribute("fromDate", getDateInFormat(0, i))
                        .attribute("toDate", getDateInFormat(4, i));
            }

            //all weeks together
            XMLBuilder allWeeksPattern = datePatterns.element("datePattern")
                    .attribute("name", "all weeks")
                    .attribute("type", "Standard")
                    .attribute("visible", "true")
                    .attribute("default", "true");
            for (int i = 0; i < 15; i++) {
                allWeeksPattern.element("dates")
                        .attribute("fromDate", getDateInFormat(0, i))
                        .attribute("toDate", getDateInFormat(4, i));
            }


            //odd weeks
            XMLBuilder oddWeeks = datePatterns.element("datePattern")
                    .attribute("name", "odd weeks")
                    .attribute("type", "Standard")
                    .attribute("visible", "true")
                    .attribute("default", "true");
            for (int i = 0; i < 15; i++) {
                if ((i + 1) % 2 != 0) {
                    oddWeeks.element("dates")
                            .attribute("fromDate", getDateInFormat(0, i))
                            .attribute("toDate", getDateInFormat(4, i));
                }
            }

            //even weeks
            XMLBuilder evenWeeks = datePatterns.element("datePattern")
                    .attribute("name", "even weeks")
                    .attribute("type", "Standard")
                    .attribute("visible", "true")
                    .attribute("default", "true");
            for (int i = 0; i < 15; i++) {
                if ((i + 1) % 2 == 0) {
                    evenWeeks.element("dates")
                            .attribute("fromDate", getDateInFormat(0, i))
                            .attribute("toDate", getDateInFormat(4, i));
                }
            }

            //from 1 to 8 weeks
            XMLBuilder weeksFromOneToEight = datePatterns.element("datePattern")
                    .attribute("name", "weeks 1-8")
                    .attribute("type", "Standard")
                    .attribute("visible", "true")
                    .attribute("default", "true");
            for (int i = 0; i < 7; i++) {
                weeksFromOneToEight.element("dates")
                        .attribute("fromDate", getDateInFormat(0, i))
                        .attribute("toDate", getDateInFormat(4, i));

            }


            //from 9 to 16 weeks
            XMLBuilder weeksFromEightToSixteen = datePatterns.element("datePattern")
                    .attribute("name", "weeks 1-8")
                    .attribute("type", "Standard")
                    .attribute("visible", "true")
                    .attribute("default", "true");
            for (int i = 8; i < 15; i++) {
                weeksFromEightToSixteen.element("dates")
                        .attribute("fromDate", getDateInFormat(0, i))
                        .attribute("toDate", getDateInFormat(4, i));

            }


            //ADD TIME PATTERNS

            XMLBuilder timePatterns = xmlSessionSetup.element("timePatterns");


            while (QUERY_TIME_PATTERNS_RESULT_SET.next()) {


                String name = QUERY_TIME_PATTERNS_RESULT_SET.getString("name");
                String nbrMeetings = QUERY_TIME_PATTERNS_RESULT_SET.getString("nbr_meetings");
                String minsPerMeeting = QUERY_TIME_PATTERNS_RESULT_SET.getString("mins_per_meetings");
                String type = QUERY_TIME_PATTERNS_RESULT_SET.getString("type");
                String visible = QUERY_TIME_PATTERNS_RESULT_SET.getString("visible");
                String nbrSlotsPerMeeting = QUERY_TIME_PATTERNS_RESULT_SET.getString("nbr_slots_per_meeting");
                String breakTime = QUERY_TIME_PATTERNS_RESULT_SET.getString("break_time");

                String[] timesTimePattern = QUERY_TIME_PATTERNS_RESULT_SET.getString("time").split(" ");
                String[] daysTimePattern = QUERY_TIME_PATTERNS_RESULT_SET.getString("days").split(" ");


                XMLBuilder timePattern = timePatterns.element("timePattern")
                        .attribute("name", name)
                        .attribute("nbrMeetings", nbrMeetings)
                        .attribute("minsPerMeeting", minsPerMeeting)
                        .attribute("type", type)
                        .attribute("visible", visible)
                        .attribute("nbrSlotsPerMeeting", nbrSlotsPerMeeting)
                        .attribute("breakTime", breakTime);


                for (int i = 0; i < daysTimePattern.length; i++) {
                    timePattern.element("days")
                            .attribute("code", daysTimePattern[i]);
                }

                for (int i = 0; i < timesTimePattern.length; i++) {
                    timePattern.element("time")
                            .attribute("start", timesTimePattern[i]);
                }


            }


            new File("XMLFiles").mkdirs();  // create XMLFiles directory


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


    private String getDateInFormat(int day, int week) {
        String[] strings = getResultSetDayAndWeek(day, week).toString().substring(0, 9).split("-");
        return strings[0] + "/" + strings[1] + "/" + strings[2];
    }


    private ResultSet getResultSetDayAndWeek(int day, int week) {

        return ParserUtility.queryDataFromDatabase("SELECT kuupaev FROM session_ajad" +
                "WHERE fk_tunn_session_id = '1123'" +
                " AND paev = '" + Integer.toString(day) +
                "' AND nadal='" + Integer.toString(week) + "'");
    }


    private ResultSet getResultSetDatesBySessionId() {

        return ParserUtility.queryDataFromDatabase(getSQLQueryDatesBySessionId());
    }

    //1123 - 2018 spring semester default
    private String getSQLQueryDatesBySessionId() {
        return "SELECT * FROM session_ajad" +
                "WHERE fk_tunn_session_id = '1123'";
    }


}


//  work later with parameters



//    private ResultSet getQueryResultSetDatesBySessionId(int sessionId) {
//        return ParserUtility.queryDataFromDatabase(getSQLQueryDatesBySessionId(sessionId));
//    }
//
//    private String getSQLQueryDatesBySessionId(int sessionId) {
//        return "SELECT * FROM TUNNIPLAAN.SESSIOON_AJAD" +
//                "WHERE fk_tunn_sessioon_id = '" + Integer.toString(sessionId) + "'";
//    }
