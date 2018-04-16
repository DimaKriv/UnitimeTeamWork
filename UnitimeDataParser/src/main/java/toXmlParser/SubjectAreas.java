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

public class SubjectAreas {

    private static final int FIRST_DEPARTMENT = 1001;
    private static final int LAST_DEPARTMENT = 1020;

    private static final String QUERY_SQL = "SELECT * FROM SUBJECT_AREAS_TTU";
    private static final ResultSet QUERY_RESULT_SET = ParserUtility.queryDataFromDatabase(QUERY_SQL);

    public void buildXML() {
        try {
            XMLBuilder xmlBuilder = XMLBuilder.create("subjectAreas")
                    .attribute("campus", "TTU")
                    .attribute("term", "Fall")
                    .attribute("year", "2018");

            int currentDepartment = FIRST_DEPARTMENT;

            while (QUERY_RESULT_SET.next() && currentDepartment <= LAST_DEPARTMENT) {
                String externalID = QUERY_RESULT_SET.getString("EXTERNAL_ID");
                String abbreviation = QUERY_RESULT_SET.getString("ABBV");
                String title = QUERY_RESULT_SET.getString("TITLE");
                String department = String.valueOf(currentDepartment);

                xmlBuilder.element("subjectArea")
                        .attribute("externalId", externalID)
                        .attribute("abbreviation", abbreviation)
                        .attribute("title", title)
                        .attribute("department", department);

                currentDepartment++;

            }

            new File("XMLFiles").mkdirs();

            PrintWriter writer = new PrintWriter(new FileOutputStream("XMLFiles/subject_areas.xml"));
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
