package toXmlParser.sessionsetup;

import com.jamesmurty.utils.XMLBuilder;

import javax.xml.parsers.ParserConfigurationException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class TimeDatePatterns {


    AcademicSessionSetup academicSessionSetup;

    public TimeDatePatterns(AcademicSessionSetup academicSessionSetup) {

        this.academicSessionSetup = academicSessionSetup;

    }


    public XMLBuilder buildOneDatePattern(ResultSet resultSet) throws SQLException, ParserConfigurationException {

        String name = resultSet.getString("name");
        String nbrMeetings = resultSet.getString("nbr_meetings");
        String minsPerMeeting = resultSet.getString("mins_per_meetings");
        String type = resultSet.getString("type");
        String visible = resultSet.getString("visible");
        String nbrSlotsPerMeeting = resultSet.getString("nbr_slots_per_meeting");
        String breakTime = resultSet.getString("break_time");

        XMLBuilder timePattern = XMLBuilder.create("timePattern")
                .attribute("name", name)
                .attribute("nbrMeetings", nbrMeetings)
                .attribute("minsPerMeeting", minsPerMeeting)
                .attribute("type", type)
                .attribute("visible", visible)
                .attribute("nbrSlotsPerMeeting", nbrSlotsPerMeeting)
                .attribute("breakTime", breakTime);

        addDaysAndWeeksToTimePattern(timePattern);

        return timePattern;
    }

    public XMLBuilder buildTimePatterns() throws ParserConfigurationException {
        XMLBuilder timePatterns = XMLBuilder.create("timePatterns");

        try {
            while (academicSessionSetup.resultSetTimePatterns.next()) {
                timePatterns.importXMLBuilder(buildOneDatePattern(academicSessionSetup.resultSetTimePatterns));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return timePatterns;
    }


    public void addDaysAndWeeksToTimePattern(XMLBuilder timePattern) {
        String[] timesTimePattern = new String[0];
        String[] daysTimePattern = new String[0];
        try {
            timesTimePattern = academicSessionSetup.resultSetTimePatterns.getString("time").split("\\s+");
            daysTimePattern = academicSessionSetup.resultSetTimePatterns.getString("days").split("\\s+");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < daysTimePattern.length; i++) {
            timePattern.element("days")
                    .attribute("code", daysTimePattern[i]);
        }

        for (int i = 0; i < timesTimePattern.length; i++) {

            timePattern.element("time")
                    .attribute("start", timesTimePattern[i]);


        }
    }


    public XMLBuilder buildDatePatterns() throws ParserConfigurationException {
        XMLBuilder datePatterns = XMLBuilder.create("datePatterns");
        try {
            for (int i = 0; i < addAllWeeksSeparately().size(); i++) {

                datePatterns.importXMLBuilder(addAllWeeksSeparately().get(i));
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        datePatterns.importXMLBuilder(addAllWeeksTogether());
        datePatterns.importXMLBuilder(addOddWeeks());
        datePatterns.importXMLBuilder(addEvenWeeks());
        datePatterns.importXMLBuilder(addWeeksFromOneToEight());
        datePatterns.importXMLBuilder(addWeeksFromNineToSixteen());
        return datePatterns;

    }

    public ArrayList<XMLBuilder> addAllWeeksSeparately() throws ParserConfigurationException {
        ArrayList<XMLBuilder> xmlBuilders;


        xmlBuilders = new ArrayList<>();

        for (int i = 0; i < 16; i++) {


            XMLBuilder xmlBuilder = XMLBuilder.create("datePattern")
                    .attribute("name", "week " + Integer.toString(i + 1))
                    .attribute("type", "Standard")
                    .attribute("visible", "true")
                    .attribute("default", "false")

                    .element("dates")
                    .attribute("fromDate", academicSessionSetup.getDateInFormat(0, i))
                    .attribute("toDate", academicSessionSetup.getDateInFormat(4, i));
            xmlBuilders.add(xmlBuilder);

        }
        return xmlBuilders;
    }

    public XMLBuilder addAllWeeksTogether() throws ParserConfigurationException {
        XMLBuilder datePattern = XMLBuilder.create("datePattern")
                .attribute("name", "all weeks")
                .attribute("type", "Standard")
                .attribute("visible", "true")
                .attribute("default", "false");
        for (int i = 0; i < 16; i++) {
            datePattern.element("dates")
                    .attribute("fromDate", academicSessionSetup.getDateInFormat(0, i))
                    .attribute("toDate", academicSessionSetup.getDateInFormat(4, i));
        }


        return datePattern;
    }

    public XMLBuilder addOddWeeks() throws ParserConfigurationException {

        XMLBuilder oddWeeks = XMLBuilder.create("datePattern")
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
        return oddWeeks;

    }

    public XMLBuilder addEvenWeeks() throws ParserConfigurationException {

        XMLBuilder evenWeeks = XMLBuilder.create("datePattern")
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
        return evenWeeks;
    }

    public XMLBuilder addWeeksFromOneToEight() throws ParserConfigurationException {

        XMLBuilder weeksFromOneToEight = XMLBuilder.create("datePattern")
                .attribute("name", "weeks 1-8")
                .attribute("type", "Standard")
                .attribute("visible", "true")
                .attribute("default", "true");
        for (int i = 0; i < 9; i++) {
            weeksFromOneToEight.element("dates")
                    .attribute("fromDate", academicSessionSetup.getDateInFormat(0, i))
                    .attribute("toDate", academicSessionSetup.getDateInFormat(4, i));

        }
        return weeksFromOneToEight;
    }

    public XMLBuilder addWeeksFromNineToSixteen() throws ParserConfigurationException {

        XMLBuilder weeksFromEightToSixteen = XMLBuilder.create("datePattern")
                .attribute("name", "weeks 1-8")
                .attribute("type", "Standard")
                .attribute("visible", "true")
                .attribute("default", "true");
        for (int i = 9; i < 16; i++) {
            weeksFromEightToSixteen.element("dates")
                    .attribute("fromDate", academicSessionSetup.getDateInFormat(0, i))
                    .attribute("toDate", academicSessionSetup.getDateInFormat(4, i));

        }
        return weeksFromEightToSixteen;
    }


}
