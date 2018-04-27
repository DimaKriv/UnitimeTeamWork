package toXmlParserTests;

import com.jamesmurty.utils.XMLBuilder;
import org.junit.Before;
import org.junit.Test;
import toXmlParser.CourseCatalog;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CourseCatalogTest {

    private static final int DEFAULT_FAKE_EAP = 5;

    private String queryMock;
    private ResultSet queryResultSetMock;
    private CourseCatalog courseCatalog;

    @Before
    public void setUp() {
        this.queryMock = "";
        this.queryResultSetMock = mock(ResultSet.class);
        this.courseCatalog = new CourseCatalog(queryMock, queryResultSetMock);
    }

    @Test
    public void testXMLBuilderWithGivenCampusAndTermAndYearIsCreatedCorrectly() throws ParserConfigurationException,
            TransformerException {

        XMLBuilder expectedBuilder = XMLBuilder.create("courseCatalog")
                .attribute("campus", "TTU")
                .attribute("term", "Fall")
                .attribute("year", "2018");
        XMLBuilder actualBuilder = courseCatalog.createCourseCatalogElementBuilder("TTU", "Fall",
                "2018");
        assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }

    @Test
    public void testBuildingXMLWithOneEntryInResultSet() throws ParserConfigurationException, SQLException,
            TransformerException {

        XMLBuilder expectedBuilder = XMLBuilder.create("courseCatalog")
                .attribute("campus", "TTU")
                .attribute("term", "Fall")
                .attribute("year", "2018");

        XMLBuilder course = expectedBuilder.element("course")
                .attribute("courseNumber", "36263")
                .attribute("subject", "TMM1280")
                .attribute("title", "Turundussimulatsioon");

        course.element("courseCredit")
                .attribute("creditType", "collegiate")
                .attribute("creditUnitType", "semesterHours")
                .attribute("creditFormat", "fixedUnit")
                .attribute("fixedCredit", String.valueOf(DEFAULT_FAKE_EAP));

        when(queryResultSetMock.next()).thenReturn(true).thenReturn(false);
        when(queryResultSetMock.getString("ainekood")).thenReturn("TMM1280");
        when(queryResultSetMock.getString("fk_aine_id")).thenReturn("36263");
        when(queryResultSetMock.getString("nimetus_est")).thenReturn("Turundussimulatsioon");

        XMLBuilder actualBuilder = courseCatalog.buildXML("TTU", "Fall", "2018");

        assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }

    @Test
    public void testBuildingXMLWithTwoEntryInResultSet() throws ParserConfigurationException, SQLException, TransformerException {
        XMLBuilder expectedBuilder = XMLBuilder.create("courseCatalog")
                .attribute("campus", "TTU")
                .attribute("term", "Fall")
                .attribute("year", "2018");

        XMLBuilder course1 = expectedBuilder.element("course")
                .attribute("courseNumber", "36263")
                .attribute("subject", "TMM1280")
                .attribute("title", "Turundussimulatsioon");

        course1.element("courseCredit")
                .attribute("creditType", "collegiate")
                .attribute("creditUnitType", "semesterHours")
                .attribute("creditFormat", "fixedUnit")
                .attribute("fixedCredit", String.valueOf(DEFAULT_FAKE_EAP));

        XMLBuilder course2 = expectedBuilder.element("course")
                .attribute("courseNumber", "25789")
                .attribute("subject", "AAV3333")
                .attribute("title", "Elektriajamid - projekt");

        course2.element("courseCredit")
                .attribute("creditType", "collegiate")
                .attribute("creditUnitType", "semesterHours")
                .attribute("creditFormat", "fixedUnit")
                .attribute("fixedCredit", String.valueOf(DEFAULT_FAKE_EAP));

        when(queryResultSetMock.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(queryResultSetMock.getString("ainekood"))
                .thenReturn("TMM1280")
                .thenReturn("AAV3333");
        when(queryResultSetMock.getString("fk_aine_id"))
                .thenReturn("36263")
                .thenReturn("25789");
        when(queryResultSetMock.getString("nimetus_est"))
                .thenReturn("Turundussimulatsioon")
                .thenReturn("Elektriajamid - projekt");

        XMLBuilder actualBuilder = courseCatalog.buildXML("TTU", "Fall", "2018");

        assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }
}