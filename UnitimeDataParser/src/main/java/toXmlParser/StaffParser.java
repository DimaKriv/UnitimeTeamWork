package toXmlParser;

import com.jamesmurty.utils.XMLBuilder;
import com.sun.org.apache.regexp.internal.RE;
import parserUtility.ParserUtility;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class StaffParser {
    ParserUtility utility;
    private String QUERY_SQL_STAFF;
    private ResultSet QUERY_SQL_RESULT_STAFF;

    public StaffParser(String querySql, ResultSet queryResultSet)  {
        utility = new ParserUtility();
        this.QUERY_SQL_STAFF = querySql;
        this.QUERY_SQL_RESULT_STAFF = queryResultSet;
    }

    public StaffParser() throws SQLException {
        QUERY_SQL_STAFF = "select O1.*, I.* from (SELECT O.*, (lower(substr(STRUKTURI_UKSUS, instr(STRUKTURI_UKSUS, '  -  ')+5, 1)) || substr(STRUKTURI_UKSUS, instr(STRUKTURI_UKSUS, '  -  ')+6)) AS name FROM OPPEJOUDUDE_VALJAVOTE as O) as O1 inner join Institudid as I on I.nimetus=O1.name";
        Connection connection = utility.connectToDatabase();
        Statement statement = utility.createStatement(connection);
        QUERY_SQL_RESULT_STAFF = utility.queryDataFromDatabase(QUERY_SQL_STAFF, statement);
    }

    public XMLBuilder createStaffParserBuilder(String campus, String term, String year) throws ParserConfigurationException {
        XMLBuilder xmlBuilder =
                XMLBuilder.create("staff")
                        .attribute("campus", campus)
                        .attribute("term", term)
                        .attribute("year", year);

        return xmlBuilder;
    }

    public XMLBuilder buildXML(String campus, String term, String year) throws ParserConfigurationException, SQLException {
        XMLBuilder xmlBuilder = createStaffParserBuilder(campus, term, year);

        while (QUERY_SQL_RESULT_STAFF.next()) {
            int externalId = 0;
            String firstName = QUERY_SQL_RESULT_STAFF.getString("NIMI");
            String lastName = QUERY_SQL_RESULT_STAFF.getString("PEREKONNANIMI");
            String positionType = QUERY_SQL_RESULT_STAFF.getString("AMET");
            String departmentCode = QUERY_SQL_RESULT_STAFF.getString("EXTERNAL_ID");
            xmlBuilder.element("staffMember")
                    .attribute("externalId", String.valueOf(++externalId))
                    .attribute("firstName", firstName)
                    .attribute("lastName", lastName)
                    .attribute("positionType", positionType)
                    .attribute("department", String.valueOf(departmentCode));
        }
        return xmlBuilder;
    }

    public void writeXML(XMLBuilder xmlBuilder) throws FileNotFoundException, TransformerException {
        utility.writeToXMLFile(xmlBuilder, "Staff.xml");
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
