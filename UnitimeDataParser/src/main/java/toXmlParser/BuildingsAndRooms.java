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

    //Select
    private final String QUERY_SQL_ROOM_FEATURES = "SELECT * FROM R_RUUM_VARUSTUS";
    private final String QUERY_SQL_BUILDING = "SELECT * FROM R_HOONE";
    private final String QUERY_SQL_ROOMS = "SELECT * FROM R_RUUM";
    private final ResultSet QUERY_SQL_RESULT_BUILDING = ParserUtility.queryDataFromDatabase(QUERY_SQL_BUILDING);

    public void buildXML() throws SQLException {
        System.out.println(ParserUtility.queryDataFromDatabase(QUERY_SQL_ROOM_FEATURES).next());
        try {
            XMLBuilder xmlBuilder =
                    XMLBuilder.create("buildingsRooms")
                            .attribute("campus", "TTU")
                            .attribute("term", "Fall")
                            .attribute("year", "2018");
            while (QUERY_SQL_RESULT_BUILDING.next()) {
                String buildingExternalId = QUERY_SQL_RESULT_BUILDING.getString("bl_id");
                String buildingName = QUERY_SQL_RESULT_BUILDING.getString("nimetus");
                ResultSet QUERY_SQL_RESULT_ROOMS = ParserUtility.queryDataFromDatabase(QUERY_SQL_ROOMS);
                while (QUERY_SQL_RESULT_ROOMS.next()) {
                    ResultSet QUERY_SQL_RESULT_ROOM_FEATURES = ParserUtility.queryDataFromDatabase(QUERY_SQL_ROOM_FEATURES);
                    String roomFeaturesId = QUERY_SQL_RESULT_ROOM_FEATURES.getString("fk_room_id");
                    String roomExternalId = QUERY_SQL_RESULT_ROOMS.getString("fk_bl_id");
                    String roomId = QUERY_SQL_RESULT_ROOMS.getString("ruum_id");
                    String roomNumber = QUERY_SQL_RESULT_ROOMS.getString("rm_id");
                    int roomCapacity = QUERY_SQL_RESULT_ROOMS.getInt("kohtade_arv");
                    String[] buildingAbbreviation = QUERY_SQL_RESULT_ROOMS.getString("kood").split("-");
                    String roomClassification = QUERY_SQL_RESULT_ROOMS.getString("nimetus");
                    while (QUERY_SQL_RESULT_ROOM_FEATURES.next()) {
                        String features = QUERY_SQL_RESULT_ROOM_FEATURES.getString("fk_varustus_kood");
                        if (roomExternalId.equals(buildingExternalId)) {
                            if (roomFeaturesId.equals(roomId)) {
                                xmlBuilder.element("building")
                                        .attribute("abbreviation", buildingAbbreviation[0])
                                        .attribute("externalId", buildingExternalId)
                                        .attribute("locationX", String.valueOf(0))
                                        .attribute("locationY", String.valueOf(0))
                                        .attribute("name", buildingName)
                                        .element("room")
                                        .attribute("externalId", roomId)
                                        .attribute("locationX", String.valueOf(0))
                                        .attribute("locationY", String.valueOf(0))
                                        .attribute("roomClassification", roomClassification)
                                        .attribute("roomNumber", roomNumber)
                                        .attribute("roomCapacity", String.valueOf(roomCapacity))
                                        .element("roomDepartments")
                                        .element("assigned")
                                        .attribute("departmnetNumber", String.valueOf(1001))
                                        .attribute("percent", "100")
                                        .up()
                                        .up()
                                        .element("roomFeatures")
                                        .element("roomFeature")
                                        .attribute("feature", features)
                                        .attribute("value", features)
                                        .up()
                                        .up();
                            }

                        }
                    }
                }
            }
            PrintWriter writer = new PrintWriter(new FileOutputStream("XMLFiles/rooms.xml"));
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
