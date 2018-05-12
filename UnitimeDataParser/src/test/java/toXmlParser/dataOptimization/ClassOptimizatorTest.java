package toXmlParserTests.dataOptimizationTests;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import toXmlParser.dataOptimization.ClassOptimization;

public class ClassOptimizatorTest {
    private ClassOptimization optimizer;
    @Before
    public void init() {
        optimizer = new ClassOptimization();
    }
    @Test
    public void findPair1() {
        int [] exp = new int[] {70,200,25};
        int [] act = optimizer.makeIntMassiveFromStringInput(";7", "2", ";25");
        System.out.println(act[0] + " " + act[1] + " " + act[2] );
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void makeIntMassiveFromStringInputNotRightInput() {
        int [] exp = new int[] {70,200,0};
        int [] act = optimizer.makeIntMassiveFromStringInput("0;7", " 2  ", ".25");
        System.out.println(act[0] + " " + act[1] + " " + act[2] );
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void makeIntMassiveFromStringInputNegative() {
        int [] exp = new int[] {0,0,0};
        int [] act = optimizer.makeIntMassiveFromStringInput("-0,7", " -2  ", "-.25");
        System.out.println(act[0] + " " + act[1] + " " + act[2] );
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void makeCourceOfferringData() {
        int[][] exp = new int[][] {new int[]{170,30,0},new int[]{0,0,0},new int[]{0,0,200}};
        int [] input = new int[] {170,30,200};
        int[][] act = optimizer.makeCourceOfferringData(input);
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void makeCourceOfferringDataTPair() {
        int[][] exp = new int[][] {new int[]{170,20,10},new int[]{0,0,0},new int[]{0,0,0}};
        int [] input = new int[] {170,20,10};
        int[][] act = optimizer.makeCourceOfferringData(input);
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void makeCourceOfferringDataNonPair() {
        int[][] exp = new int[][] {new int[]{170,0,0},new int[]{0,150,0},new int[]{0,0,110}};
        int [] input = new int[] {170,150,110};
        int[][] act = optimizer.makeCourceOfferringData(input);
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void findPair() {
        int [][] exp =new int[][]{new int[]{130, 70,0}, new int[]{0, 0,0}, new int[]{0,0,200} };
        int [][] act = new int[][]{new int[]{130, 0,0}
                , new int[]{0, 70,0}, new int[]{0,0,200}};
        Assert.assertTrue(optimizer.findPair(0, act));
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void findPairNotValid() {
        int [][] exp =new int[][]{new int[]{130, 0,0}, new int[]{0, 80,0}, new int[]{0,0,200} };
        int [][] act = new int[][]{new int[]{130, 0,0}
                , new int[]{0, 80,0}, new int[]{0,0,200}};
        Assert.assertFalse(optimizer.findPair(0, act));
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void findPairAlt() {
        int [][] exp =new int[][]{new int[]{0,0 ,0}, new int[]{0, 50,50}, new int[]{0,0,0} };
        int [][] act = new int[][]{new int[]{0, 0,0}
                , new int[]{0, 50,0}, new int[]{0,0,50}};
        Assert.assertTrue(optimizer.findPair(1, act));
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void findPairWorkCorrectlyEvenIfDatacorrupted() {
        int [][] exp = new int[][]{new int[]{70,0 ,0}, new int[]{130,0 ,130}, new int[]{130,130,0} };
        int [][] act = new int[][]{new int[]{70,0 ,0}, new int[]{130,0 ,130}, new int[]{130,130,0} };
        Assert.assertFalse(optimizer.findPair(0, act));
        Assert.assertArrayEquals(exp, act);
    }

    @Test
    public void findTPair() {
        int [][] exp = new int[][]{new int[]{70,10 ,20}, new int[]{0,0 ,0}, new int[]{0,0,0} };
        int [][] act = new int[][]{new int[]{70,0 ,0}, new int[]{0,10 ,0}, new int[]{0,0,20} };
        Assert.assertTrue(optimizer.findTPair(act));
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void findTPairNotCorrect() {
        int [][] exp = new int[][]{new int[]{70,0 ,0}, new int[]{10,10 ,10}, new int[]{20,20,0} };
        int [][] act = new int[][]{new int[]{70,0 ,0}, new int[]{10,10 ,10}, new int[]{20,20,0} };
        Assert.assertFalse(optimizer.findTPair(act));
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void countPattern1() {
        int[] exp = new int[] {15,200,6};
       int[] act = optimizer.countDatePattern(new int[][]{new int [] {80, 0,0}},0);
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void countPattern12() {
        int[] exp = new int[] {10,100,7};
        int[] act = optimizer.countDatePattern(new int[][]{new int [] {40, 20,10}},0);
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void countPattern13() {
        int[] exp = new int[] {16,200,8};
        int[] act = optimizer.countDatePattern(new int[][]{new int [] {100, 0,0}},0);
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void countPattern14() {
        int[] exp = new int[] {16,160,0};
        int[] act = optimizer.countDatePattern(new int[][]{new int [] {10, 0,0}},0);
        Assert.assertArrayEquals(exp, act);
    }
    @Test
    public void timePattern() {
        String exp = "1X90";
        String act = optimizer.getTimePattern(new int[]{10,200,11});
        Assert.assertEquals(exp, act);
    }
    @Test
    public void timePattern1() {
        String exp = "3X90";
        String act = optimizer.getTimePattern(new int[] {16,200,48});
        Assert.assertEquals(exp, act);
    }
    @Test
    public void timePattern2() {
        String exp = "2X135";
        String act = optimizer.getTimePattern(new int[] {16,300,32});
        Assert.assertEquals(exp, act);
    }
    @Test
    public void timePatternNotValid() {
        String exp = ClassOptimization.NO_TIME_PATTERN;
        String act = optimizer.getTimePattern(new int[]{16,120,0});
        Assert.assertEquals(exp, act);
    }
    /*
    This test base on non database implementation.
     */
    @Test
    public void datePattern1() {
        String exp = "Half Term";
        String act = optimizer.getDatePattern(new int[]{16,200,8});
        Assert.assertEquals(exp, act);
    }
    @Test
    public void datePatternNotValid() {
        String exp = ClassOptimization.NO_DATE_PATTERN;
        String act = optimizer.getDatePattern(new int[]{10, 200,4});
        Assert.assertEquals(exp, act);
    }
}
