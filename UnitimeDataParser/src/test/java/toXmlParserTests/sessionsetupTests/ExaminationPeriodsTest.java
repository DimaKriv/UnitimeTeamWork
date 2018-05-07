package toXmlParserTests.sessionsetupTests;

import com.jamesmurty.utils.XMLBuilder;
import org.junit.Before;
import org.junit.Test;
import toXmlParser.sessionsetup.AcademicSessionSetup;
import toXmlParser.sessionsetup.ExaminationPeriods;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.sql.ResultSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ExaminationPeriodsTest {
    
    private AcademicSessionSetup academicSessionSetup;
    private ExaminationPeriods examinationPeriods;

    @Before
    public void setup() {
        academicSessionSetup = mock(AcademicSessionSetup.class);
        examinationPeriods = new ExaminationPeriods(academicSessionSetup);
    }

    /*@Test
    public void testExaminationPeriodBuilder() throws ParserConfigurationException, TransformerException {
        XMLBuilder expectedPeriods = XMLBuilder.create("examinationPeriods");
        expectedPeriods.element("periods").attribute("type", "final");

        XMLBuilder actualperiods = examinationPeriods.buildExaminationPeriods();
        assertEquals(expectedPeriods.asString(), actualperiods.asString());
    }*/

}