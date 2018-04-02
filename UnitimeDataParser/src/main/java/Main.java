import parserUtility.ParserUtility;
import toXmlParser.BuildingsAndRooms;
import toXmlParser.Departments;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        BuildingsAndRooms buildingsAndRooms = new BuildingsAndRooms();
        buildingsAndRooms.buildXML();
    }
}
