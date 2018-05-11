package toXmlParser.sessionsetup;

import org.junit.Before;

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