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

public class CourseOffering {

    private ParserUtility utility;
    private String querySql;
    private ResultSet queryResultSet;

    public CourseOffering() throws SQLException {
        utility = new ParserUtility();
        Connection connection = utility.connectToDatabase();
        Statement statement = utility.createStatement(connection);
        querySql = "select inimeste_arv,ainekood,nimetus,loeng,praktikum,harjutus from U_COURSEOFFERING";
        queryResultSet = utility.queryDataFromDatabase(querySql, statement);
    }

    public CourseOffering(String querySql, ResultSet queryResultSet) {
        utility = new ParserUtility();
        this.querySql = querySql;
        this.queryResultSet = queryResultSet;
    }

    public XMLBuilder createCourseOfferingElementBuilder(String campus, String term, String year) throws ParserConfigurationException {
        XMLBuilder xmlBuilder =
                XMLBuilder.create("offerings")
                        .attribute("campus", campus)
                        .attribute("term", term)
                        .attribute("year", year);
        return xmlBuilder;
    }

    public XMLBuilder buildXML(String campus, String term, String year) throws ParserConfigurationException, SQLException {
        XMLBuilder xmlBuilder = createCourseOfferingElementBuilder(campus, term, year);
        int i = 1;
        while (queryResultSet.next()) {
            String limitStr = queryResultSet.getString("inimeste_arv");
            String subject = queryResultSet.getString("nimetus");
            String courseNumber = queryResultSet.getString("ainekood");
            String lecture = queryResultSet.getString("loeng");
            String paktikum = queryResultSet.getString("praktikum");
            String harjutus = queryResultSet.getString("harjutus");
            ClassOptimization optimizator = new ClassOptimization();
            int[][] subpartMin = optimizator.makeCourceOfferringData(
                    optimizator.makeIntMassiveFromStringInput(lecture, paktikum, harjutus));
            int limit;
            try {
                limit = Integer.parseInt(limitStr);
            } catch (Exception e) {
                System.out.println("Not valid limit");
                continue;
            }
            XMLBuilder out = xmlBuilder.element("offering")
                    .attribute("id", String.valueOf(i++))
                    .attribute("offered", "true")
                    .attribute("action", "insert|update|delete")
                    .element("course")
                    .attribute("subject", courseNumber)
                    .attribute("courseNbr", 1 + "")
                    .attribute("controlling", "true")
                    .attribute("title", subject)
                    .element("courseCredit")
                    .attribute("creditType", "collegiate")
                    .attribute("creditUnitType", "semesterHours")
                    .attribute("creditFormat", "creditFormat")
                    .attribute("fixedCredit", "2")
                    .up()
                    .up()
                    .element("config")
                    .attribute("name", "1")
                    .attribute("limit", limit + "");
            createCourseSubject(subpartMin, out);
            createCourseClass(subpartMin, out, limit);
        }
        return xmlBuilder;
    }

    public void writeXML(XMLBuilder xmlBuilder) throws FileNotFoundException, TransformerException {
        utility.writeToXMLFile(xmlBuilder, "courseOfferings.xml");
    }

    public void createXMLFile(String campus, String term, String year) {
        try {
            XMLBuilder xmlBuilder = buildXML(campus, term, year);
            writeXML(xmlBuilder);
        } catch (ParserConfigurationException | SQLException | FileNotFoundException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public void createCourseSubject(int[][] input, XMLBuilder out) {
        String[] type = new String[]{"Lec", "Lab", "Rec"};
        for (int i = 0; i < input.length; i++) {
            ClassOptimization optimizator = new ClassOptimization();
            int[] classConfig = optimizator.countDatePattern(input, i);
            if (isExist(classConfig, optimizator)) {
                out = out.e("subpart").attribute("type", type[i])
                        .a("minPerWeek", minPerWeek(classConfig, optimizator) + "");
                out = out.up();
            }
        }
    }

    public void createCourseClass(int[][] input, XMLBuilder out, int people) {
        String[] type = new String[]{"Lec", "Lab", "Rec"};
        int peopleNotInClass = people;
        int suffixOfClass = 1;
        for (int i = 0; i < input.length; ) {
            ClassOptimization optimizator = new ClassOptimization();
            int[] classConfig = optimizator.countDatePattern(input, i);
            if (!isExist(classConfig, optimizator)) {
                i++;
                continue;
            }
            int limitClassCapacity;
            if (i == 0) limitClassCapacity = 200;
            else limitClassCapacity = 20;
            out = out.element("class")
                    .attribute("type", type[i]).attribute("suffix"
                            , suffixOfClass + "")
                    .attribute("limit", limitClassCapacity + "").up();
            if ((peopleNotInClass - limitClassCapacity) > 0) {
                peopleNotInClass -= limitClassCapacity;
                suffixOfClass++;
            } else {
                i++;
                peopleNotInClass = people;
                suffixOfClass = 1;
            }
        }
    }

    public int minPerWeek(int[] classConfig, ClassOptimization op) {
        int[] numberAndMinutesOfClass = op.getNumberAndMinutesOfClass(classConfig);
        return numberAndMinutesOfClass[0] * numberAndMinutesOfClass[1];
    }

    public boolean isExist(int[] classConfig, ClassOptimization op) {
        if (op.getDatePattern(classConfig).equals(ClassOptimization.NO_DATE_PATTERN)
                || op.getTimePattern(classConfig).equals(ClassOptimization.NO_TIME_PATTERN))
            return false;
        else return true;
    }
}
