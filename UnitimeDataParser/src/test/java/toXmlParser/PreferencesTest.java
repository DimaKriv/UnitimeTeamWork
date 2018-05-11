package toXmlParser;

import com.jamesmurty.utils.XMLBuilder;
import org.junit.Before;
import org.junit.Test;
import parserUtility.ParserUtility;
import toXmlParser.dataOptimization.ClassOptimizator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.sql.ResultSet;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PreferencesTest {

    private Preferences preferences;
    private ParserUtility utilityMock;
    private String queryMock;
    private ResultSet queryResultSetMock;
    ClassOptimizator optimizerMock;

    @Before
    public void setup() {
        utilityMock = mock(ParserUtility.class);
        queryMock = "";
        queryResultSetMock = mock(ResultSet.class);
        optimizerMock = mock(ClassOptimizator.class);
        preferences = new Preferences(utilityMock, queryMock, queryResultSetMock, optimizerMock);
    }

    @Test
    public void testXMLBuilderWithGivenCampusAndTermAndYearIsCreatedCorrectly() throws ParserConfigurationException, TransformerException {
        XMLBuilder expectedBuilder = XMLBuilder.create("preferences")
                .attribute("campus", "TTU")
                .attribute("term", "Fall")
                .attribute("year", "2018");
        XMLBuilder actualBuilder = preferences.createPreferencesElementBuilder("TTU", "Fall", "2018");
        assertEquals(expectedBuilder.asString(), actualBuilder.asString());
    }

    @Test
    public void testGetClassTypeWithIndexZero() {
        String actualClassType = preferences.getClassType(0);
        String expectedClassType = "Lec";
        assertEquals(expectedClassType, actualClassType);
    }

    @Test
    public void testGetClassTypeWithIndexOne() {
        String actualClassType = preferences.getClassType(1);
        String expectedClassType = "Lab";
        assertEquals(expectedClassType, actualClassType);
    }

    @Test
    public void testGetClassTypeWithIndexTwo() {
        String actualClassType = preferences.getClassType(2);
        String expectedClassType = "Rec";
        assertEquals(expectedClassType, actualClassType);
    }

    @Test
    public void testGetClassTypeWithUnhandledIndex() {
        String actualClassType = preferences.getClassType(30);
        String expectedClassType = "Undefined type (classTypeIndex not in range from 0 to 2)";
        assertEquals(expectedClassType, actualClassType);
    }

    @Test
    public void testGetNumberOfClassesForClassTypeLecAnd100Students() {
        int actualNumberOfClasses = preferences.getNumberOfClassesForClassType("Lec", 100);
        int expectedNumberOfClasses = 1;
        assertEquals(expectedNumberOfClasses, actualNumberOfClasses);
    }

    @Test
    public void testGetNumberOfClassesForClassTypeLecAnd225Students() {
        int actualNumberOfClasses = preferences.getNumberOfClassesForClassType("Lec", 225);
        int expectedNumberOfClasses = 2;
        assertEquals(expectedNumberOfClasses, actualNumberOfClasses);
    }

    @Test
    public void testGetNumberOfClassesForClassTypeLabAnd10Students() {
        int actualNumberOfClasses = preferences.getNumberOfClassesForClassType("Lab", 10);
        int expectedNumberOfClasses = 1;
        assertEquals(expectedNumberOfClasses, actualNumberOfClasses);
    }

    @Test
    public void testGetNumberOfClassesForClassTypeLabAnd25Students() {
        int actualNumberOfClasses = preferences.getNumberOfClassesForClassType("Lab", 25);
        int expectedNumberOfClasses = 2;
        assertEquals(expectedNumberOfClasses, actualNumberOfClasses);
    }

    @Test
    public void testGetNumberOfClassesForClassTypeRecAnd10Students() {
        int actualNumberOfClasses = preferences.getNumberOfClassesForClassType("Lab", 10);
        int expectedNumberOfClasses = 1;
        assertEquals(expectedNumberOfClasses, actualNumberOfClasses);
    }

    @Test
    public void testGetNumberOfClassesForClassTypeRecAnd25Students() {
        int actualNumberOfClasses = preferences.getNumberOfClassesForClassType("Lab", 25);
        int expectedNumberOfClasses = 2;
        assertEquals(expectedNumberOfClasses, actualNumberOfClasses);
    }

    @Test
    public void testCountNumberOfClassesWhenStudentsNumberIsLowerThanClassCapacity() {
        int actualNumberOfClasses = preferences.countNumberOfClasses(20, 25);
        int expectedNumberOfClasses = 1;
        assertEquals(expectedNumberOfClasses, actualNumberOfClasses);
    }

    @Test
    public void testCountNumberOfClassesWhenStudentsNumberEqualsClassCapacity() {
        int actualNumberOfClasses = preferences.countNumberOfClasses(25, 25);
        int expectedNumberOfClasses = 1;
        assertEquals(expectedNumberOfClasses, actualNumberOfClasses);
    }

    @Test
    public void testCountNumberOfClassesWhenStudentsNumberIsGreaterThanClassCapacityAndModuloIsZero() {
        int actualNumberOfClasses = preferences.countNumberOfClasses(50, 25);
        int expectedNumberOfClasses = 2;
        assertEquals(expectedNumberOfClasses, actualNumberOfClasses);
    }

    @Test
    public void testCountNumberOfClassesWhenStudentsNumberIsGreaterThanClassCapacityAndModuloIsNotZero() {
        int actualNumberOfClasses = preferences.countNumberOfClasses(51, 25);
        int expectedNumberOfClasses = 3;
        assertEquals(expectedNumberOfClasses, actualNumberOfClasses);
    }

    @Test
    public void testGetTimePatternWhenTimePatternFromOptimizerIsNull() {
        ArrayList<Integer> dummyList = new ArrayList<>();
        when(optimizerMock.getTimePattern(dummyList)).thenReturn(null);
        String actualTimePattern = preferences.getTimePattern(dummyList);
        String expectedTimePattern = "Empty time pattern (Time patterns array is null)";
        assertEquals(expectedTimePattern, actualTimePattern);
    }

    @Test
    public void testGetTimePatternWhenTimePatternFromOptimizerIsStringArray() {
        ArrayList<Integer> dummyList = new ArrayList<>();
        String[] dummyArray = new String[]{"first pattern", "second pattern"};
        when(optimizerMock.getTimePattern(dummyList)).thenReturn(dummyArray);
        String actualTimePattern = preferences.getTimePattern(dummyList);
        String expectedTimePattern = "first pattern";
        assertEquals(expectedTimePattern, actualTimePattern);
    }

    @Test
    public void testGetDatePatternWhenDatePatternFromOptimizerIsNull() {
        ArrayList<Integer> dummyList = new ArrayList<>();
        when(optimizerMock.getDatePattern(dummyList)).thenReturn(null);
        String actualDatePattern = preferences.getDatePattern(dummyList);
        String expectedDatePattern = "Empty date pattern (Date patterns array is null)";
        assertEquals(expectedDatePattern, actualDatePattern);
    }

    @Test
    public void testGetDatePatternWhenDatePatternFromOptimizerIsStringArray() {
        ArrayList<Integer> dummyList = new ArrayList<>();
        String[] dummyArray = new String[]{"first pattern", "second pattern"};
        when(optimizerMock.getDatePattern(dummyList)).thenReturn(dummyArray);
        String actualDatePattern = preferences.getDatePattern(dummyList);
        String expectedDatePattern = "first pattern";
        assertEquals(expectedDatePattern, actualDatePattern);
    }

    @Test
    public void testCreateTimePrefElement() throws ParserConfigurationException, TransformerException {
        XMLBuilder actualXmlBuilder = XMLBuilder.create("test");
        actualXmlBuilder = preferences.createTimePrefElement(actualXmlBuilder, "time pattern");

        XMLBuilder expectedXmlBuilder = XMLBuilder.create("test");
        expectedXmlBuilder = expectedXmlBuilder.element("timePref")
                .attribute("pattern", "time pattern")
                .attribute("level", "0")
                .up();

        assertEquals(expectedXmlBuilder.asString(), actualXmlBuilder.asString());
    }

    @Test
    public void testCreateDatePrefElement() throws ParserConfigurationException, TransformerException {
        XMLBuilder actualXmlBuilder = XMLBuilder.create("test");
        actualXmlBuilder = preferences.createDatePrefElement(actualXmlBuilder, "date pattern");

        XMLBuilder expectedXmlBuilder = XMLBuilder.create("test");
        expectedXmlBuilder = expectedXmlBuilder.element("datePref")
                .attribute("pattern", "date pattern")
                .attribute("level", "0")
                .up();

        assertEquals(expectedXmlBuilder.asString(), actualXmlBuilder.asString());
    }

    @Test
    public void testCreateSubPartElementWithTimeAndDatePreferenceInXmlBuilder() throws ParserConfigurationException, TransformerException {
        XMLBuilder actualXmlBuilder = XMLBuilder.create("test");
        actualXmlBuilder = preferences.createSubPartElementWithTimeAndDatePreferenceInXmlBuilder(
                actualXmlBuilder, "subject", "class type", "time pattern", "date pattern");

        XMLBuilder expectedXmlBuilder = XMLBuilder.create("test");
        expectedXmlBuilder = expectedXmlBuilder.element("subpart")
                .attribute("subject", "subject")
                .attribute("course", "1")
                .attribute("type", "class type")
                .element("timePref")
                .attribute("pattern", "time pattern")
                .attribute("level", "0")
                .up()
                .element("datePref")
                .attribute("pattern", "date pattern")
                .attribute("level", "0")
                .up()
                .up();
        assertEquals(expectedXmlBuilder.asString(), actualXmlBuilder.asString());
    }

    @Test
    public void testCreateClassElement() throws ParserConfigurationException, TransformerException {
        XMLBuilder actualXmlBuilder = XMLBuilder.create("test");
        actualXmlBuilder = preferences.createClassElement(
                actualXmlBuilder, "subject", "class type", 0);

        XMLBuilder expectedXmlBuilder = XMLBuilder.create("test");
        expectedXmlBuilder = expectedXmlBuilder.element("class")
                .attribute("subject", "subject")
                .attribute("course", "1")
                .attribute("type", "class type")
                .attribute("suffix", "0")
                .up();

        assertEquals(expectedXmlBuilder.asString(), actualXmlBuilder.asString());
    }

    @Test
    public void testCreateClassElements() throws ParserConfigurationException, TransformerException {
        XMLBuilder actualXmlBuilder = XMLBuilder.create("test");
        actualXmlBuilder = preferences.createClassElements(
                actualXmlBuilder, "subject", "class type", 2);

        XMLBuilder expectedXmlBuilder = XMLBuilder.create("test");
        expectedXmlBuilder = expectedXmlBuilder.element("class")
                .attribute("subject", "subject")
                .attribute("course", "1")
                .attribute("type", "class type")
                .attribute("suffix", "1")
                .up()
                .element("class")
                .attribute("subject", "subject")
                .attribute("course", "1")
                .attribute("type", "class type")
                .attribute("suffix", "2")
                .up();

        assertEquals(expectedXmlBuilder.asString(), actualXmlBuilder.asString());
    }
}
