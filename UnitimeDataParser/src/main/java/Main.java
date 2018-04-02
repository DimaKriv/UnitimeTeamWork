import parserUtility.ParserUtility;
import toXmlParser.Departments;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Departments departments = new Departments();
        departments.buildXML();
    }
}
