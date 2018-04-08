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

public class SubjectAreas {

    private static final int FIRST_DEPARTMENT = 1001;
    private static final int LAST_DEPARTMENT = 1020;

    private static final String QUERY_SQL = "SELECT ainekood, nimetus_est FROM TUNN_AINE";
    private static final ResultSet QUERY_RESULT_SET = ParserUtility.queryDataFromDatabase(QUERY_SQL);

    public void buildXML() {
        try {
            XMLBuilder xmlBuilder = XMLBuilder.create("subjectAreas")
                    .attribute("campus", "TTU")
                    .attribute("term", "Fall")
                    .attribute("year", "2018");

            Random random = new Random();

            while (QUERY_RESULT_SET.next()) {
                String abbreviation = QUERY_RESULT_SET.getString("ainekood");
                String title = QUERY_RESULT_SET.getString("nimetus_est");
                String department = String.valueOf(random.nextInt((LAST_DEPARTMENT - FIRST_DEPARTMENT) + 1)
                        + FIRST_DEPARTMENT);


                xmlBuilder.element("subjectArea")
                        .attribute("abbreviation", abbreviation)
                        .attribute("title", title)
                        .attribute("department", department);
            }

            new File("XMLFiles").mkdirs();

            PrintWriter writer = new PrintWriter(new FileOutputStream("XMLFiles/subjectAreas.xml"));
            Properties outputProperties = new Properties();
            outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            outputProperties.put(javax.xml.transform.OutputKeys.INDENT, "yes");
            outputProperties.put("{http://xml.apache.org/xslt}indent-amount", "2");
            xmlBuilder.toWriter(writer, outputProperties);


        } catch (ParserConfigurationException | SQLException | FileNotFoundException | TransformerException e) {
            e.printStackTrace();
        }
    }
}
