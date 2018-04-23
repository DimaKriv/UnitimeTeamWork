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

//    private static final String QUERY_SQL = "SELECT fk_aine_id, ainekood, nimetus_est FROM TUNN_AINE WHERE Length(ainekood)<=10";
//    private static final ResultSet QUERY_RESULT_SET = ParserUtility.queryDataFromDatabase(QUERY_SQL);
//
//    private static final int DEFAULT_FAKE_EAP = 5;
//
//    private void printXMLFile(XMLBuilder courseCatalog) throws FileNotFoundException, TransformerException {
//        new File("XMLFiles").mkdirs();
//        PrintWriter writer = new PrintWriter(new FileOutputStream("XMLFiles/course_catalog.xml"));
//        Properties outputProperties = new Properties();
//
//        outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
//        outputProperties.put(javax.xml.transform.OutputKeys.INDENT, "yes");
//        outputProperties.put("{http://xml.apache.org/xslt}indent-amount", "2");
//        courseCatalog.toWriter(writer, outputProperties);
//    }
//
//    public void buildXML() {
//        try {
//            XMLBuilder courseCatalog = XMLBuilder.create("courseCatalog")
//                    .attribute("campus", "TTU")
//                    .attribute("term", "Fall")
//                    .attribute("year", "2018");
//
//            while (QUERY_RESULT_SET.next()) {
//                String subject = QUERY_RESULT_SET.getString("ainekood");
//                String courseNumber = QUERY_RESULT_SET.getString("fk_aine_id");
//                String title = QUERY_RESULT_SET.getString("nimetus_est");
//
//                XMLBuilder course = courseCatalog.element("course")
//                        .attribute("subject", subject)
//                        .attribute("courseNumber", courseNumber)
//                        .attribute("title", title);
//
//                course.element("courseCredit")
//                        .attribute("creditType", "collegiate")
//                        .attribute("creditUnitType", "semesterHours")
//                        .attribute("creditFormat", "fixedUnit")
//                        .attribute("fixedCredit", String.valueOf(DEFAULT_FAKE_EAP));
//            }
//
//            printXMLFile(courseCatalog);
//
//        } catch (ParserConfigurationException | SQLException | FileNotFoundException | TransformerException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("Course catalog XML is successfully created");
//    }

    private static final int DEFAULT_FAKE_EAP = 5;

    private String querySql;
    private ResultSet queryResultSet;

    public CourseCatalog() {
        querySql = "SELECT fk_aine_id, ainekood, nimetus_est FROM TUNN_AINE WHERE Length(ainekood)<=10";
        queryResultSet = ParserUtility.queryDataFromDatabase(querySql);
    }

    public CourseCatalog(String querySql, ResultSet queryResultSet) {
        this.querySql = querySql;
        this.queryResultSet = queryResultSet;
    }

    public XMLBuilder createCourseCatalogElementBuilder(String campus, String term, String year) throws ParserConfigurationException {
        return XMLBuilder.create("departments")
                .attribute("campus", campus)
                .attribute("term", term)
                .attribute("year", year);
    }

    public XMLBuilder buildXML(String campus, String term, String year) throws ParserConfigurationException, SQLException {
        XMLBuilder xmlBuilder = createCourseCatalogElementBuilder(campus, term, year);

        while (queryResultSet.next()) {
            String subject = queryResultSet.getString("ainekood");
            String courseNumber = queryResultSet.getString("fk_aine_id");
            String title = queryResultSet.getString("nimetus_est");

            XMLBuilder course = xmlBuilder.element("course")
                    .attribute("subject", subject)
                    .attribute("courseNumber", courseNumber)
                    .attribute("title", title);

            course.element("courseCredit")
                    .attribute("creditType", "collegiate")
                    .attribute("creditUnitType", "semesterHours")
                    .attribute("creditFormat", "fixedUnit")
                    .attribute("fixedCredit", String.valueOf(DEFAULT_FAKE_EAP));
        }

        return xmlBuilder;
    }

    public void writeXML(XMLBuilder xmlBuilder) throws FileNotFoundException, TransformerException {
        ParserUtility.writeToXMLFile(xmlBuilder, "course_catalog.xml");
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
