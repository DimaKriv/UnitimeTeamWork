package parser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import parserUtility.ParserUtility;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class EapParser {

    private static final String PATH_CSV_FILE = "CSVFiles/EAP.csv";

    private ParserUtility utility;
    private String querySql;
    private ResultSet queryResultSet;

    public EapParser() throws SQLException {
        utility = new ParserUtility();
        Connection connection = utility.connectToDatabase();
        Statement statement = utility.createStatement(connection);
        querySql = "SELECT fk_aine_id FROM TUNN_AINE LIMIT 100";
        queryResultSet = utility.queryDataFromDatabase(querySql, statement);
    }

    public void parseEAP() throws IOException, SQLException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(PATH_CSV_FILE));

        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("subject", "eap"));

        while (queryResultSet.next()) {
            String fkAineID = queryResultSet.getString("fk_aine_id");

            Document doc  = Jsoup.connect("https://ois.ttu.ee/portal/page?_pageid=37,674581&_dad=portal&_schema=" +
                    "PORTAL&p_open_node2=&p_id=" + fkAineID + "&p_action=alluvaine&p_public=1&p_mode=1&keel=ET" +
                    "&p_session_id=69558200").get();

            String subject = doc.body().getElementsByAttributeValue("class", "s border_right").get(0).html();
            String eap = doc.body().getElementsByAttributeValue("class", "s border_right").get(4).html();

            csvPrinter.printRecord(subject, eap);

            System.out.println("Parsing " + subject);
        }

        csvPrinter.flush();

        System.out.println("All information about EAP has been parsed");
    }
}