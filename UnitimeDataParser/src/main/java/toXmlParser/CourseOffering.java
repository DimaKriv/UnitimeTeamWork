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
        querySql = "select D.NIMETUS, D.ainekood,A.loeng, A.praktikum, A.harjutus from (select S.nimetus, S.ainekood,T.TUNN_AINE_ID from  SUBJECT_DEPARTMENT as S inner join TUNN_AINE as T ON T.ainekood=S.ainekood) as D inner join T_AINE as A on D.TUNN_AINE_ID=A.FK_TUNN_AINE_ID where A.FK_TYYPKAVA_LIIK_KOOD='TYYPKAVA_LIIK_SS' " +
                "and A.FK_OPETSEMESTER_KOOD in ('AINESEMESTER_SK','AINESEMESTER_K') and not (A.loeng='0' and A.praktikum='0' and A.harjutus" +
                "='0') limit 30";

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
        int k = 0;
        while (queryResultSet.next()) {
            String subject = queryResultSet.getString("nimetus");
            String courseNumber = queryResultSet.getString("ainekood");
            String lecture = queryResultSet.getString("loeng");
            String paktikum = queryResultSet.getString("praktikum");
            String harjutus = queryResultSet.getString("harjutus");
            int[][] subpartMin = makeCourceOfferringData(makeIntMassiveFromStringInput(lecture,paktikum,harjutus));
           XMLBuilder out = xmlBuilder.element("offering")
                    .attribute("id", String.valueOf(i++))
                    .attribute("offered", "true")
                    .attribute("action", "insert|update|delete")
                    .element("course")
                    .attribute("subject", "ARIR")
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
            .attribute("limit", "15");
            createCourseSubject(subpartMin, out);
            createCourseClass(subpartMin, out);
            System.out.println(k++);
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
    public int[] makeIntMassiveFromStringInput(String lec, String pra, String har) {
        int[] clasT = new int[3];
        String [] input = new String[]{lec.trim(), pra.trim(),har.trim()};
        for (int i = 0;i<input.length;i++) {
            if (input[i].indexOf(",")== -1) {
             input[i] += "00";
            } else {
                if (input[i].indexOf(",") == input[i].length() - 2) {
                    input[i] += "0";
                }
               input[i] = input[i].replace(",","");
            }
            try {
                clasT[i]= Integer.parseInt(input[i]);
                if (clasT[i]< 0) clasT[i]=0;
            } catch (Exception exp) {
                clasT[i]=0;
            }
        }
        return clasT;
    }
    public int[][] makeCourceOfferringData(int[] input) {
        int [][] out = new int[input.length][input.length];
        for (int i = 0; i < input.length; i++) {
            out[i][i]= input[i];
        }
        loop: for (int i = 0; i < input.length; i++) {
            if (out[i][i] % 100 != 0) if (findPair(i, out)) continue loop;
            if (out[i][i] % 100 != 0 && i == 0) {
                findTPair(out);
        }
        }
        return out;
    }
    public boolean findPair(int index, int[][] out){
        for(int i = 1; i <out.length; i++) {
           if ((out[(index + i) % out.length][(index + i) % out.length] + out[index][index])%100 == 0) {
               out[index][(index + i) % out.length] = out[(index + i) % out.length][(index + i) % out.length];
               out[(index + i) % out.length][(index + i) % out.length] = 0;
               return true;
           }
        }
        return false;
    }
    public boolean findTPair(int[][] out){
        int sum = 0;
        for (int i = 0; i < out.length; i++) {
            sum += out[i][i];
        }
        if (sum % 100 == 0) {
            for (int i = 1; i < out.length; i++) {
                out[0][i] = out[i][i];
                out[i][i]=0;
            }
            return true;
        }
        return false;
    }
    public int[][] findOptionalPair(int[] initial, int[][] out){
        return out;
    }
    public XMLBuilder createCourseSubject(int [][] input, XMLBuilder out){
        String[] type = new String[] {"Lec","Lab","Rec"};
        for (int i = 0; i < input.length; i++) {
            int additionalpart = (input[i][0] * 45) % 100 + (input[i][1] * 45) % 100 + (input[i][2] * 45) % 100;
            if (input[i][i] != 0) {
                int count = 1;
               out = out.e("subpart").attribute("type", type[i])
                        .a("minPerWeek", (input[i][i] * 45 / 100 + additionalpart / 100) + "");
                int addPart = 0;
                for (int j = 0; j < input.length; j++) {
                    if (i == j) continue;
                    if (input[i][j] != 0) {
                        count++;
                     out =  out.e("subpart").a("type", type[j])
                                .a("minPerWeek", (input[i][j] * 45 / 100 + ""));
                    }
                }
                for (int j = 0; j < count; j++) out = out.up();
            }
        }
        return out;
    }
    public XMLBuilder createCourseClass(int [][] input, XMLBuilder out){
        String[] type = new String[] {"Lec","Lab","Rec"};
            for (int i = 0; i < input.length; i++) {
                if (input[i][i] != 0) {
                    int count = 1;
                 out =  out.element("class")
                            .attribute("type", type[i]).attribute("suffix", i + "")
                            .attribute("limit", 15 + "");
                    for (int j = 0; j < input.length; j++) {
                        if (i == j) continue;
                        if (input[i][j] != 0) {
                            count++;
                         out =  out.element("class").attribute("type", type[j])
                                    .attribute("suffix", i + "" + j)
                                    .attribute("limit", 15 + "");
                        }
                    }
                    for (int j = 0; j < count; j++) out = out.up();
                }
            }
        return out;
    }
}
