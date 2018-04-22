package toXmlParser;

import com.jamesmurty.utils.XMLBuilder;
import parserUtility.ParserUtility;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Departments {

    private String querySql;
    private ResultSet queryResultSet;

    public Departments() {
        querySql = "SELECT * FROM INSTITUDID";
        queryResultSet = ParserUtility.queryDataFromDatabase(querySql);
    }

    public Departments(String querySql, ResultSet queryResultSet) {
        this.querySql = querySql;
        this.queryResultSet = queryResultSet;
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
            String abbreviation = queryResultSet.getString("ABBV");
            String name = queryResultSet.getString("NIMETUS");
            String deptCode = queryResultSet.getString("EXTERNAL_ID");
            xmlBuilder.element("department")
                        .attribute("abbreviation", abbreviation)
                        .attribute("name", name)
                        .attribute("deptCode", deptCode);
        }
        return xmlBuilder;
    }

    public void writeXML(XMLBuilder xmlBuilder) throws FileNotFoundException, TransformerException {
        ParserUtility.writeToXMLFile(xmlBuilder, "departments.xml");
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
