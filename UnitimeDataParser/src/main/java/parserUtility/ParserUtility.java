package parserUtility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ParserUtility {

    /**
     * Connect to the Tunniplaan.db database
     * @return the Connection object
     */
    public static Connection connectToDatabase() {
        // SQLite connection string
        String url = "jdbc:sqlite:../Tunniplaan.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
