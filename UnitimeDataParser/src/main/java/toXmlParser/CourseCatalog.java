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

public class CourseCatalog {

    private static final String QUERY_SQL = "SELECT fk_aine_id, ainekood, nimetus_est FROM TUNN_AINE" +
            "WHERE Length(ainekood)<=10";
    private static final ResultSet QUERY_RESULT_SET = ParserUtility.queryDataFromDatabase(QUERY_SQL);

    private static final int DEFAULT_FAKE_EAP = 5;

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

                XMLBuilder course = courseCatalog.element("course")
                        .attribute("subject", subject)
                        .attribute("courseNumber", courseNumber)
                        .attribute("title", title);

                course.element("courseCredit")
                        .attribute("creditType", "collegiate")
                        .attribute("creditUnitType", "semesterHours")
                        .attribute("creditFormat", "fixedUnit")
                        .attribute("fixedCredit", String.valueOf(DEFAULT_FAKE_EAP));
            }

            printXMLFile(courseCatalog);

        } catch (ParserConfigurationException | SQLException | FileNotFoundException | TransformerException e) {
            e.printStackTrace();
        }
    }
}
