package toXmlParserTests;


import com.jamesmurty.utils.XMLBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import parserUtility.ParserUtility;
import toXmlParser.CourseOffering;

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
            int [] exp = new int[] {70,200,25};
          int [] act = courseOffering.makeIntMassiveFromStringInput(",7", "2", ",25");
        System.out.println(act[0] + " " + act[1] + " " + act[2] );
        Assert.assertArrayEquals(exp, act);
    }

    @Test
    public void makeIntMassiveFromStringInputNotRightInput() {
        int [] exp = new int[] {70,200,0};
        int [] act = courseOffering.makeIntMassiveFromStringInput("0,7", " 2  ", ".25");
        System.out.println(act[0] + " " + act[1] + " " + act[2] );
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void makeIntMassiveFromStringInputNegative() {
        int [] exp = new int[] {0,0,0};
        int [] act = courseOffering.makeIntMassiveFromStringInput("-0,7", " -2  ", "-.25");
        System.out.println(act[0] + " " + act[1] + " " + act[2] );
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void makeCourceOfferringData() {
            int[][] exp = new int[][] {new int[]{170,30,0},new int[]{0,0,0},new int[]{0,0,200}};
            int [] input = new int[] {170,30,200};
            int[][] act = courseOffering.makeCourceOfferringData(input);
            Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void makeCourceOfferringDataTPair() {
        int[][] exp = new int[][] {new int[]{170,20,10},new int[]{0,0,0},new int[]{0,0,0}};
        int [] input = new int[] {170,20,10};
        int[][] act = courseOffering.makeCourceOfferringData(input);
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void makeCourceOfferringDataNonPair() {
        int[][] exp = new int[][] {new int[]{170,0,0},new int[]{0,150,0},new int[]{0,0,110}};
        int [] input = new int[] {170,150,110};
        int[][] act = courseOffering.makeCourceOfferringData(input);
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void findPair() {
            int [][] exp =new int[][]{new int[]{130, 70,0}, new int[]{0, 0,0}, new int[]{0,0,200} };
            int [][] act = new int[][]{new int[]{130, 0,0}
                    , new int[]{0, 70,0}, new int[]{0,0,200}};
            Assert.assertTrue(courseOffering.findPair(0, act));
            Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void findPairNotValid() {
        int [][] exp =new int[][]{new int[]{130, 0,0}, new int[]{0, 80,0}, new int[]{0,0,200} };
        int [][] act = new int[][]{new int[]{130, 0,0}
                , new int[]{0, 80,0}, new int[]{0,0,200}};
        Assert.assertFalse(courseOffering.findPair(0, act));
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void findPairAlt() {
        int [][] exp =new int[][]{new int[]{0,0 ,0}, new int[]{0, 50,50}, new int[]{0,0,0} };
        int [][] act = new int[][]{new int[]{0, 0,0}
                , new int[]{0, 50,0}, new int[]{0,0,50}};
        Assert.assertTrue(courseOffering.findPair(1, act));
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void findPairWorkCorrectlyEvenIfDatacorrupted() {
        int [][] exp = new int[][]{new int[]{70,0 ,0}, new int[]{130,0 ,130}, new int[]{130,130,0} };
        int [][] act = new int[][]{new int[]{70,0 ,0}, new int[]{130,0 ,130}, new int[]{130,130,0} };
        Assert.assertFalse(courseOffering.findPair(0, act));
        Assert.assertArrayEquals(exp, act);
    }

    @Test
    public void findTPair() {
        int [][] exp = new int[][]{new int[]{70,10 ,20}, new int[]{0,0 ,0}, new int[]{0,0,0} };
        int [][] act = new int[][]{new int[]{70,0 ,0}, new int[]{0,10 ,0}, new int[]{0,0,20} };
        Assert.assertTrue(courseOffering.findTPair(act));
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void findTPairNotCorrect() {
        int [][] exp = new int[][]{new int[]{70,0 ,0}, new int[]{10,10 ,10}, new int[]{20,20,0} };
        int [][] act = new int[][]{new int[]{70,0 ,0}, new int[]{10,10 ,10}, new int[]{20,20,0} };
        Assert.assertFalse(courseOffering.findTPair(act));
        Assert.assertArrayEquals(exp, act);
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
                courseOffering.createCourseClass(subpartMin, SubpartAndClass);
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
