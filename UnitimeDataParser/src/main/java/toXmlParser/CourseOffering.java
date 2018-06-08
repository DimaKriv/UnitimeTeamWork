package toXmlParser;

import com.jamesmurty.utils.XMLBuilder;
import javafx.util.Pair;
import parserUtility.ParserUtility;
import toXmlParser.dataOptimization.ClassOptimization;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CourseOffering {

    private ParserUtility utility;
    private String querySql;
    private ResultSet queryResultSet;

    public CourseOffering() throws SQLException {
        utility = new ParserUtility();
        Connection connection = utility.connectToDatabase();
        Statement statement = utility.createStatement(connection);
        querySql ="select inimeste_arv,ainekood,nimetus,loeng,praktikum,harjutus,fk_aine_id,T.oppejoud_id,T.type " +
                "from U_COURSEOFFERING u left join (select distinct AO.aine_id, AO.oppejoud_id,AT.type " +
                "from aine_type_oppejoud at inner join aine_oppejoud ao on ao.aine_id=at.aine_id " +
                "and ao.oppejoud_id=at.oppejoud_id)t on t.aine_id=u.fk_aine_id;";
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
        String curlimitStr = null;
        String cursubject = null;
        String curcourseNumber = null;
        String curlecture = null;
        String curpaktikum = null;
        String curharjutus = null;
        String cur_aine_id = null;
        HashMap<Integer,List<Integer>> staffSortByClassCode = new HashMap<>();
        int loengINT = 0; int praktikumINT = 1; int harjutusINT = 2;
        staffSortByClassCode.put(loengINT,new ArrayList<>());
        staffSortByClassCode.put(praktikumINT,new ArrayList<>());
        staffSortByClassCode.put(harjutusINT,new ArrayList<>());

        while (queryResultSet.next()) {
            String limitStr = queryResultSet.getString("inimeste_arv");
            String subject = queryResultSet.getString("nimetus");
            String courseNumber = queryResultSet.getString("ainekood");
            String lecture = queryResultSet.getString("loeng");
            String paktikum = queryResultSet.getString("praktikum");
            String harjutus = queryResultSet.getString("harjutus");
            String instructorId = queryResultSet.getString("oppejoud_id");
            String lessonType = queryResultSet.getString("type");
            String aine_id = queryResultSet.getString("fk_aine_id");
            if (cursubject == null) {
                curlimitStr = limitStr;
                cursubject = subject;
                curcourseNumber = courseNumber;
                curlecture = lecture;
                curpaktikum = paktikum;
                curharjutus = harjutus;
                cur_aine_id = aine_id;
                putToMapTypeOfClassAndInstructorId(staffSortByClassCode, lessonType,instructorId);
                continue;
            }
            if (cursubject != null && cursubject.equals(subject)) {
                putToMapTypeOfClassAndInstructorId(staffSortByClassCode, lessonType,instructorId);
                continue;
            } else {
                addCourse(xmlBuilder, curlimitStr,curlecture,
                        curpaktikum, curharjutus,cursubject, curcourseNumber, cur_aine_id, staffSortByClassCode);
                staffSortByClassCode.get(loengINT).clear();
                staffSortByClassCode.get(praktikumINT).clear();
                staffSortByClassCode.get(harjutusINT).clear();
                curlimitStr = limitStr;
                cursubject = subject;
                curcourseNumber = courseNumber;
                curlecture = lecture;
                curpaktikum = paktikum;
                curharjutus = harjutus;
                cur_aine_id = aine_id;
                putToMapTypeOfClassAndInstructorId(staffSortByClassCode, lessonType,instructorId);
            }
        }
        addCourse(xmlBuilder, curlimitStr,curlecture,
                curpaktikum, curharjutus,cursubject, curcourseNumber, cur_aine_id, staffSortByClassCode);
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

    public void createCourseSubject(int[][] input, XMLBuilder out) {
        String[] type = new String[]{"Lec", "Lab", "Rec"};
        for (int i = 0; i < input.length; i++) {
            ClassOptimization optimizator = new ClassOptimization();
            int[] classConfig = optimizator.countDatePattern(input, i);
            if (isExist(classConfig, optimizator)) {
                out = out.e("subpart").attribute("type", type[i])
                        .a("minPerWeek", minPerWeek(classConfig, optimizator) + "");
                out = out.up();
            }
        }
    }

    public void createCourseClass(int[][] input, XMLBuilder out, int people, HashMap<Integer, List<Integer>> staff) {
        String[] type = new String[]{"Lec", "Lab", "Rec"};
        int peopleNotInClass = people;
        int suffixOfClass = 1;
        for (int i = 0; i < input.length; ) {
            ClassOptimization optimizator = new ClassOptimization();
            int[] classConfig = optimizator.countDatePattern(input, i);
            if (!isExist(classConfig, optimizator)) {
                i++;
                continue;
            }
            int limitClassCapacity;
            if (i == 0) limitClassCapacity = 200;
            else limitClassCapacity = 20;
            out = out.element("class")
                    .attribute("type", type[i]).attribute("suffix"
                            , suffixOfClass + "")
                    .attribute("limit", limitClassCapacity + "");
            for (int j = 0; j < staff.get(i).size(); j++) {
              out = out.element("instructor").attribute("id",staff.get(i).get(j) + "")
                      .attribute("share", "100").up();
            }
                   out = out.up();
            if ((peopleNotInClass - limitClassCapacity) > 0) {
                peopleNotInClass -= limitClassCapacity;
                suffixOfClass++;
            } else {
                i++;
                peopleNotInClass = people;
                suffixOfClass = 1;
            }
        }
    }

    public boolean putToMapTypeOfClassAndInstructorId(HashMap<Integer,List<Integer>> staffSortByCode,
                                                  String typeOfClass, String strinstructor_id) {
        Pair<String,Integer>[] typetoCode  = new Pair[] {
                new Pair("loeng",0), new Pair("praktikum",1), new Pair("harjutus",2)};
        try {
            String[] typeMassive = typeOfClass.split("\\+");
            int instructorId = Integer.parseInt(strinstructor_id);
            for (String type : typeMassive) {
                int typeClass = Arrays.asList(typetoCode).stream()
                        .filter(pair -> type.equals(pair.getKey())).findFirst().get().getValue();
                staffSortByCode.get(typeClass).add(instructorId);
            }
        } catch (Exception exp) {
            System.out.println(exp.getMessage());
            return false;
        }
        return true;
    }

    public int minPerWeek(int[] classConfig, ClassOptimization op) {
        int[] numberAndMinutesOfClass = op.getNumberAndMinutesOfClass(classConfig);
        return numberAndMinutesOfClass[0] * numberAndMinutesOfClass[1];
    }

    public boolean isExist(int[] classConfig, ClassOptimization op) {
        return !op.getDatePattern(classConfig).equals(ClassOptimization.NO_DATE_PATTERN)
                && !op.getTimePattern(classConfig).equals(ClassOptimization.NO_TIME_PATTERN);
    }

    public void addCourse(XMLBuilder xmlBuilder, String limitStr, String lecture,
                          String paktikum, String harjutus, String subject, String courseNumber, String i,
                          HashMap<Integer,List<Integer>> staff) {

        ClassOptimization optimizator = new ClassOptimization();
        int[][] subpartMin = optimizator.makeCourceOfferringData(
                optimizator.makeIntMassiveFromStringInput(lecture, paktikum, harjutus));
        int limit;
        try {
            limit = Integer.parseInt(limitStr);
        } catch (Exception e) {
            System.out.println("Not valid limit");
            return ;
        }
        XMLBuilder out = xmlBuilder.element("offering")
                .attribute("id", i)
                .attribute("offered", "true")
                .attribute("action", "insert|update|delete")
                .element("course")
                .attribute("subject", courseNumber)
                .attribute("courseNbr", 1 + "")
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
                .attribute("limit", limit + "");
        createCourseSubject(subpartMin, out);
        createCourseClass(subpartMin, out, limit, staff);
    }
    }

