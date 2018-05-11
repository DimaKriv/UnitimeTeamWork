package toXmlParser;

import com.jamesmurty.utils.XMLBuilder;
import parserUtility.ParserUtility;
import toXmlParser.dataOptimization.ClassOptimizator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;

public class Preferences {

    private ParserUtility utility;
    private String querySql;
    private ResultSet queryResultSet;
    private ClassOptimizator optimizer;
    private static final int LECTURE_CLASS_TYPE_CAPACITY = 200;
    private static final int LABORATORY_CLASS_TYPE_CAPACITY = 20;
    private static final int RECITATION_CLASS_TYPE_CAPACITY = 20;


    public Preferences() throws SQLException {
        utility = new ParserUtility();
        Connection connection = utility.connectToDatabase();
        Statement statement = utility.createStatement(connection);
        querySql = "select inimeste_arv,ainekood,nimetus,loeng,praktikum,harjutus from U_COURSEOFFERING";
        queryResultSet = utility.queryDataFromDatabase(querySql, statement);
        optimizer = new ClassOptimizator();
    }

    public Preferences(ParserUtility utility, String querySql, ResultSet queryResultSet, ClassOptimizator optimizer) {
        this.utility = utility;
        this.querySql = querySql;
        this.queryResultSet = queryResultSet;
        this.optimizer = optimizer;
    }

    public XMLBuilder createPreferencesElementBuilder(String campus, String term, String year) throws ParserConfigurationException {
        XMLBuilder xmlBuilder =
            XMLBuilder.create("preferences")
                .attribute("campus", campus)
                .attribute("term", term)
                .attribute("year", year);

        return xmlBuilder;
    }

    public XMLBuilder buildXML(String campus, String term, String year) throws ParserConfigurationException, SQLException {
        XMLBuilder xmlBuilder = createPreferencesElementBuilder(campus, term, year);

        while (queryResultSet.next()) {
            String subject = queryResultSet.getString("ainekood");
            String lectureHoursPerWeek = queryResultSet.getString("loeng");
            String laboratoryHoursPerWeek = queryResultSet.getString("praktikum");
            String recitationHoursPerWeek = queryResultSet.getString("harjutus");
            int studentsNumber = Integer.parseInt(queryResultSet.getString("inimeste_arv"));
            int[] classTypes = optimizer.makeIntMassiveFromStringInput(
                    lectureHoursPerWeek, laboratoryHoursPerWeek, recitationHoursPerWeek);
            int[][] subPartsForTypes = optimizer.makeCourceOfferringData(classTypes);
            for (int index = 0; index < 2; index++) {
                if (classTypes[index] != 0) {
                    String classType = getClassType(index);
                    int numberOfClasses = getNumberOfClassesForClassType(classType, studentsNumber);
                    ArrayList<Integer> classTypeTimeDistributionInfo = optimizer.countDatePattern(subPartsForTypes, index);
                    String timePattern = getTimePattern(classTypeTimeDistributionInfo);
                    String datePattern = getDatePattern(classTypeTimeDistributionInfo);
                    xmlBuilder = createSubPartElementWithTimeAndDatePreferenceInXmlBuilder(
                            xmlBuilder, subject, classType, timePattern, datePattern);
                    xmlBuilder = createClassElements(xmlBuilder, subject, classType, numberOfClasses);
                }
            }
        }
        xmlBuilder = xmlBuilder.up();
        return xmlBuilder;
    }

    public String getClassType(int classTypeIndex) {
        switch (classTypeIndex) {
            case 0:
                return "Lec";
            case 1:
                return "Lab";
            case 2:
                return "Rec";
            default:
                return "Undefined type (classTypeIndex not in range from 0 to 2)";
        }
    }

    public int countNumberOfClasses(int studentsNumber, int classCapacity) {
        int numberOfClasses;
        if (studentsNumber <= classCapacity) {
            numberOfClasses = 1;
        } else {
            if (studentsNumber % classCapacity == 0) {
                numberOfClasses = studentsNumber / classCapacity;
            } else {
                numberOfClasses = studentsNumber / classCapacity + 1;
            }
        }
        return numberOfClasses;
    }

