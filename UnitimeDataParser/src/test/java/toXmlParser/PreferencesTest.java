package toXmlParser;

import com.jamesmurty.utils.XMLBuilder;
import org.junit.Before;
import org.junit.Test;
import parserUtility.ParserUtility;
import toXmlParser.dataOptimization.ClassOptimization;

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
    ClassOptimization optimizerMock;

    @Before
    public void setup() {
        utilityMock = mock(ParserUtility.class);
        queryMock = "";
        queryResultSetMock = mock(ResultSet.class);
        optimizerMock = mock(ClassOptimization.class);
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
        String expectedClassType = "Undefined type";
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
        int[] dummyTimeDistribution = new int[3];
        when(optimizerMock.getTimePattern(dummyTimeDistribution)).thenReturn(null);
        String actualTimePattern = preferences.getTimePattern(dummyTimeDistribution);
        String expectedTimePattern = "Undefined time pattern";
        assertEquals(expectedTimePattern, actualTimePattern);
    }

    @Test
    public void testGetTimePatternWhenTimePatternFromOptimizerIsStringArray() {
        int[] dummyTimeDistribution = new int[3];
        String dummyTimePattern = "dummy pattern";
        when(optimizerMock.getTimePattern(dummyTimeDistribution)).thenReturn(dummyTimePattern);
        String actualTimePattern = preferences.getTimePattern(dummyTimeDistribution);
        String expectedTimePattern = "dummy pattern";
        assertEquals(expectedTimePattern, actualTimePattern);
    }

    @Test
    public void testGetDatePatternWhenDatePatternFromOptimizerIsNull() {
        int[] dummyTimeDistribution = new int[3];
        when(optimizerMock.getDatePattern(dummyTimeDistribution)).thenReturn(null);
        String actualDatePattern = preferences.getDatePattern(dummyTimeDistribution);
        String expectedDatePattern = "Undefined date pattern";
        assertEquals(expectedDatePattern, actualDatePattern);
    }

    @Test
    public void testGetDatePatternWhenDatePatternFromOptimizerIsStringArray() {
        int[] dummyTimeDistribution = new int[3];
        String dummyDatePattern = "dummy pattern";
        when(optimizerMock.getDatePattern(dummyTimeDistribution)).thenReturn(dummyDatePattern);
        String actualDatePattern = preferences.getDatePattern(dummyTimeDistribution);
        String expectedDatePattern = "dummy pattern";
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
