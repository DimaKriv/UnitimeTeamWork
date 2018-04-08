package databaseUtility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtility {

    // SQLite connection string
    public final static String DATABASE_URL = "jdbc:sqlite:../Tunniplaan.db";

    /**
     * Connect to the database
     * @return the Connection object
     */
    public static Connection connectToDatabase() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Make query from database and return queried data in ResultSet object
     * @param querySQL SQL statement to query from database
     * @return queried data in ResultSet object.
     */
    public static ResultSet queryDataFromDatabase(String querySQL){
        ResultSet resultSet = null;
        try {
            Connection connection = connectToDatabase();
            Statement statement  = connection.createStatement();
            resultSet = statement.executeQuery(querySQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
}
