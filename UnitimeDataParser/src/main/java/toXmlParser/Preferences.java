package toXmlParser;

import com.jamesmurty.utils.XMLBuilder;
import parserUtility.ParserUtility;
import toXmlParser.dataOptimization.ClassOptimization;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class Preferences {

    private ParserUtility utility;
    private String querySql;
    private ResultSet queryResultSet;
    private ClassOptimization optimizer;
    private static final int LECTURE_CLASS_TYPE_CAPACITY = 200;
    private static final int LABORATORY_CLASS_TYPE_CAPACITY = 20;
    private static final int RECITATION_CLASS_TYPE_CAPACITY = 20;


    public Preferences() throws SQLException {
        utility = new ParserUtility();
        Connection connection = utility.connectToDatabase();
        Statement statement = utility.createStatement(connection);
        querySql = "select inimeste_arv,ainekood,nimetus,loeng,praktikum,harjutus from U_COURSEOFFERING";
        queryResultSet = utility.queryDataFromDatabase(querySql, statement);
        optimizer = new ClassOptimization();
    }

    public Preferences(ParserUtility utility, String querySql, ResultSet queryResultSet, ClassOptimization optimizer) {
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
            for (int index = 0; index < 3; index++) {
                if (classTypes[index] != 0) {
                    String classType = getClassType(index);
                    int numberOfClasses = getNumberOfClassesForClassType(classType, studentsNumber);
                    int[] classTypeTimeDistributionInfo = optimizer.countDatePattern(subPartsForTypes, index);
                    String timePattern = getTimePattern(classTypeTimeDistributionInfo);
                    String datePattern = getDatePattern(classTypeTimeDistributionInfo);
                    if (!timePattern.equals("Undefined time pattern") && !datePattern.equals("Undefined date pattern")) {
                        xmlBuilder = createSubPartElementWithTimeAndDatePattern(
                                xmlBuilder, subject, classType, timePattern, datePattern);
                        xmlBuilder = createClassElements(
                                xmlBuilder, subject, classType, numberOfClasses, timePattern, datePattern);
                    }
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
                return "Undefined type";
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

    public String getTimePattern(int[] classTypeTimeDistributionInfo) {
        Optional<String> optionalTimePattern = Optional.ofNullable(
                optimizer.getTimePattern(classTypeTimeDistributionInfo));
        String timePattern = "Undefined time pattern";
        if (optionalTimePattern.isPresent())  {
            timePattern = optionalTimePattern.get();
        }
        return timePattern;
    }

    public String getDatePattern(int[] classTypeTimeDistributionInfo) {
        Optional<String> optionalDatePatterns = Optional.ofNullable(
                optimizer.getDatePattern(classTypeTimeDistributionInfo));
        String datePattern = "Undefined date pattern";
        if (optionalDatePatterns.isPresent())  {
            datePattern = optionalDatePatterns.get();
        }
        return datePattern;
    }

    public XMLBuilder createTimePrefElement(XMLBuilder xmlBuilder, String timePattern) {
        xmlBuilder = xmlBuilder.element("timePref")
                // pattern need some improvements after fixes will be made in ClassOptimizator class.
                .attribute("pattern", timePattern)
                .attribute("level", "1")
                .up();
        return xmlBuilder;
    }

    public XMLBuilder createDatePrefElement(XMLBuilder xmlBuilder, String datePattern) {
        xmlBuilder = xmlBuilder.element("datePref")
                .attribute("pattern", datePattern)
                .attribute("level", "1")
                .up();
        return xmlBuilder;
    }

    public XMLBuilder createSubPartElementWithTimeAndDatePattern(
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

    public XMLBuilder createClassElementWithTimeAndDatePattern(
            XMLBuilder xmlBuilder, String subject, String classType, int classNumber, String timePattern, String datePattern) {
        xmlBuilder = xmlBuilder.element("class")
                .attribute("subject", subject)
                .attribute("course", "1")
                .attribute("type", classType)
                .attribute("suffix", String.valueOf(classNumber));
        xmlBuilder = createTimePrefElement(xmlBuilder, timePattern);
        xmlBuilder = createDatePrefElement(xmlBuilder, datePattern);
        xmlBuilder = xmlBuilder.up();
        return xmlBuilder;
    }

    public XMLBuilder createClassElements(
            XMLBuilder xmlBuilder, String subject, String classType, int numberOfClasses, String timePattern, String datePattern) {
        for (int classNumber = 1; classNumber <= numberOfClasses; classNumber++) {
            xmlBuilder = createClassElementWithTimeAndDatePattern(
                    xmlBuilder, subject, classType, classNumber, timePattern, datePattern);
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
