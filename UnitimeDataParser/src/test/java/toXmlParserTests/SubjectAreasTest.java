package toXmlParserTests;

import com.jamesmurty.utils.XMLBuilder;
import org.junit.Before;
import org.junit.Test;
import toXmlParser.SubjectAreas;

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
                .attribute("externalId", "SA0000000000000000005")
                .attribute("abbreviation", "INFR")
                .attribute("title", "Informaatika")
                .attribute("department", "1001");

        when(queryResultSetMock.next()).thenReturn(true).thenReturn(false);
        when(queryResultSetMock.getString("EXTERNAL_ID")).thenReturn("SA0000000000000000005");
        when(queryResultSetMock.getString("ABBV")).thenReturn("INFR");
        when(queryResultSetMock.getString("TITLE")).thenReturn("Informaatika");

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
                .attribute("externalId", "SA0000000000000000005")
                .attribute("abbreviation", "INFR")
                .attribute("title", "Informaatika")
                .attribute("department", "1001");

        expectedBuilder.element("subjectArea")
                .attribute("externalId", "SA0000000000000000006")
                .attribute("abbreviation", "KUBR")
                .attribute("title", "Küberturbe tehnoloogiad")
                .attribute("department", "1002");

        when(queryResultSetMock.next()).thenReturn(true).thenReturn(true).thenReturn(false);

        when(queryResultSetMock.getString("EXTERNAL_ID"))
                .thenReturn("SA0000000000000000005")
                .thenReturn("SA0000000000000000006");

        when(queryResultSetMock.getString("ABBV"))
                .thenReturn("INFR")
                .thenReturn("KUBR");

        when(queryResultSetMock.getString("TITLE"))
                .thenReturn("Informaatika")
                .thenReturn("Küberturbe tehnoloogiad");

        XMLBuilder actualBuilder = subjectAreas.buildXML("TTU", "Fall", "2018");

        assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }


}