    public int getNumberOfClassesForClassType(String classType, int studentsNumber) {
        switch (classType) {
            case "Lec":
                return countNumberOfClasses(studentsNumber, LECTURE_CLASS_TYPE_CAPACITY);
            case "Lab":
                return countNumberOfClasses(studentsNumber, LABORATORY_CLASS_TYPE_CAPACITY);
            case "Rec":
                return countNumberOfClasses(studentsNumber, RECITATION_CLASS_TYPE_CAPACITY);
            default:
                return 0;
        }
    }

    public String getTimePattern(ArrayList<Integer> classTypeTimeDistributionInfo) {
        String[] timePatterns = optimizer.getTimePattern(classTypeTimeDistributionInfo);
        Optional<String[]> optionalTimePatterns = Optional.ofNullable(timePatterns);
        String timePattern = "Empty time pattern (Time patterns array is null)";
        if (optionalTimePatterns.isPresent())  {
            timePattern = timePatterns[0];
        }
        return timePattern;
    }

    public String getDatePattern(ArrayList<Integer> classTypeTimeDistributionInfo) {
        String[] datePatterns = optimizer.getDatePattern(classTypeTimeDistributionInfo);
        Optional<String[]> optionalDatePatterns = Optional.ofNullable(datePatterns);
        String datePattern = "Empty date pattern (Date patterns array is null)";
        if (optionalDatePatterns.isPresent())  {
            datePattern = datePatterns[0];
        }
        return datePattern;
    }

    public XMLBuilder createTimePrefElement(XMLBuilder xmlBuilder, String timePattern) {
        xmlBuilder = xmlBuilder.element("timePref")
                // pattern need some improvements after fixes will be made in ClassOptimizator class.
                .attribute("pattern", timePattern)
                .attribute("level", "0")
                .up();
        return xmlBuilder;
    }

    public XMLBuilder createDatePrefElement(XMLBuilder xmlBuilder, String datePattern) {
        xmlBuilder = xmlBuilder.element("datePref")
                .attribute("pattern", datePattern)
                .attribute("level", "0")
                .up();
        return xmlBuilder;
    }

    public XMLBuilder createSubPartElementWithTimeAndDatePreferenceInXmlBuilder(
            XMLBuilder xmlBuilder, String subject, String classType, String timePattern, String datePattern) {
        xmlBuilder = xmlBuilder.element("subpart")
                .attribute("subject", subject)
                .attribute("course", "1")
                .attribute("type", classType);
        xmlBuilder = createTimePrefElement(xmlBuilder, timePattern);
        xmlBuilder = createDatePrefElement(xmlBuilder, datePattern);
        xmlBuilder = xmlBuilder.up();
        return xmlBuilder;
    }

    public XMLBuilder createClassElement(XMLBuilder xmlBuilder, String subject, String classType, int classNumber) {
        xmlBuilder = xmlBuilder.element("class")
                .attribute("subject", subject)
                .attribute("course", "1")
                .attribute("type", classType)
                .attribute("suffix", String.valueOf(classNumber))
                .up();
        return xmlBuilder;
    }

    public XMLBuilder createClassElements(XMLBuilder xmlBuilder, String subject, String classType, int numberOfClasses) {
        for (int classNumber = 1; classNumber <= numberOfClasses; classNumber++) {
            xmlBuilder = createClassElement(xmlBuilder, subject, classType, classNumber);
        }
        return xmlBuilder;
    }

    public void writeXML(XMLBuilder xmlBuilder) throws FileNotFoundException, TransformerException {
        utility.writeToXMLFile(xmlBuilder, "preferences.xml");
    }

    public void createXMLFile(String campus, String term, String year) {
        try {
            XMLBuilder xmlBuilder = buildXML(campus, term, year);
            writeXML(xmlBuilder);
        } catch (ParserConfigurationException | SQLException | FileNotFoundException | TransformerException e) {
            e.printStackTrace();
        }
    }
}
