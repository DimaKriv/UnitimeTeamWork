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

public class SubjectAreasTest {

    private String queryMock;
    private ResultSet queryResultSetMock;
    private SubjectAreas subjectAreas;

    @Before
    public void setUp() {
        this.queryMock = "";
        this.queryResultSetMock = mock(ResultSet.class);
        this.subjectAreas = new SubjectAreas(queryMock, queryResultSetMock);
    }

    @Test
    public void testXMLBuilderWithGivenCampusAndTermAndYearIsCreatedCorrectly() throws ParserConfigurationException,
            TransformerException {

        XMLBuilder expectedBuilder = XMLBuilder.create("subjectAreas")
                .attribute("campus", "TTU")
                .attribute("term", "Fall")
                .attribute("year", "2018");
        XMLBuilder actualBuilder = subjectAreas.createSubjectAreasElementBuilder("TTU", "Fall",
                "2018");
        assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }

    @Test
    public void testBuildingXMLWithOneEntryInResultSet() throws ParserConfigurationException, SQLException,
            TransformerException {

        XMLBuilder expectedBuilder = XMLBuilder.create("subjectAreas")
                .attribute("campus", "TTU")
                .attribute("term", "Fall")
                .attribute("year", "2018");

        expectedBuilder.element("subjectArea")
                .attribute("externalId", "1000")
                .attribute("abbreviation", "AAR0020")
                .attribute("title", "Automaatjuhtimise alused")
                .attribute("department", "1007");

        when(queryResultSetMock.next()).thenReturn(true).thenReturn(false);
        when(queryResultSetMock.getString("ainekood")).thenReturn("AAR0020");
        when(queryResultSetMock.getString("nimetus")).thenReturn("Automaatjuhtimise alused");
        when(queryResultSetMock.getString("department_id")).thenReturn("1007");

        XMLBuilder actualBuilder = subjectAreas.buildXML("TTU", "Fall", "2018");

        assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }

    @Test
    public void testBuildingXMLWithTwoEntryInResultSet() throws SQLException, ParserConfigurationException,
            TransformerException {

        XMLBuilder expectedBuilder = XMLBuilder.create("subjectAreas")
                .attribute("campus", "TTU")
                .attribute("term", "Fall")
                .attribute("year", "2018");

        expectedBuilder.element("subjectArea")
                .attribute("externalId", "1000")
                .attribute("abbreviation", "AAR0020")
                .attribute("title", "Automaatjuhtimise alused")
                .attribute("department", "1007");

        expectedBuilder.element("subjectArea")
                .attribute("externalId", "1001")
                .attribute("abbreviation", "AAR0030")
                .attribute("title", "Sissejuhatus robotitehnikasse")
                .attribute("department", "1007");

        when(queryResultSetMock.next()).thenReturn(true).thenReturn(true).thenReturn(false);

        when(queryResultSetMock.getString("ainekood"))
                .thenReturn("AAR0020")
                .thenReturn("AAR0030");

        when(queryResultSetMock.getString("nimetus"))
                .thenReturn("Automaatjuhtimise alused")
                .thenReturn("Sissejuhatus robotitehnikasse");

        when(queryResultSetMock.getString("department_id"))
                .thenReturn("1007")
                .thenReturn("1007");

        XMLBuilder actualBuilder = subjectAreas.buildXML("TTU", "Fall", "2018");

        assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }


}