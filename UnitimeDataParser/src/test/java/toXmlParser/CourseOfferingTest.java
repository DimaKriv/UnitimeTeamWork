package toXmlParser;

import com.jamesmurty.utils.XMLBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.sql.ResultSet;
import java.util.*;

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
    public void putToMapTypeOfClassAndInstructorId() {
       HashMap<Integer, List<Integer>> exp = new HashMap<>();
       exp.put(0, new ArrayList<>(Arrays.asList(new Integer(0001))));
       HashMap<Integer, List<Integer>> act = new HashMap<>();
       act.put(0, new ArrayList<>());
            courseOffering.putToMapTypeOfClassAndInstructorId(act,"loeng","0001");
       Assert.assertEquals(act,exp);
   }

    @Test
    public void putToMapTypeOfClassAndInstructorId2() {
        HashMap<Integer, List<Integer>> exp = new HashMap<>();
        exp.put(0, new ArrayList<>(Arrays.asList(new Integer(0001))));
        exp.put(1, new ArrayList<>(Arrays.asList(new Integer(0001))));
        HashMap<Integer, List<Integer>> act = new HashMap<>();
        act.put(0, new ArrayList<>());
        act.put(1, new ArrayList<>());
        courseOffering.putToMapTypeOfClassAndInstructorId(act,"loeng+praktikum","0001");
        Assert.assertEquals(act,exp);
    }

    @Test
    public void putToMapTypeOfClassAndInstructorId3() {
        HashMap<Integer, List<Integer>> exp = new HashMap<>();
        exp.put(0, new ArrayList<>(Arrays.asList(new Integer(0001))));
        exp.put(1, new ArrayList<>(Arrays.asList(new Integer(0001))));
        exp.put(2,new ArrayList<>((Arrays.asList(new Integer(0001)))));
        HashMap<Integer, List<Integer>> act = new HashMap<>();
        act.put(0, new ArrayList<>());
        act.put(1, new ArrayList<>());
        act.put(2, new ArrayList<>());
        courseOffering.putToMapTypeOfClassAndInstructorId(
                act,"loeng+praktikum+harjutus","0001");
        Assert.assertEquals(act,exp);
    }
    @Test
    public void putToMapTypeOfClassAndInstructorIdNotValid() {
        HashMap<Integer, List<Integer>> exp = new HashMap<>();
        exp.put(0, new ArrayList<>());
        HashMap<Integer, List<Integer>> act = new HashMap<>();
        act.put(0, new ArrayList<>());
        courseOffering.putToMapTypeOfClassAndInstructorId(
                act,"loeng+praktikum+harjutus","0001d");
        Assert.assertEquals(act,exp);
    }
    @Test
    public void putToMapTypeOfClassAndInstructorIdNotValidType() {
        HashMap<Integer, List<Integer>> exp = new HashMap<>();
        exp.put(0, new ArrayList<>());
        HashMap<Integer, List<Integer>> act = new HashMap<>();
        act.put(0, new ArrayList<>());
        courseOffering.putToMapTypeOfClassAndInstructorId(
                act,"loengt+praktikum+harjutus","0001");
        Assert.assertEquals(act,exp);
    }

    @Test
    public void controllStructureOfXmlMannually() {
            int[][] subpartMin =  new int[][] {new int[]{170,30,0},new int[]{0,0,0},new int[]{0,0,200}};
        try {
            HashMap<Integer,List<Integer>> staff = new HashMap<>();
            staff.put(0,new ArrayList(Arrays.asList(new String[] {"890", "1000","3020"})));
            staff.put(1,new ArrayList(Arrays.asList(new String[] {"891", "1001","3021"})));
            staff.put(2,new ArrayList(Arrays.asList(new String[] {"890", "1002","30022"})));
            XMLBuilder SubpartAndClass = XMLBuilder.create("offering");
                courseOffering.createCourseSubject(subpartMin, SubpartAndClass);
                courseOffering.createCourseClass(subpartMin, SubpartAndClass, 100, staff);
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
