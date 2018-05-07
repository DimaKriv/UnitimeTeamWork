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
import java.util.List;
import java.util.Optional;

public class Preferences {

    private ParserUtility utility;
    private String querySql;
    private ResultSet queryResultSet;
    private ClassOptimizator optimizer;

    public Preferences() throws SQLException {
        utility = new ParserUtility();
        Connection connection = utility.connectToDatabase();
        Statement statement = utility.createStatement(connection);
        querySql = "select inimeste_arv,ainekood,nimetus,loeng,praktikum,harjutus from U_COURSEOFFERING";
        queryResultSet = utility.queryDataFromDatabase(querySql, statement);
        optimizer = new ClassOptimizator();
    }

    public Preferences(String querySql, ResultSet queryResultSet) {
        utility = new ParserUtility();
        this.querySql = querySql;
        this.queryResultSet = queryResultSet;
        optimizer = new ClassOptimizator();
    }

    public XMLBuilder createDepartmentsElementBuilder(String campus, String term, String year) throws ParserConfigurationException {
        XMLBuilder xmlBuilder =
            XMLBuilder.create("departments")
                .attribute("campus", campus)
                .attribute("term", term)
                .attribute("year", year);

        return xmlBuilder;
    }

    public XMLBuilder buildXML(String campus, String term, String year) throws ParserConfigurationException, SQLException {
        XMLBuilder xmlBuilder = createDepartmentsElementBuilder(campus, term, year);

        while (queryResultSet.next()) {
            String subject = queryResultSet.getString("ainekood");
            String lectureHoursPerWeek = queryResultSet.getString("loeng");
            String laboratoryHoursPerWeek = queryResultSet.getString("praktikum");
            String recitationHoursPerWeek = queryResultSet.getString("harjutus");
            int studentsNumber = Integer.parseInt(queryResultSet.getString("inimeste_arv"));
            int[] classTypes = optimizer.makeIntMassiveFromStringInput(lectureHoursPerWeek, laboratoryHoursPerWeek, recitationHoursPerWeek);
            int[][] subPartsForTypes = optimizer.makeCourceOfferringData(classTypes);
            for (int index = 0; index < 2; index++) {
                if (classTypes[index] != 0) {
                    String type;
                    int numberOfClasses;
                    switch (index) {
                        case 0:
                            type = "Lec";
                            numberOfClasses = countNumberOfClasses(studentsNumber, 200);
                            break;
                        case 1:
                            type = "Lab";
                            numberOfClasses = countNumberOfClasses(studentsNumber, 20);
                            break;
                        case 2:
                            type = "Rec";
                            numberOfClasses = countNumberOfClasses(studentsNumber, 20);
                            break;
                        default:
                            numberOfClasses = 0;
                            type = "Undefined";
                            break;
                    }
                    ArrayList<Integer> classTypeTimeDistributionInfo = optimizer.countDatePattern(subPartsForTypes, index);
                    String[] timePatterns = optimizer.getTimePattern(classTypeTimeDistributionInfo);
                    Optional<String[]> optionalTimePatterns = Optional.ofNullable(timePatterns);
                    String timePattern = "Empty time pattern";
                    if (optionalTimePatterns.isPresent())  {
                        timePattern = timePatterns[0];
                    }
                    String[] datePatterns = optimizer.getDatePattern(classTypeTimeDistributionInfo);
                    Optional<String[]> optionalDatePatterns = Optional.ofNullable(datePatterns);
                    String datePattern = "Empty date pattern";
                    if (optionalDatePatterns.isPresent())  {
                        datePattern = datePatterns[0];
                    }
                    xmlBuilder = xmlBuilder.element("subpart")
                        .attribute("subject", subject)
                        .attribute("course", "1")
                        .attribute("type", type)
                            .element("timePref")
                            // pattern need some improvements after fixes will be made in ClassOptimizator class.
                            .attribute("pattern", timePattern)
                            .attribute("level", "0")
                            .up()
                            .element("datePref")
                            .attribute("pattern", datePattern)
                            .attribute("level", "0")
                            .up()
                        .up();
                    for (int classNumber = 1; classNumber <= numberOfClasses; classNumber++) {
                        xmlBuilder = xmlBuilder.element("class")
                            .attribute("subject", subject)
                            .attribute("course", "1")
                            .attribute("type", type)
                            .attribute("suffix", String.valueOf(classNumber))
                            .up();
                    }
                }
            }
        }
        xmlBuilder = xmlBuilder.up();
        return xmlBuilder;
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
