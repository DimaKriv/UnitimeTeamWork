package toXmlParserTests;

import com.jamesmurty.utils.XMLBuilder;
import org.junit.Before;
import org.junit.Test;
import toXmlParser.Departments;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DepartmentsTest {

    private Departments departments;
    private String queryMock;
    private ResultSet queryResultSetMock;

    @Before
    public void setup() {
        queryMock = "";
        queryResultSetMock = mock(ResultSet.class);
        departments = new Departments(queryMock, queryResultSetMock);
    }

    @Test
    public void testXMLBuilderWithGivenCampusAndTermAndYearIsCreatedCorrectly() throws ParserConfigurationException, TransformerException {
        XMLBuilder expectedBuilder = XMLBuilder.create("departments")
                .attribute("campus", "TTU")
                .attribute("term", "Fall")
                .attribute("year", "2018");
        XMLBuilder actualBuilder = departments.createDepartmentsElementBuilder("TTU", "Fall", "2018");
        assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }

    @Test
    public void testBuildingXMLWithOneEntryInResultSet() throws ParserConfigurationException, SQLException, TransformerException {
        XMLBuilder expectedBuilder = XMLBuilder.create("departments")
                .attribute("campus", "TTU")
                .attribute("term", "Fall")
                .attribute("year", "2018");
        expectedBuilder.element("department")
                .attribute("abbreviation", "Abv")
                .attribute("name", "Name")
                .attribute("deptCode", "Code");

        when(queryResultSetMock.next()).thenReturn(true).thenReturn(false);
        when(queryResultSetMock.getString("ABBV")).thenReturn("Abv");
        when(queryResultSetMock.getString("NIMETUS")).thenReturn("Name");
        when(queryResultSetMock.getString("EXTERNAL_ID")).thenReturn("Code");

        XMLBuilder actualBuilder = departments.buildXML("TTU", "Fall", "2018");
        assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }

    @Test
    public void testBuildingXMLWithTwoEntryInResultSet() throws ParserConfigurationException, SQLException, TransformerException {
        XMLBuilder expectedBuilder = XMLBuilder.create("departments")
                .attribute("campus", "TTU")
                .attribute("term", "Fall")
                .attribute("year", "2018");
        expectedBuilder.element("department")
                .attribute("abbreviation", "Abv")
                .attribute("name", "Name")
                .attribute("deptCode", "Code");
        expectedBuilder.element("department")
                .attribute("abbreviation", "Abvv")
                .attribute("name", "Namee")
                .attribute("deptCode", "Codee");

        when(queryResultSetMock.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(queryResultSetMock.getString("ABBV")).thenReturn("Abv").thenReturn("Abvv");
        when(queryResultSetMock.getString("NIMETUS")).thenReturn("Name").thenReturn("Namee");
        when(queryResultSetMock.getString("EXTERNAL_ID")).thenReturn("Code").thenReturn("Codee");

        XMLBuilder actualBuilder = departments.buildXML("TTU", "Fall", "2018");
        assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }

    @Test
    public void testBuildingXMLWithZeroEntryInResultSet() throws ParserConfigurationException, SQLException, TransformerException {
        XMLBuilder expectedBuilder = XMLBuilder.create("departments")
                .attribute("campus", "TTU")
                .attribute("term", "Fall")
                .attribute("year", "2018");

        when(queryResultSetMock.next()).thenReturn(false);

        XMLBuilder actualBuilder = departments.buildXML("TTU", "Fall", "2018");
        assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }
}
