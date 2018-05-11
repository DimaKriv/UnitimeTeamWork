package toXmlParser.sessionsetup;

import com.jamesmurty.utils.XMLBuilder;
import org.junit.Before;
import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MainDataTest {

    private AcademicSessionSetup academicSessionSetup;
    private MainData mainData;

    @Before
    public void setup() {
        academicSessionSetup = mock(AcademicSessionSetup.class);
        mainData = new MainData(academicSessionSetup);
    }

    @Test
    public void testBuildXmlMainDataRightFormat() throws ParserConfigurationException, TransformerException {
        XMLBuilder expectedSession = XMLBuilder.create("session")
                .attribute("startDate", "2018/1/29")
                .attribute("endDate", "2018/6/8")
                .attribute("classEndDate", "2018/5/18")
                .attribute("examStartDate", "2018/5/21")
                .attribute("eventStartDate", "2018/1/22")
                .attribute("eventEndDate", "2018/1/23");

        when(academicSessionSetup.getDateInFormat(0, 0)).thenReturn("2018/1/29");
        when(academicSessionSetup.getDateInFormat(4, 18)).thenReturn("2018/6/8");
        when(academicSessionSetup.getDateInFormat(4, 15)).thenReturn("2018/5/18");
        when(academicSessionSetup.getDateInFormat(0, 16)).thenReturn("2018/5/21");
        when(academicSessionSetup.getDateInFormat(0, -1)).thenReturn("2018/1/22");
        when(academicSessionSetup.getDateInFormat(1, -1)).thenReturn("2018/1/23");

        XMLBuilder actualSession = mainData.buildXML();
        assertEquals(expectedSession.asString(), actualSession.asString());
    }
}