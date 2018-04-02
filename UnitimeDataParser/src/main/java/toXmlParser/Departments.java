package toXmlParser;

import parserUtility.ParserUtility;

import java.sql.ResultSet;

public class Departments {

    private final String QUERY_SQL = "SELECT * FROM INSTITUDID";
    private final ResultSet QUERY_RESULT_SET = ParserUtility.queryDataFromDatabase(QUERY_SQL);

    public void buildXML() {

    }
}
