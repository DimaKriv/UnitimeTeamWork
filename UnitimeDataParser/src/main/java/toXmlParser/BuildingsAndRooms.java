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
    private final String QUERY_SQL_BUILDING = "SELECT V.fk_varustus_kood, H.bl_id, H.nimetus AS buildingName, R.kohtade_arv, R.rm_id, R.ruum_id, R.kood, R.nimetus AS roomName FROM R_HOONE AS H INNER JOIN R_RUUM AS R ON H.bl_id=R.fk_bl_id INNER JOIN R_RUUM_VARUSTUS AS V ON V.fk_ruum_id=R.ruum_id";
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
                String buildingName = QUERY_SQL_RESULT_BUILDING.getString("buildingName");
                String roomExternalId = QUERY_SQL_RESULT_BUILDING.getString("ruum_id");
                String buildingAbbreviation = QUERY_SQL_RESULT_BUILDING.getString("kood").split("-")[0];
                String roomCapacity = QUERY_SQL_RESULT_BUILDING.getString("kohtade_arv");
                String roomNumber = QUERY_SQL_RESULT_BUILDING.getString("rm_id");
                String roomClassification = QUERY_SQL_RESULT_BUILDING.getString("roomName");
                if (roomClassification.length() > 20){
                    roomClassification = roomClassification.substring(0, 20);
                }
                String roomFeatures = QUERY_SQL_RESULT_BUILDING.getString("fk_varustus_kood");
                String roomFeaturesValue = roomFeatures;
                if (roomFeatures.length() > 20){
                    roomFeaturesValue = roomFeatures.substring(0, 20);
                }
                xmlBuilder.element("building")
                        .attribute("abbreviation", buildingAbbreviation)
                        .attribute("externalId", buildingExternalId)
                        .attribute("locationX", String.valueOf(0))
                        .attribute("locationY", String.valueOf(0))
                        .attribute("name", buildingName)
                        .element("room")
                        .attribute("externalId", roomExternalId)
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
                        .attribute("feature", roomFeatures)
                        .attribute("value", roomFeaturesValue)
                        .up()
                        .up();
            }
            PrintWriter writer = new PrintWriter(new FileOutputStream("XMLFiles/BuildingAndRooms.xml"));
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
