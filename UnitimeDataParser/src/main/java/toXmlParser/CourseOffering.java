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
            int[] allTypeOfClass = new int[3];
    /*        if (lecture.indexOf(",")==0) {
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
                System.out.println("Exception harjutus");
            }
      */

            if (lecture.length() < 3) {
                lecture += "0";
            }
            if (lecture.length() < 3) {
                lecture += "0";
            }
            lecture = lecture.replace(",","");
            try {
            // Old code.
            // lectureTime =new Double.parseDouble(lecture).intValue();
            allTypeOfClass[0] = Integer.parseInt(lecture);
            } catch (Exception e) {
                System.out.println(" Exp l" + lecture);
            }
            if (harjutus.length() < 3) {
                harjutus += "0";
            }
            if (harjutus.length() < 3) {
                harjutus += "0";
            }
            harjutus = harjutus.replace(",","");
            try {
                // Old code.
                // harjutusTime =new Double.parseDouble(harjutus).intValue();
                allTypeOfClass[2] = Integer.parseInt(harjutus);
            } catch (Exception e) {
                System.out.println("Exp h " + harjutus);
            }
            if (paktikum.length() < 3) {
                paktikum += "0";
            }
            if (paktikum.length() < 3) {
                paktikum += "0";
            }
            paktikum = paktikum.replace(",","");
            try {
                // Old code.
                // paktikumTime =new Double.parseDouble(paktikum).intValue();
                allTypeOfClass[1] = Integer.parseInt(paktikum);
            } catch (Exception e) {
                System.out.println("Exp p" + paktikum);
            }
            // After creating doubles lecture,praktikum,harjutus
            int[][] subpartMin = new int[3][3];
            loop: for (int k = 0; k < subpartMin.length;k++) {
                if (allTypeOfClass[k] == 0) continue;
                subpartMin[k][k]= allTypeOfClass[k];
                if (allTypeOfClass[k] % 100 != 0) {
                    for (int j = 1; j < allTypeOfClass.length - k; j++) {
                        if ((allTypeOfClass[k] + allTypeOfClass[(k + j)]) % 100 == 0) {
                            subpartMin[k][k + j] = allTypeOfClass[k + j];
                            allTypeOfClass[k + j] = 0;
                            continue loop;
                        }
                    }
                    if (k == 0 && (allTypeOfClass[0] + allTypeOfClass[1] + allTypeOfClass[2]) % 100 == 0) {
                        subpartMin[0][1] = allTypeOfClass[1];
                        allTypeOfClass[1] = 0;
                        subpartMin[0][2] = allTypeOfClass[2];
                        allTypeOfClass[2] = 0;
                        continue loop;
                    }
                    if (allTypeOfClass[k] < 100) {
                        for (int e = 1; e < allTypeOfClass.length - k; e++) {
                            if (allTypeOfClass[k] < 100) {
                                for (int j = 1; j < allTypeOfClass.length - k; j++) {
                                    if ((allTypeOfClass[k] + allTypeOfClass[(k + j)]) > 100) {
                                        subpartMin[k][k + j] = allTypeOfClass[k + j];
                                        allTypeOfClass[k + j] = 0;
                                        continue loop;
                                    }
                                }
                            }
                        }

                    }
                }
            }
            System.out.println(subpartMin[0][0] + subpartMin[0][1] + subpartMin[0][2]);
            System.out.println(subpartMin[1][0] + subpartMin[1][1] + subpartMin[1][2]);
            System.out.println(subpartMin[2][0] + subpartMin[2][1] + subpartMin[2][2]);
            System.out.println();
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
            .attribute("endDate", "05/18")
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
