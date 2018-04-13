package encodingRepairer;

import databaseUtility.DatabaseUtility;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TablesEncodingRepairer {

    /**
     * SQL query for getting names of all tables in database.
     */
    private final String TABLES_QUERY = "SELECT name FROM sqlite_master WHERE type='table'";
    private final ResultSet TABLES_RESULT_SET = DatabaseUtility.queryDataFromDatabase(TABLES_QUERY);
    /**
     * SQL query dummy for getting all data from 'tableName', which was gotten from TABLES_QUERY.
     */
    private final String TABLE_DUMMY_QUERY = "SELECT * FROM";

    private final String INVALID_ENCODING_REGEX_PATTERN = "\\[u][0-9a-z]{4}";
    private final Pattern REGEX_PATTERN = Pattern.compile(INVALID_ENCODING_REGEX_PATTERN);


    public void repairEncodingInDatabaseTables() {
        try {
            while (TABLES_RESULT_SET.next()) {
                String tableName = TABLES_RESULT_SET.getString("name");

                final String tableDataQuery = String.format("%s %s", TABLE_DUMMY_QUERY, tableName);
                final ResultSet tableDataResultSet = DatabaseUtility.queryDataFromDatabase(tableDataQuery);
                final ResultSetMetaData tableDataResultSetMetaData = tableDataResultSet.getMetaData();
                final int tableColumnsNumber = tableDataResultSetMetaData.getColumnCount();

                while (tableDataResultSet.next()) {
                    for (int column = 0; column < tableColumnsNumber; column++) {
                        String columnValue = tableDataResultSet.getString(column);
                        Matcher regexMatcher = REGEX_PATTERN.matcher(columnValue);
                        if (regexMatcher.find()) {

                        }
                    }
                }


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
