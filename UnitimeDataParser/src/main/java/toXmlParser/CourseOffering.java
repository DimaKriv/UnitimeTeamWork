package toXmlParser;

import com.jamesmurty.utils.XMLBuilder;
import parserUtility.ParserUtility;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CourseOffering {

    private ParserUtility utility;
    private String querySql;
    private ResultSet queryResultSet;

    public CourseOffering() throws SQLException {
        utility = new ParserUtility();
        Connection connection = utility.connectToDatabase();
        Statement statement = utility.createStatement(connection);
//        querySql = "select D.course, D.department_name, D.nimetus_est,A.loeng, A.praktikum, A.harjutus   from  (select distinct S.course, S.department_name, T.nimetus_est, T.TUNN_AINE_ID from SUBJECT_TO_AREA as S INNER JOIN TUNN_AINE as T ON S.course=T.ainekood) as D inner join T_AINE as A ON A.FK_TUNN_AINE_ID=D.TUNN_AINE_ID where A.FK_TYYPKAVA_LIIK_KOOD='TYYPKAVA_LIIK_SS' and A.FK_OPETSEMESTER_KOOD in ('AINESEMESTER_SK','AINESEMESTER_K')";
        querySql = "select D.NIMETUS, D.ainekood,A.loeng, A.praktikum, A.harjutus from (select S.nimetus, S.ainekood,T.TUNN_AINE_ID from  SUBJECT_DEPARTMENT as S inner join TUNN_AINE as T ON T.ainekood=S.ainekood) as D inner join T_AINE as A on D.TUNN_AINE_ID=A.FK_TUNN_AINE_ID where A.FK_TYYPKAVA_LIIK_KOOD='TYYPKAVA_LIIK_SS' and A.FK_OPETSEMESTER_KOOD in ('AINESEMESTER_SK','AINESEMESTER_K') limit 30";
        queryResultSet = utility.queryDataFromDatabase(querySql, statement);
    }

    public CourseOffering(String querySql, ResultSet queryResultSet) {
        utility = new ParserUtility();
        this.querySql = querySql;
        this.queryResultSet = queryResultSet;
    }

    public XMLBuilder createCourseOfferingElementBuilder(String campus, String term, String year) throws ParserConfigurationException {
        XMLBuilder xmlBuilder =
                XMLBuilder.create("offerings")
                        .attribute("campus", campus)
                        .attribute("term", term)
                        .attribute("year", year);
        return xmlBuilder;
    }

    public XMLBuilder buildXML(String campus, String term, String year) throws ParserConfigurationException, SQLException {
        XMLBuilder xmlBuilder = createCourseOfferingElementBuilder(campus, term, year);
        int i =1;
        while (queryResultSet.next()) {
            int lectureTime = 0;
            int praktikumTime = 0;
            int harjutusTime = 0;
            String subject = queryResultSet.getString("nimetus");
            String courseNumber = queryResultSet.getString("ainekood");
            String lecture = queryResultSet.getString("loeng");
            String paktikum = queryResultSet.getString("praktikum");
            String harjutus = queryResultSet.getString("harjutus");
            if (lecture.indexOf(",")==0) {
                lecture = "0" + lecture;
            }
            lecture = lecture.replace(",",".");
            try {
                lectureTime = new Double(Double.parseDouble(lecture) * 45).intValue();
            } catch (Exception e) {
                System.out.println(lecture);
            }
            if (paktikum.indexOf(",")==0) {
                paktikum = "0" + lecture;
            }
            paktikum = paktikum.replace(",",".");
            try {
                praktikumTime = new Double(Double.parseDouble(paktikum) * 45).intValue();
            } catch (Exception e) {
                System.out.println(lecture);
            }
            if (harjutus.indexOf(",")==0) {
                harjutus = "0" + lecture;
            }
            harjutus = harjutus.replace(",",".");
            try {
                harjutusTime = new Double(Double.parseDouble(harjutus) * 45).intValue();
            } catch (Exception e) {
                System.out.println(lecture);
            }
            xmlBuilder.element("offering")
                    .attribute("id", String.valueOf(i++))
                    .attribute("offered", "true")
                    .attribute("action", "insert")
                    .element("course")
                    .attribute("subject", "ARAR")
                    .attribute("courseNbr", courseNumber)
                    .attribute("controlling", "true")
                    .attribute("title", subject)
                    .element("courseCredit")
                    .attribute("creditType", "collegiate")
                    .attribute("creditUnitType", "semesterHours")
                    .attribute("creditFormat", "creditFormat")
                    .attribute("fixedCredit", "2")
                    .up()
                    .up()
            .element("config")
            .attribute("name", "1")
            .attribute("limit", "15")
            .element("subpart")
            .attribute("type","Rec")
            .attribute("minPerWeek", String.valueOf(harjutusTime))
                    .up()
            .element("subpart")
            .attribute("type", "Lec")
            .attribute("minPerWeek", String.valueOf(lectureTime))
                    .up()
            .element("subpart")
            .attribute("type", "Lab")
            .attribute("minPerWeek", String.valueOf(praktikumTime))
                    .up()
            .element("class")
                    .attribute("type", "Lec")
                    .attribute("id", "")
                    .attribute("suffix", "1")
                    .attribute("limit", "15")
                    .up()
            .element("class")
                    .attribute("type", "Rec")
                    .attribute("id", "")
                    .attribute("suffix", "1")
                    .attribute("limit", "15")
                    .up()
            .element("class")
                    .attribute("type", "Lab")
                    .attribute("id", "")
                    .attribute("suffix", "1")
                    .attribute("limit", "15")
                    .element("date")
            .attribute("startDate", "01/29")
            .attribute("endDate", "06/07")
            .element("time")
            .attribute("days", "MTWThF")
            .attribute("startTime", "1000")
            .attribute("endTime", "1130");
        }
        return xmlBuilder;
    }

    public void writeXML(XMLBuilder xmlBuilder) throws FileNotFoundException, TransformerException {
        utility.writeToXMLFile(xmlBuilder, "courseOfferings.xml");
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
