package toXmlParser;

import com.jamesmurty.utils.XMLBuilder;
import parserUtility.ParserUtility;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class BuildingsAndRooms {

    //Select
    private String QUERY_SQL_BUILDING;
    private ResultSet QUERY_SQL_RESULT_BUILDING;
    private ParserUtility utility;


    public BuildingsAndRooms(String querySql, ResultSet queryResultSet) {
        utility = new ParserUtility();
        this.QUERY_SQL_BUILDING = querySql;
        this.QUERY_SQL_RESULT_BUILDING = queryResultSet;
    }

    public BuildingsAndRooms() throws SQLException{
        utility = new ParserUtility();
        QUERY_SQL_BUILDING = "SELECT V.FK_VARUSTUS_KOOD, H.bl_id, H.nimetus AS buildingName, R.kohtade_arv, R.rm_id, R.ruum_id, R.kood, R.nimetus AS roomName FROM R_HOONE AS H INNER JOIN R_RUUM AS R ON H.bl_id=R.fk_bl_id LEFT JOIN R_RUUM_VARUSTUS AS V ON V.fk_ruum_id=R.ruum_id WHERE H.bl_id NOT IN ('KOPLI116', 'AKAD21F', 'AKAD21B', 'KOPLI101', 'KJARVE2')";
        Connection connection = utility.connectToDatabase();
        Statement statement = utility.createStatement(connection);
        QUERY_SQL_RESULT_BUILDING = utility.queryDataFromDatabase(QUERY_SQL_BUILDING, statement);
    }

    public XMLBuilder createBuildingAndRoomsElementBuilder(String campus, String term, String year) throws ParserConfigurationException {
        XMLBuilder xmlBuilder =
                XMLBuilder.create("buildingsRooms")
                        .attribute("campus", campus)
                        .attribute("term", term)
                        .attribute("year", year);

        return xmlBuilder;
    }

    public XMLBuilder buildXML(String campus, String term, String year) throws ParserConfigurationException, SQLException {
        XMLBuilder xmlBuilder = createBuildingAndRoomsElementBuilder(campus, term, year);

        while (QUERY_SQL_RESULT_BUILDING.next()) {
            String departmentCode = "1001";
            String roomFeature = "";
            String buildingExternalId = QUERY_SQL_RESULT_BUILDING.getString("bl_id");
            String buildingName = QUERY_SQL_RESULT_BUILDING.getString("buildingName");
            String roomExternalId = QUERY_SQL_RESULT_BUILDING.getString("ruum_id");
            String buildingAbbreviation = QUERY_SQL_RESULT_BUILDING.getString("kood").split("-")[0];
            String roomCapacity = QUERY_SQL_RESULT_BUILDING.getString("kohtade_arv");
            String roomNumber = QUERY_SQL_RESULT_BUILDING.getString("rm_id");
            String roomClassification = QUERY_SQL_RESULT_BUILDING.getString("roomName");
            roomFeature = QUERY_SQL_RESULT_BUILDING.getString("FK_VARUSTUS_KOOD");
            if (roomFeature == null){
                roomFeature = "";
            }
            if (roomFeature.length() > 20){
                roomFeature = roomFeature.substring(0,20);
            }
            if (roomClassification.equals("Üldkasutatav auditoorium")){
                roomClassification = "Uld.auditoorium";
            }
            if (roomClassification.equals("Eriotstarbeline auditoorium")){
                roomClassification = "Eri.auditoorium";
            }
            if (roomClassification.equals("Kategooria: õppetegevus")){
                roomClassification = "õppetegevus";
            }
            if (buildingName.equals("Õppehoone U02 (B korpus)")){
                buildingName = "Õppehoone U02 (B)";
            }
            if (buildingName.equals("Õppehoone U04 (B korpus)")){
                buildingName = "Õppehoone U04 (B)";
            }
            if (buildingName.equals("Õppehoone U05 (B korpus)")){
                buildingName = "Õppehoone U05 (B)";
            }
            if (buildingName.equals("Õppehoone U06 (A korpus)")){
                buildingName = "Õppehoone U02 (A)";
            }
//                String roomFeatures = QUERY_SQL_RESULT_BUILDING.getString("fk_varustus_kood");
//                String roomFeaturesValue = roomFeatures;
//                if (roomFeatures.length() > 20){
//                    roomFeaturesValue = roomFeatures.substring(0, 20);
//                    roomFeatures = roomFeatures.substring(0, 20);
//                }
            if (buildingAbbreviation.equals("SOC")){
                departmentCode = "1017";
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
                        .attribute("capacity", String.valueOf(roomCapacity))
                        .element("roomDepartments")
                        .element("assigned")
                        .attribute("departmentNumber", departmentCode)
                        .attribute("percent", "100")
                        .up()
                        .element("scheduling")
                        .attribute("departmentNumber", "1002")
                        .attribute("percent", "50")
                        .up()
                        .up()
                        .element("roomFeatures")
                        .element("roomFeature")
                        .attribute("feature", roomFeature)
                        .attribute("value", roomFeature)
                        .up()
                        .up();
            }
            if (buildingAbbreviation.equals("EIK")){
                departmentCode = "1005";
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
                        .attribute("capacity", String.valueOf(roomCapacity))
                        .element("roomDepartments")
                        .element("assigned")
                        .attribute("departmentNumber", departmentCode)
                        .attribute("percent", "100")
                        .up()
                        .element("scheduling")
                        .attribute("departmentNumber", "1002")
                        .attribute("percent", "50")
                        .up()
                        .up()
                        .element("roomFeatures")
                        .element("roomFeature")
                        .attribute("feature", roomFeature)
                        .attribute("value", roomFeature)
                        .up()
                        .up();
            }
            if (buildingAbbreviation.equals("SCI")){
                departmentCode = "1014";
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
                        .attribute("capacity", String.valueOf(roomCapacity))
                        .element("roomDepartments")
                        .element("assigned")
                        .attribute("departmentNumber", departmentCode)
                        .attribute("percent", "100")
                        .up()
                        .element("scheduling")
                        .attribute("departmentNumber", "1002")
                        .attribute("percent", "50")
                        .up()
                        .up()
                        .element("roomFeatures")
                        .element("roomFeature")
                        .attribute("feature", roomFeature)
                        .attribute("value", roomFeature)
                        .up()
                        .up();
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
                    .attribute("capacity", String.valueOf(roomCapacity))
                    .element("roomFeatures")
                    .element("roomFeature")
                    .attribute("feature", roomFeature)
                    .attribute("value", roomFeature)
                    .up()
                    .up();
        }
        return xmlBuilder;
    }

    public void writeXML(XMLBuilder xmlBuilder) throws FileNotFoundException, TransformerException {
        utility.writeToXMLFile(xmlBuilder, "BuildingsAndRooms.xml");
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