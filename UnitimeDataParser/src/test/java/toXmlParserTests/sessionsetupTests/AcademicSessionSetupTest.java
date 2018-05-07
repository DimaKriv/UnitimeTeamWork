package toXmlParserTests.sessionsetupTests;

import com.jamesmurty.utils.XMLBuilder;
        import org.junit.Before;
        import org.junit.Test;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import toXmlParser.sessionsetup.AcademicSessionSetup;

import javax.xml.parsers.ParserConfigurationException;
        import javax.xml.transform.TransformerException;
        import java.sql.SQLException;
import java.util.Arrays;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;


        import static org.junit.Assert.assertEquals;
public class AcademicSessionSetupTest {
/*
    private AcademicSessionSetup academicSessionSetup;
    private String queryMock;



    @Before
    public void setup() throws SQLException {
        queryMock = "";
        academicSessionSetup = new AcademicSessionSetup(queryMock);
    }

    @Test
    public void testXMLBuilderBuildsXMLCorrectly() throws ParserConfigurationException, TransformerException {
        XMLBuilder expectedBuilder = XMLBuilder.create(("sessionSetup"))
                .attribute("term", "Fall")
                .attribute("year", "2018")
                .attribute("campus", "TTU")
                .attribute("dateFormat", "yyyy/M/d")
                .attribute("created", "Fri Jun 23 15:21:28 CEST 2117");

        XMLBuilder actualBuilder = academicSessionSetup.buildXML("TTU", "Fall", "2018");
        assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }

    @Test
    public void testGenerateDateCorrectly() {
        String expectedFormat = "2018/4/3";
        assertEquals(expectedFormat, academicSessionSetup.getDateInFormat(2, 10));
    }

    @Test
    public void testCallCreateXmlFileWorks() {
        AcademicSessionSetup acad = mock(AcademicSessionSetup.class);
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                System.out.println("called with arguments: " + Arrays.toString(args));
                return null;
            }
        }).when(acad).createXMLFile(anyString(),anyString(),anyString());
    }*/
}