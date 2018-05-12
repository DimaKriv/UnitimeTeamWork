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

public class StaffTest {

    private StaffParser staffParser;
    private String queryMock;
    private ResultSet queryResultSetMock;

    @Before
    public void setup() {
        queryMock = "";
        queryResultSetMock = mock(ResultSet.class);
        staffParser = new StaffParser(queryMock, queryResultSetMock);
    }

    @Test
    public void testXMLBuilderWithGivenCampusAndTermAndYearIsCreatedCorrectly() throws ParserConfigurationException, TransformerException {
        XMLBuilder expectedBuilder = XMLBuilder.create("staff")
                .attribute("campus", "TTU")
                .attribute("term", "Fall")
                .attribute("year", "2018");
        XMLBuilder actualBuilder = staffParser.createStaffParserBuilder("TTU", "Fall", "2018");
        assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }

    @Test
    public void testStaffWithOneResultSet() throws ParserConfigurationException, TransformerException, SQLException {
        XMLBuilder expectedBuilder = XMLBuilder.create("staff")
                .attribute("campus", "TTU")
                .attribute("term", "Fall")
                .attribute("year", "2018");
        expectedBuilder.element("staffMember")
                .attribute("externalId", "1")
                .attribute("firstName", "Tarmo")
                .attribute("lastName", "Kalvo")
                .attribute("positionType", "Doktor")
                .attribute("department", "1002");

        when(queryResultSetMock.next()).thenReturn(true).thenReturn(false);
        when(queryResultSetMock.getString("NIMI")).thenReturn("Tarmo");
        when(queryResultSetMock.getString("PEREKONNANIMI")).thenReturn("Kalvo");
        when(queryResultSetMock.getString("AMET")).thenReturn("Doktor");
        when(queryResultSetMock.getString("EXTERNAL_ID")).thenReturn("1002");

        XMLBuilder actualBuilder = staffParser.buildXML("TTU", "Fall", "2018");
        assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }

    @Test
    public void testStaffWithTwoResultSet() throws ParserConfigurationException, SQLException, TransformerException {
        XMLBuilder expectedBuilder = XMLBuilder.create("staff")
                .attribute("campus", "TTU")
                .attribute("term", "Fall")
                .attribute("year", "2018");
        expectedBuilder.element("staffMember")
                .attribute("externalId", "1")
                .attribute("firstName", "Tarmo")
                .attribute("lastName", "Kalvo")
                .attribute("positionType", "Doktor")
                .attribute("department", "1002");
        expectedBuilder.element("staffMember")
                .attribute("externalId", "2")
                .attribute("firstName", "Kevin")
                .attribute("lastName", "Lostma")
                .attribute("positionType", "Lektor")
                .attribute("department", "1001");

        when(queryResultSetMock.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(queryResultSetMock.getString("NIMI")).thenReturn("Tarmo").thenReturn("Kevin");
        when(queryResultSetMock.getString("PEREKONNANIMI")).thenReturn("Kalvo").thenReturn("Lostma");
        when(queryResultSetMock.getString("AMET")).thenReturn("Doktor").thenReturn("Lektor");
        when(queryResultSetMock.getString("EXTERNAL_ID")).thenReturn("1002").thenReturn("1001");

        XMLBuilder actualBuilder = staffParser.buildXML("TTU", "Fall", "2018");
        assertEquals(expectedBuilder.asString(), actualBuilder.asString());

    }


}
