package toXmlParser;

import com.jamesmurty.utils.XMLBuilder;
import org.junit.Before;
import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CurriculaTest {

    private String queryMock;
    private ResultSet queryResultSetMock;
    private Curricula curricula;

    @Before
    public void setUp() {
        this.queryMock = "";
        this.queryResultSetMock = mock(ResultSet.class);
        this.curricula = new Curricula(queryMock, queryResultSetMock);
    }

    @Test
    public void testXMLBuilderWithGivenCampusAndTermAndYearIsCreatedCorrectly() throws ParserConfigurationException,
            TransformerException {

        XMLBuilder expectedBuilder = XMLBuilder.create("curricula")
                .attribute("campus", "TTU")
                .attribute("term", "Fall")
                .attribute("year", "2018");
        XMLBuilder actualBuilder = curricula.createCurriculaElementBuilder("TTU", "Fall",
                "2018");
        assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }

    @Test
    public void testBuildingXMLWithOneEntryInResultSet() throws ParserConfigurationException, SQLException, TransformerException {
        XMLBuilder expectedBuilder = XMLBuilder.create("curricula")
                .attribute("campus", "TTU")
                .attribute("term", "Fall")
                .attribute("year", "2018");

        XMLBuilder curriculum = expectedBuilder.element("curriculum")
                .attribute("name", "Elektroonika ja telekommunikatsioon");

        curriculum.element("academicArea")
                .attribute("abbreviation", "TTUM");

        XMLBuilder classification = curriculum.element("classification")
                .attribute("enrollment", "1");

        classification.element("academicClassification")
                .attribute("code", "1");

        classification.element("course")
                .attribute("courseNbr", "1")
                .attribute("subject", "ITI0101");


        when(queryResultSetMock.next()).thenReturn(true).thenReturn(false);
        when(queryResultSetMock.getString("peaeriala_nimetus")).thenReturn("Elektroonika ja telekommunikatsioon");
        when(queryResultSetMock.getString("semester_number")).thenReturn("1");
        when(queryResultSetMock.getString("ainekood")).thenReturn("ITI0101");
        when(queryResultSetMock.getString("aine_tyyp")).thenReturn("K");

        XMLBuilder actualBuilder = curricula.buildXML("TTU", "Fall", "2018");
        assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }

    @Test
    public void testBuildingXMLWithOneEntryInResultSetWithGroup() throws ParserConfigurationException, SQLException, TransformerException {
        XMLBuilder expectedBuilder = XMLBuilder.create("curricula")
                .attribute("campus", "TTU")
                .attribute("term", "Fall")
                .attribute("year", "2018");

        XMLBuilder curriculum = expectedBuilder.element("curriculum")
                .attribute("name", "Elektroonika ja telekommunikatsioon");

        curriculum.element("academicArea")
                .attribute("abbreviation", "TTUM");

        XMLBuilder classification = curriculum.element("classification")
                .attribute("enrollment", "1");

        classification.element("academicClassification")
                .attribute("code", "1");

        XMLBuilder course = classification.element("course")
                .attribute("courseNbr", "1")
                .attribute("subject", "ITI0101");

        course.element("group")
                .attribute("id", "1")
                .attribute("type", "OPT");

        when(queryResultSetMock.next()).thenReturn(true).thenReturn(false);
        when(queryResultSetMock.getString("peaeriala_nimetus")).thenReturn("Elektroonika ja telekommunikatsioon");
        when(queryResultSetMock.getString("semester_number")).thenReturn("1");
        when(queryResultSetMock.getString("ainekood")).thenReturn("ITI0101");
        when(queryResultSetMock.getString("aine_tyyp")).thenReturn("V");

        XMLBuilder actualBuilder = curricula.buildXML("TTU", "Fall", "2018");
        assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }

}
