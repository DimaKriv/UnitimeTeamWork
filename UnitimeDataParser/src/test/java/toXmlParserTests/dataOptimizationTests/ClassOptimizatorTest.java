package toXmlParserTests.dataOptimizationTests;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import toXmlParser.dataOptimization.ClassOptimizator;

import java.util.ArrayList;
import java.util.Arrays;

public class ClassOptimizatorTest {
    private ClassOptimizator optimizer;
    @Before
    public void init() {
        optimizer = new ClassOptimizator();
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
        ArrayList<Integer> exp = new ArrayList<>(Arrays.asList(15,5,200,2));
       ArrayList<Integer> act = optimizer.countDatePattern(new int[][]{new int [] {80, 0,0}},0);
        Assert.assertEquals(exp, act);
    }
    @Test
    public void countPattern12() {
        ArrayList<Integer> exp = new ArrayList<>(Arrays.asList(10,10,100,7));
        ArrayList<Integer> act = optimizer.countDatePattern(new int[][]{new int [] {40, 20,10}},0);
        Assert.assertEquals(exp, act);
    }
    @Test
    public void countPattern13() {
        ArrayList<Integer> exp = new ArrayList<>(Arrays.asList(16,2,200,1));
        ArrayList<Integer> act = optimizer.countDatePattern(new int[][]{new int [] {100, 0,0}},0);
        Assert.assertEquals(exp, act);
    }
    @Test
    public void countPattern14() {
        ArrayList<Integer> exp = new ArrayList<>(Arrays.asList(16,16,160,0));
        ArrayList<Integer> act = optimizer.countDatePattern(new int[][]{new int [] {10, 0,0}},0);
        Assert.assertEquals(exp, act);
    }
    @Test
    public void timePattern() {
        String[] exp = new String[] {"2X90", "1X90"};
        String[] act = optimizer.getTimePattern(new ArrayList<>(Arrays.asList(10,10,200,11)));
        Assert.assertEquals(exp, act);
    }
    @Test
    public void timePattern1() {
        String[] exp = new String[] {null ,"1X45"};
        String[] act = optimizer.getTimePattern(new ArrayList<>(Arrays.asList(10,10,100,7)));
        Assert.assertEquals(exp, act);
    }
    @Test
    public void timePattern2() {
        String[] exp = new String[] {"3X135", "2X135"};
        String[] act = optimizer.getTimePattern(new ArrayList<>(Arrays.asList(16,4,300,11)));
        Assert.assertEquals(exp, act);
    }
    @Test
    public void timePatternNotValid() {
        String[] exp = null;
        String[] act = optimizer.getTimePattern(new ArrayList<>(Arrays.asList(16,16,120,0)));
        Assert.assertEquals(exp, act);
    }
    /*
    This test base on non database implementation.
     */
    @Test
    public void datePattern1() {
        String exp = "Odd week";
        String act = optimizer.getDatePattern(new ArrayList<>(Arrays.asList(16,2, 200,1)))[0];
        Assert.assertEquals(exp, act);
    }
    @Test
    public void datePattern2() {
        String exp = "15 weeks/12 class";
        String act = optimizer.getDatePattern(new ArrayList<>(Arrays.asList(15,5, 200,4)))[0];
        Assert.assertEquals(exp, act);
    }
    @Test
    public void datePatternNotValid() {
        String exp = null;
        String[] act = optimizer.getDatePattern(new ArrayList<>(Arrays.asList(10,10, 200,4)));
        Assert.assertEquals(exp, act);
    }
}
