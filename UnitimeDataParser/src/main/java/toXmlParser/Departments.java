package toXmlParser;

import com.jamesmurty.utils.XMLBuilder;
import parserUtility.ParserUtility;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class Departments {

    private final String QUERY_SQL = "SELECT * FROM INSTITUDID";
    private final ResultSet QUERY_RESULT_SET = ParserUtility.queryDataFromDatabase(QUERY_SQL);

    public void buildXML() {
        try {
            XMLBuilder xmlBuilder =
                    XMLBuilder.create("departments")
                        .attribute("campus", "TTU")
                        .attribute("term", "Fall")
                        .attribute("year", "2018");

            while (QUERY_RESULT_SET.next()) {
                String externalId = QUERY_RESULT_SET.getString("externalId");
                String abbreviation = QUERY_RESULT_SET.getString("abbreviation");
                String name = QUERY_RESULT_SET.getString("name");
                String deptCode = QUERY_RESULT_SET.getString("deptCode");
                xmlBuilder.element("department")
                            .attribute("externalId", externalId)
                            .attribute("abbreviation", abbreviation)
                            .attribute("name", name)
                            .attribute("deptCode", deptCode)
                            .attribute("allowEvents", "true");
            }

            PrintWriter writer = new PrintWriter(new FileOutputStream("XMLFiles/departments.xml"));
            Properties outputProperties = new Properties();
            outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            xmlBuilder.toWriter(writer, outputProperties);

        } catch (ParserConfigurationException | SQLException | FileNotFoundException | TransformerException e) {
            e.printStackTrace();
        }
    }
}
