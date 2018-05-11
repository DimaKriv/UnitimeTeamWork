package toXmlParser.sessionsetup;

import org.junit.Before;

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