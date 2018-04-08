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
import java.util.Properties;


public class AcademicSessionSetup {


    public void buildXML() {
        try {
            XMLBuilder xmlBuilder;

            xmlBuilder =
                    XMLBuilder.create(("datePatterns"));


            //ADDING DATE PATTERNS

            //all weeks separately
            for (int i = 0; i < 15; i++) {
                xmlBuilder.element("datePattern")
                        .attribute("name", "week " + Integer.toString(i+1))
                        .attribute("type", "Standard")
                        .attribute("visible", "true")
                        .attribute("default", "true")

                        .element("dates")
                        .attribute("fromDate", getDateInFormat(0, i))
                        .attribute("toDate", getDateInFormat(4, i));

            }

            //all weeks together
            xmlBuilder.element("datePattern")
                    .attribute("name", "all weeks")
                    .attribute("type", "Standard")
                    .attribute("visible", "true")
                    .attribute("default", "true");

            for (int i = 0; i < 15; i++) {

                xmlBuilder.element("dates")
                        .attribute("fromDate", getDateInFormat(0, i))
                        .attribute("toDate", getDateInFormat(4, i));


            }

            //odd weeks

            xmlBuilder.element("datePattern")
                    .attribute("name", "odd weeks")
                    .attribute("type", "Standard")
                    .attribute("visible", "true")
                    .attribute("default", "true");


            for (int i = 0; i < 15; i++) {
                if ((i + 1) % 2 != 0) {
                    xmlBuilder.element("dates")
                            .attribute("fromDate", getDateInFormat(0, i))
                            .attribute("toDate", getDateInFormat(4, i));
                }
            }



            //even weeks
            xmlBuilder.element("datePattern")
                    .attribute("name", "even weeks")
                    .attribute("type", "Standard")
                    .attribute("visible", "true")
                    .attribute("default", "true");

            for (int i = 0; i < 15; i++) {
                if ((i + 1) % 2 == 0) {
                    xmlBuilder.element("dates")
                            .attribute("fromDate", getDateInFormat(0, i))
                            .attribute("toDate", getDateInFormat(4, i));
                }
            }





            new File("XMLFiles").mkdirs();  // create XMLFiles directory


            PrintWriter writer = new PrintWriter(new FileOutputStream("XMLFiles/academicSessionSetup.xml"));
            Properties outputProperties = new Properties();
            outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            outputProperties.put(javax.xml.transform.OutputKeys.INDENT, "yes");
            outputProperties.put("{http://xml.apache.org/xslt}indent-amount", "2");
            xmlBuilder.toWriter(writer, outputProperties);


        } catch (ParserConfigurationException | FileNotFoundException | TransformerException e) {
            e.printStackTrace();
        }
    }


    private String getDateInFormat(int day, int week) {
        String[] strings = getResultSetDayAndWeek(day, week).toString().substring(0, 9).split("-");
        return strings[0] + "/" + strings[1] + "/" + strings[2];
    }


    private ResultSet getResultSetDayAndWeek(int day, int week) {
        return ParserUtility.queryDataFromDatabase("SELECT kuupaev FROM SESSIOON_AJAD" +
                "WHERE fk_tunn_sessioon_id = '1123'" +
                " AND paev = '" + Integer.toString(day) +
                "' AND nadal='" + Integer.toString(week) + "'");
    }


    private ResultSet getResultSetDatesBySessionId() {
        return ParserUtility.queryDataFromDatabase(getSQLQueryDatesBySessionId());
    }

    //1123 - 2018 spring semester default
    private String getSQLQueryDatesBySessionId() {
        return "SELECT * FROM SESSIOON_AJAD" +
                "WHERE fk_tunn_sessioon_id = '1123'";
    }


}


//  work later with parameters


//    private ResultSet getQueryResultSetDatesBySessionId(int sessionId) {
//        return ParserUtility.queryDataFromDatabase(getSQLQueryDatesBySessionId(sessionId));
//    }
//
//    private String getSQLQueryDatesBySessionId(int sessionId) {
//        return "SELECT * FROM TUNNIPLAAN.SESSIOON_AJAD" +
//                "WHERE fk_tunn_sessioon_id = '" + Integer.toString(sessionId) + "'";
//    }
