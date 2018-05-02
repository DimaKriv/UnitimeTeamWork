package toXmlParserTests.sessionsetupTests;

import org.junit.Before;
import toXmlParser.sessionsetup.AcademicSessionSetup;
import toXmlParser.sessionsetup.TimeDatePatterns;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class TimeDatePatternsTest {

    private AcademicSessionSetup academicSessionSetup;
    private TimeDatePatterns timeDataPatterns;

    @Before
    public void setup() {
        academicSessionSetup = mock(AcademicSessionSetup.class);
        timeDataPatterns = new TimeDatePatterns(academicSessionSetup);
    }

}