package toXmlParser;


import com.jamesmurty.utils.XMLBuilder;
import parserUtility.ParserUtility;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;

public class CourseCatalog {

    private static final String QUERY_SQL = "SELECT fk_aine_id, ainekood, nimetus_est FROM TUNN_AINE";
    private static final ResultSet QUERY_RESULT_SET = ParserUtility.queryDataFromDatabase(QUERY_SQL);

    private static final String LECTURE_ID = "1";
    private static final String PRACTICE_ID = "2";
    private static final String EXERCISES_ID = "3";

    private static final int MAX_EAP = 3;
    private static final int MIN_EAP = 1;
    private Random random = new Random();

    /**
     * The function generates dummy credits (EAP) because now we have not information about it
     * @return dummy credit array
     */
    private int[] generateDummyCredits() {
        int lectureCredit = random.nextInt((MAX_EAP - MIN_EAP) + 1)
                + MIN_EAP;
        int practiceCredit = random.nextInt((MAX_EAP - MIN_EAP) + 1)
                + MIN_EAP;
        int exercisesCredit = random.nextInt((MAX_EAP - MIN_EAP) + 1)
                + MIN_EAP;
        int fixedCredit = lectureCredit + practiceCredit + exercisesCredit;

        return new int[] {fixedCredit, lectureCredit, practiceCredit, exercisesCredit};
    }

    private void printXMLFile(XMLBuilder courseCatalog) throws FileNotFoundException, TransformerException {
        new File("XMLFiles").mkdirs();
        PrintWriter writer = new PrintWriter(new FileOutputStream("XMLFiles/course_catalog.xml"));
        Properties outputProperties = new Properties();

        outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
        outputProperties.put(javax.xml.transform.OutputKeys.INDENT, "yes");
        outputProperties.put("{http://xml.apache.org/xslt}indent-amount", "2");
        courseCatalog.toWriter(writer, outputProperties);
    }

    public void buildXML() {
        try {
            XMLBuilder courseCatalog = XMLBuilder.create("courseCatalog")
                    .attribute("campus", "TTU")
                    .attribute("term", "Fall")
                    .attribute("year", "2018");

            while (QUERY_RESULT_SET.next()) {
                String subject = QUERY_RESULT_SET.getString("ainekood");
                String courseNumber = QUERY_RESULT_SET.getString("fk_aine_id");
                String title = QUERY_RESULT_SET.getString("nimetus_est");

                int[] dummyCredits = generateDummyCredits();

                XMLBuilder course = courseCatalog.element("course")
                        .attribute("subject", subject)
                        .attribute("courseNumber", courseNumber)
                        .attribute("title", title);

                course.element("courseCredit")
                        .attribute("creditType", "collegiate")
                        .attribute("creditUnitType", "semesterHours")
                        .attribute("creditFormat", "fixedUnit")
                        .attribute("fixedCredit", String.valueOf(dummyCredits[0]));

                course.element("subpartCredit")
                        .attribute("subpartId", LECTURE_ID)
                        .attribute("creditType", "collegiate")
                        .attribute("creditUnitType", "semesterHours")
                        .attribute("creditFormat", "fixedUnit")
                        .attribute("fixedCredit", String.valueOf(dummyCredits[1]));

                course.element("subpartCredit")
                        .attribute("subpartId", PRACTICE_ID)
                        .attribute("creditType", "collegiate")
                        .attribute("creditUnitType", "semesterHours")
                        .attribute("creditFormat", "fixedUnit")
                        .attribute("fixedCredit", String.valueOf(dummyCredits[2]));

                course.element("subpartCredit")
                        .attribute("subpartId", EXERCISES_ID)
                        .attribute("creditType", "collegiate")
                        .attribute("creditUnitType", "semesterHours")
                        .attribute("creditFormat", "fixedUnit")
                        .attribute("fixedCredit", String.valueOf(dummyCredits[3]));
            }

            printXMLFile(courseCatalog);

        } catch (ParserConfigurationException | SQLException | FileNotFoundException | TransformerException e) {
            e.printStackTrace();
        }
    }
}
