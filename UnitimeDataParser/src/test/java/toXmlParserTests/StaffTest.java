package toXmlParserTests;

import com.jamesmurty.utils.XMLBuilder;
import org.junit.Before;
import org.junit.Test;
import toXmlParser.StaffParser;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

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
}
