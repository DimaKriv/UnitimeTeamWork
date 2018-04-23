package toXmlParserTests.sessionsetup;


import com.jamesmurty.utils.XMLBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import toXmlParser.sessionsetup.AcademicSessionSetup;
import toXmlParser.sessionsetup.TimeDatePatterns;

import javax.xml.parsers.ParserConfigurationException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TimeDatePatternsTest {

    TimeDatePatterns timeDatePatterns;
    AcademicSessionSetup academicSessionSetup;
    private ResultSet queryResultSetMock;

    @BeforeEach
    void setup() throws SQLException {
        academicSessionSetup = new AcademicSessionSetup("1123");
        queryResultSetMock = mock(ResultSet.class);
        timeDatePatterns = new TimeDatePatterns(academicSessionSetup);


    }

    @Test
    void testBuildTimePattern() throws SQLException, ParserConfigurationException {


        XMLBuilder xmlBuilder = XMLBuilder.create("timePattern")
                .attribute("name", "na")
                .attribute("nbr_meetings", "nb")
                .attribute("mins_per_meetings", "mi")
                .attribute("type", "ty")
                .attribute("visible", "vi")
                .attribute("nbr_slots_per_meeting", "nb")
                .attribute("break_time", "br");


        when(queryResultSetMock.next()).thenReturn(true).thenReturn(false);

        when(queryResultSetMock.getString("name")).thenReturn("na");
        when(queryResultSetMock.getString("nbr_meetings")).thenReturn("nb");
        when(queryResultSetMock.getString("mins_per_meetings")).thenReturn("mi");
        when(queryResultSetMock.getString("type")).thenReturn("ty");
        when(queryResultSetMock.getString("visible")).thenReturn("vi");
        when(queryResultSetMock.getString("nbr_slots_per_meeting")).thenReturn("nb");
        when(queryResultSetMock.getString("break_time")).thenReturn("br");
        timeDatePatterns.buildOneDatePattern(queryResultSetMock);

        assert timeDatePatterns.buildOneDatePattern(queryResultSetMock).equals(xmlBuilder);

    }








}
