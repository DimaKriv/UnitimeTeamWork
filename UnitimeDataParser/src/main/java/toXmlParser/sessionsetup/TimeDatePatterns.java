package toXmlParser.sessionsetup;

import com.jamesmurty.utils.XMLBuilder;

import java.sql.SQLException;

/**
 * Created by lll on 11-Apr-18.
 */
public class TimeDatePatterns {


    AcademicSessionSetup academicSessionSetup;

    public TimeDatePatterns(AcademicSessionSetup academicSessionSetup) {
        this.academicSessionSetup = academicSessionSetup;
    }


    void buildTimePatternsXML() {

        XMLBuilder timePatterns = academicSessionSetup.xmlSessionSetup.element("timePatterns");


        try {
            while (academicSessionSetup.QUERY_TIME_PATTERNS_RESULT_SET.next()) {


                String name = academicSessionSetup.QUERY_TIME_PATTERNS_RESULT_SET.getString("name");
                String nbrMeetings = academicSessionSetup.QUERY_TIME_PATTERNS_RESULT_SET.getString("nbr_meetings");
                String minsPerMeeting = academicSessionSetup.QUERY_TIME_PATTERNS_RESULT_SET.getString("mins_per_meetings");
                String type = academicSessionSetup.QUERY_TIME_PATTERNS_RESULT_SET.getString("type");
                String visible = academicSessionSetup.QUERY_TIME_PATTERNS_RESULT_SET.getString("visible");
                String nbrSlotsPerMeeting = academicSessionSetup.QUERY_TIME_PATTERNS_RESULT_SET.getString("nbr_slots_per_meeting");
                String breakTime = academicSessionSetup.QUERY_TIME_PATTERNS_RESULT_SET.getString("break_time");

                String[] timesTimePattern = academicSessionSetup.QUERY_TIME_PATTERNS_RESULT_SET.getString("time").split("\\s+");
                String[] daysTimePattern = academicSessionSetup.QUERY_TIME_PATTERNS_RESULT_SET.getString("days").split("\\s+");


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
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    void buildDatePatterns() {
        XMLBuilder datePatterns = academicSessionSetup.xmlSessionSetup.element("datePatterns");

        //all weeks separately
        for (int i = 0; i < 16; i++) {
            datePatterns.element("datePattern")
                    .attribute("name", "week " + Integer.toString(i + 1))
                    .attribute("type", "Standard")
                    .attribute("visible", "true")
                    .attribute("default", "false")

                    .element("dates")
                    .attribute("fromDate", academicSessionSetup.getDateInFormat(0, i))
                    .attribute("toDate", academicSessionSetup.getDateInFormat(4, i));
        }

        //all weeks together
        XMLBuilder allWeeksPattern = datePatterns.element("datePattern")
                .attribute("name", "all weeks")
                .attribute("type", "Standard")
                .attribute("visible", "true")
                .attribute("default", "false");
        for (int i = 0; i < 16; i++) {
            allWeeksPattern.element("dates")
                    .attribute("fromDate", academicSessionSetup.getDateInFormat(0, i))
                    .attribute("toDate", academicSessionSetup.getDateInFormat(4, i));
        }

        //all weeks together ALTERNATIVE
//        XMLBuilder allWeeksPattern = datePatterns.element("datePattern")
//                .attribute("name", "all weeks")
//                .attribute("type", "Standard")
//                .attribute("visible", "true")
//                .attribute("default", "false");
//        for (int i = 0; i < 16; i++) {
//            allWeeksPattern.element("datePattern")
//                    .attribute("name", "week " + (i+1));
//        }


        //odd weeks
        XMLBuilder oddWeeks = datePatterns.element("datePattern")
                .attribute("name", "odd weeks")
                .attribute("type", "Standard")
                .attribute("visible", "true")
                .attribute("default", "false");
        for (int i = 0; i < 16; i++) {
            if ((i + 1) % 2 != 0) {
                oddWeeks.element("dates")
                        .attribute("fromDate", academicSessionSetup.getDateInFormat(0, i))
                        .attribute("toDate", academicSessionSetup.getDateInFormat(4, i));
            }
        }

        //TEST LATER


//        //even weeks
        XMLBuilder evenWeeks = datePatterns.element("datePattern")
                .attribute("name", "even weeks")
                .attribute("type", "Standard")
                .attribute("visible", "true")
                .attribute("default", "true");
        for (int i = 0; i < 16; i++) {
            if ((i + 1) % 2 == 0) {
                evenWeeks.element("dates")
                        .attribute("fromDate", academicSessionSetup.getDateInFormat(0, i))
                        .attribute("toDate", academicSessionSetup.getDateInFormat(4, i));
            }
        }

//        //from 1 to 8 weeks
        XMLBuilder weeksFromOneToEight = datePatterns.element("datePattern")
                .attribute("name", "weeks 1-8")
                .attribute("type", "Standard")
                .attribute("visible", "true")
                .attribute("default", "true");
        for (int i = 0; i < 9; i++) {
            weeksFromOneToEight.element("dates")
                    .attribute("fromDate", academicSessionSetup.getDateInFormat(0, i))
                    .attribute("toDate", academicSessionSetup.getDateInFormat(4, i));

        }


        //from 9 to 16 weeks
        XMLBuilder weeksFromEightToSixteen = datePatterns.element("datePattern")
                .attribute("name", "weeks 1-8")
                .attribute("type", "Standard")
                .attribute("visible", "true")
                .attribute("default", "true");
        for (int i = 9; i < 16; i++) {
            weeksFromEightToSixteen.element("dates")
                    .attribute("fromDate", academicSessionSetup.getDateInFormat(0, i))
                    .attribute("toDate", academicSessionSetup.getDateInFormat(4, i));

        }
    }

}
