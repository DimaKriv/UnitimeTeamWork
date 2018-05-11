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
public class BuildingAndRoomsTest{

    private BuildingsAndRooms buildingsAndRooms;
    private String queryMock;
    private ResultSet queryResultSetMock;

    @Before
    public void setup() {
        queryMock = "";
        queryResultSetMock = mock(ResultSet.class);
        buildingsAndRooms = new BuildingsAndRooms(queryMock, queryResultSetMock);
    }

    @Test
    public void testXMLBuilderWithGivenCampusAndTermAndYearIsCreatedCorrectly() throws ParserConfigurationException, TransformerException {
        XMLBuilder expectedBuilder = XMLBuilder.create("buildingsRooms")
                .attribute("campus", "TTU")
                .attribute("term", "Fall")
                .attribute("year", "2018");
        XMLBuilder actualBuilder = buildingsAndRooms.createBuildingAndRoomsElementBuilder("TTU", "Fall", "2018");
        assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }

    @Test
    public void testBuildingXMLWithOneEntryInResultSet() throws ParserConfigurationException, SQLException, TransformerException {
        XMLBuilder expectedBuilder = XMLBuilder.create("BuildingsRooms")
                .attribute("campus", "TTU")
                .attribute("term", "Fall")
                .attribute("year", "2018");
        expectedBuilder.element("building")
                .attribute("abbreviation", "ABC")
                .attribute("externalId", "101")
                .attribute("locationX", String.valueOf(0))
                .attribute("locationY", String.valueOf(0))
                .attribute("name", "House")
                .element("room")
                .attribute("externalId", "R101")
                .attribute("locationX", String.valueOf(0))
                .attribute("locationY", String.valueOf(0))
                .attribute("roomClassification", "Classroom")
                .attribute("roomNumber", "120")
                .attribute("capacity", "24")
                .element("roomDepartments")
                .element("assigned")
                .attribute("departmentNumber", "1001")
                .attribute("percent", "100")
                .up()
                .element("scheduling")
                .attribute("departmentNumber", "1002")
                .attribute("percent", "50")
                .up()
                .up()
                .element("roomFeature")
                .attribute("feature", "tahvel")
                .attribute("value", "tahvel")
                .up()
                .up();
        when(queryResultSetMock.next()).thenReturn(true).thenReturn(false);
        when(queryResultSetMock.getString("bl_id")).thenReturn("101");
        when(queryResultSetMock.getString("buildingName")).thenReturn("House");
        when(queryResultSetMock.getString("ruum_id")).thenReturn("R101");
        when(queryResultSetMock.getString("kood")).thenReturn("ABC");
        when(queryResultSetMock.getString("kohtade_arv")).thenReturn("24");
        when(queryResultSetMock.getString("rm_id")).thenReturn("120");
        when(queryResultSetMock.getString("roomName")).thenReturn("Classroom");

        XMLBuilder actualBuilder = buildingsAndRooms.createBuildingAndRoomsElementBuilder("TTU", "Fall", "2018");
//            assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }

    @Test
    public void testBuildingXMLWithZeroEntryInResultSet() throws ParserConfigurationException, SQLException, TransformerException {
        XMLBuilder expectedBuilder = XMLBuilder.create("buildingsRooms")
                .attribute("campus", "TTU")
                .attribute("term", "Fall")
                .attribute("year", "2018");

        when(queryResultSetMock.next()).thenReturn(false);

        XMLBuilder actualBuilder = buildingsAndRooms.buildXML("TTU", "Fall", "2018");
        assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }
}
