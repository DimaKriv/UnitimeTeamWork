package toXmlParser;

import com.jamesmurty.utils.XMLBuilder;
import parserUtility.ParserUtility;
import toXmlParser.dataOptimization.ClassOptimizator;

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
        querySql = "select inimeste_arv,ainekood,nimetus,loeng,praktikum,harjutus from U_COURSEOFFERING";

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
            String limitStr = queryResultSet.getString("inimeste_arv");
            String subject = queryResultSet.getString("nimetus");
            String courseNumber = queryResultSet.getString("ainekood");
            String lecture = queryResultSet.getString("loeng");
            String paktikum = queryResultSet.getString("praktikum");
            String harjutus = queryResultSet.getString("harjutus");
            ClassOptimizator optimizator = new ClassOptimizator();
            int[][] subpartMin = optimizator.makeCourceOfferringData(
                    optimizator.makeIntMassiveFromStringInput(lecture,paktikum,harjutus));
            int limit = 0;
            try {
                limit= Integer.parseInt(limitStr);
            } catch (Exception e) {
                System.out.println("Not valid limit");
                continue;
            }
           XMLBuilder out = xmlBuilder.element("offering")
                    .attribute("id", String.valueOf(i++))
                    .attribute("offered", "true")
                    .attribute("action", "insert|update|delete")
                    .element("course")
                    .attribute("subject", courseNumber)
                    .attribute("courseNbr", 1 +"")
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
            .attribute("limit", limit + 20 +"");
            createCourseSubject(subpartMin, out);
            createCourseClass(subpartMin, out, limit + 20);
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


    public int[][] findOptionalPair(int[] initial, int[][] out){
        return out;
    }
    public XMLBuilder createCourseSubject(int [][] input, XMLBuilder out){
        String[] type = new String[] {"Lec","Lab","Lab"};
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
    public XMLBuilder createCourseClass(int [][] input, XMLBuilder out,int limit){
        String[] type = new String[] {"Lec","Lab","Lab"};
        int limitCopy = limit;
        int suf=0;
            for (int i = 0; i < input.length;) {
                if (input[i][i] != 0) {
                    int count = 1;
                    int limClas = limitCopy;
                    if (limitCopy > 200 && i == 0) limClas = 200;
                    else if (limitCopy > 20 && i != 0) limClas = 20;
                 out =  out.element("class")
                            .attribute("type", type[i]).attribute("suffix", i + "" + suf +"")
                            .attribute("limit", limClas+"");
                    for (int j = 0; j < input.length; j++) {
                        if (i == j) continue;
                        if (input[i][j] != 0) {
                            count++;
                            int limClasSub = limClas;
                            if (limClasSub > 20) limClasSub =20;
                         out = makeRepeat(out, type[j], ""+ j, limClasSub + "",limClas / 20);
                        }
                    }
                    for (int j = 0; j < count; j++) out = out.up();
                    if (limitCopy - limClas != 0) {
                        limitCopy -= limClas;
                        suf++;
                    }
                    else{i++; limitCopy = limit; suf=0;}
                } else {
                    i++;
                    suf=0;
                }
            }
        return out;
    }
    public XMLBuilder makeRepeat(XMLBuilder out,String type, String suffix, String limit, int repeat) {
        if (repeat< 1) return out;
        for (int i = 1; i < repeat; i++) {
            out.element("class").attribute("type", type)
                    .attribute("suffix", suffix + i)
                    .attribute("limit", limit).up();
        }
        out.element("class").attribute("type", type)
                .attribute("suffix", suffix + 0)
                .attribute("limit", limit);
     return out;
    }
}
