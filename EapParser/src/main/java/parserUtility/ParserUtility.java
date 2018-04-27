package parserUtility;

import java.sql.*;

public class ParserUtility {

    // SQLite connection url
    private String databaseUrl;
    // Directory name, in which xml file are saved
    private String xmlFilesDirectory;

    public ParserUtility() {
        databaseUrl = "jdbc:sqlite:../Tunniplaan.db";
        xmlFilesDirectory = "XMLFiles";
    }

    public ParserUtility(String databaseUrl, String xmlFilesDirectory) {
        this.databaseUrl = databaseUrl;
        this.xmlFilesDirectory = xmlFilesDirectory;
    }

    /**
     * Connect to the database
     * @return the Connection object
     */
    public Connection connectToDatabase() throws SQLException {
        return DriverManager.getConnection(databaseUrl);
    }

    /**
     * Create Statement object, which can process SQL string, using given connection object.
     * @param connection from which will be created Statement object
     * @return Statement object for executing given SQL query in database to which it is connected.
     */
    public Statement createStatement(Connection connection) throws SQLException {
        return connection.createStatement();
    }

    /**
     * Make query from database and return queried data in ResultSet object.
     * @param querySQL SQL statement to query from database.
     * @param statement is Statement object which will process given SQL string.
     * @return queried data in ResultSet object.
     */
    public ResultSet queryDataFromDatabase(String querySQL, Statement statement) throws SQLException {
        return statement.executeQuery(querySQL);
    }

}
