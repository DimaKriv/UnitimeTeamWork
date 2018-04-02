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

public class BuildingsAndRooms {

    private final String QUERY_SQL_ROOM_FEATURES = "SELECT * FROM R_VARUSTUS";
    private final String QUERY_SQL_BUILDING = "SELECT * FROM R_HOONE";
    private final String QUERY_SQL_ROOMS = "SELECT * FROM R_RUUM WHERE R_RUUM.fk_bl_id=";
    //private final ResultSet QUERY_SQL_RESULT_ROOMS = ParserUtility.queryDataFromDatabase(QUERY_SQL_ROOMS);
    private final ResultSet QUERY_SQL_RESULT_BUILDING = ParserUtility.queryDataFromDatabase(QUERY_SQL_BUILDING);
    private final ResultSet QUERY_RESULT_SET = ParserUtility.queryDataFromDatabase(QUERY_SQL_ROOM_FEATURES);

    public void buildXML() throws SQLException {
        try {
            XMLBuilder xmlBuilder =
                    XMLBuilder.create("buildingsRooms")
                            .attribute("campus", "TTU")
                            .attribute("term", "Fall")
                            .attribute("year", "2018");
            while (QUERY_SQL_RESULT_BUILDING.next()){
                String externalId = QUERY_SQL_RESULT_BUILDING.getString("bl_id");
                String name = QUERY_SQL_RESULT_BUILDING.getString("nimetus");
                ResultSet QUERY_SQL_RESULT_ROOMS = ParserUtility.queryDataFromDatabase(QUERY_SQL_ROOMS + "'" + externalId + "'");
                xmlBuilder.element("building")
                        .attribute("externalId", externalId)
                        .attribute("abbreviation", externalId)
                        .attribute("locationX", String.valueOf(0))
                        .attribute("locationY", String.valueOf(0))
                        .attribute("name", name);
                while (QUERY_SQL_RESULT_ROOMS.next()){
                    String roomExternalId = QUERY_SQL_RESULT_ROOMS.getString("fk_bl_id");
                    int capacity = QUERY_SQL_RESULT_ROOMS.getInt("kohtade_arv");
                    String roomNumber = QUERY_SQL_RESULT_ROOMS.getString("ruum_id");
                    String roomClassification = QUERY_SQL_RESULT_ROOMS.getString("nimetus");
                    xmlBuilder.element("room")
                            .attribute("externalId", roomExternalId)
                            .attribute("locationX", String.valueOf(0))
                            .attribute("locationY", String.valueOf(0))
                            .attribute("roomNumber", roomNumber)
                            .attribute("roomClassification", roomClassification)
                            .attribute("capacity", String.valueOf(capacity));
                    xmlBuilder.element("roomDepartments")
                            .element("assigned")
                            .attribute("departmentNumber", String.valueOf(1001))
                            .attribute("percent", String.valueOf(100));
                    while (QUERY_RESULT_SET.next()) {
                        String varustusKood = QUERY_RESULT_SET.getString("VARUSTUS_KOOD");
                        xmlBuilder.element("roomFeatures")
                                .attribute("feature", varustusKood)
                                .attribute("value", varustusKood);
                    }
                }
            }
            PrintWriter writer = new PrintWriter(new FileOutputStream("XMLFiles/buildingsAndRooms.xml"));
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
