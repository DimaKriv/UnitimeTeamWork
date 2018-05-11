package toXmlParser;


import com.jamesmurty.utils.XMLBuilder;
import org.junit.Before;
import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.sql.ResultSet;
import java.util.Properties;

import static org.mockito.Mockito.mock;

public class CourseOfferingTest {
        private CourseOffering courseOffering;
        private String queryMock;
        private ResultSet queryResultSetMock;
        @Before
        public void setup() {
            queryMock = "";
            queryResultSetMock = mock(ResultSet.class);
            courseOffering = new CourseOffering(queryMock, queryResultSetMock);
        }
    @Test
    public void makeIntMassiveFromStringInput() {

    }


    @Test
    public void findOptionalPair() {
    }
    @Test
    public void controllStructureOfXmlMannually() {
            int[][] subpartMin =  new int[][] {new int[]{170,30,0},new int[]{0,0,0},new int[]{0,0,200}};
        try {
            XMLBuilder SubpartAndClass = XMLBuilder.create("offering");
        courseOffering.createCourseSubject(subpartMin, SubpartAndClass);
                courseOffering.createCourseClass(subpartMin, SubpartAndClass, 100);
            Properties prop = new Properties();
            prop.put(javax.xml.transform.OutputKeys.INDENT, "yes");
            System.out.println( SubpartAndClass.asString(prop));
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}
