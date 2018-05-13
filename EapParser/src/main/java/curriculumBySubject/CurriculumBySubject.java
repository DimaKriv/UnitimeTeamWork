package curriculumBySubject;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CurriculumBySubject {

    private static final String PATH_CSV_FILE = "CSVFiles/curriculum_with_subjects.csv";
    private CSVPrinter csvPrinter;

    public void runParser() throws IOException {
//        String[] curriculaCodes = {
//                "50401", "50406", "50403", "50400", "50405", "50402", "50491", "50442", "50464", "50414", "50431", "50460", "50457", "50408", "50439", "50479", "50428", "50424",
//                "50387", "50384", "50371", "50370", "50357", "50392", "50486", "50482", "50481", "50484", "50365", "50361", "50388", "50359", "50465", "50461", "50468", "50419", "50289", "50306",
//                "50412", "50427", "50472", "50430", "50469", "50445", "50454", "50447", "50458", "50456", "50452", "50394", "50372", "50451", "50395", "50432",
//                "50364", "50358", "50356", "50492", "50117", "50489", "50429", "50411", "50410", "50434",
//                "50386", "50378", "50368", "50385", "50373", "50488", "50474", "50380", "50421", "50423", "50416", "50417", "50426", "50418", "50425", "50179", "50422", "50415"
//        };

        String[][] curriculaCodes = {
                {"50401", "50406", "50403", "50400", "50405", "50402", "50491", "50442", "50464", "50414", "50431"},
//                {"50460", "50457", "50408", "50439", "50479", "50428", "50424"},
//                {"50387", "50384", "50371", "50370", "50357", "50392", "50486", "50482", "50481", "50484", "50365"},
//                {"50361", "50388", "50359", "50465", "50461", "50468", "50419", "50289", "50306"},
//                {"50412", "50427", "50472", "50430", "50469", "50445", "50454", "50447", "50458", "50456", "50452"},
//                {"50394", "50372", "50451", "50395", "50432"},
//                {"50364", "50358", "50356", "50492", "50117", "50489", "50429", "50411", "50410", "50434"},
//                {"50386", "50378", "50368", "50385", "50373", "50488", "50474", "50380", "50421", "50423"},
//                {"50416", "50417", "50426", "50418", "50425", "50179", "50422", "50415"}
        };

        /*{"50401", "50406", "50403", "50400", "50405", "50402", "50491", "50442", "50464", "50414", "50431", "50460", "50457", "50408", "50439", "50479", "50428", "50424"},
        {"50387", "50384", "50371", "50370", "50357", "50392", "50486", "50482", "50481", "50484", "50365", "50361", "50388", "50359", "50465", "50461", "50468", "50419", "50289", "50306"},
        {"50412", "50427", "50472", "50430", "50469", "50445", "50454", "50447", "50458", "50456", "50452", "50394", "50372", "50451", "50395", "50432"},
        {"50364", "50358", "50356", "50492", "50117", "50489", "50429", "50411", "50410", "50434"},
        {"50386", "50378", "50368", "50385", "50373", "50488", "50474", "50380", "50421", "50423", "50416", "50417", "50426", "50418", "50425", "50179", "50422", "50415"}*/

        BufferedWriter writer = Files.newBufferedWriter(Paths.get(PATH_CSV_FILE));
        csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("oppekava_kood, oppekava_versioon, oppekava_nimetus, peaeriala_number, peaeriala_nimetus, semester_number, semester_tyyp, ainekood, aine_nimetus, aine_tyyp"));

//        for (String curriculaCode : curriculaCodes) {
//            parseCurriculaBySubject(curriculaCode);
//            System.out.println("---------------------------------");
//        }

        for (int i = 0; i < curriculaCodes.length; i++) {
            for (int codeIndex = 0; codeIndex < curriculaCodes[i].length; codeIndex++) {
                parseCurriculaBySubject(curriculaCodes[i][codeIndex]);
                System.out.println("---------------------------------");
            }
            System.out.println("=================================");
        }

        csvPrinter.flush();
        System.out.println("All information about curricula by subjects has been parsed");
    }

    private void parseCurriculaBySubject(String curriculaCode) throws IOException {
        Document doc  = Jsoup.connect("https://ois.ttu.ee/portal/page?_pageid=37,674560&_dad=portal&_schema=PORTAL&" +
                "p_action=view&p_fk_str_yksus_id=50001&p_kava_versioon_id=" + curriculaCode + "&" +
                "p_net=internet&p_lang=ET&p_rezhiim=0&p_mode=1&p_from=").get();

        Element curriculumBody = doc.body();
        String curriculumCode = curriculumBody.getElementsByAttributeValue("class", "s border_right").get(0).html();
        String curriculumVersion = curriculumBody.getElementsByAttributeValue("class", "s border_right").get(2).html();
        String curriculumName = curriculumBody.getElementsByAttributeValue("class", "s border_right").get(4).html();

        String body = doc.body().html();
        String regex = "<tr>\\s*<td class=\"s tt border_right\" colspan=\"100\" valign=\"top\">" +
                "<span class=\"subheader1\"><a name=\"\\w{21}\">" +
                "</a>tüüpõpingukava: sügis statsionaarne&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                "<a href=\"javascript:;\" onclick=\"document.location.hash='algus';\">algusesse</a></span>" +
                "</td>\\s*</tr>";

        String semestersRegex = "<tr class=\"ht\">\\s*" +
                "<td class=\"s_top border_right_light\" colspan=\"100\" style=\"font-weight:bold;font-style:italic\">" +
                "\\d+. semester&nbsp;&nbsp;\\s*<div id=\"\\w{3}\\d{2,3}\" class=\"short\"(\\s\\w{5}=\"\\w{7}:\\s\\w{4};\")*>" +
                "\\s*<a href=\"javascript:hide_show_rows\\('\\w{10}','\\w{3}\\d{2,3}',true\\);\">" +
                "<img src=\"/images/ilistadd.gif\" border=\"0\" alt=\"Näita rohkem\" title=\"Näita rohkem\"></a>\\s*</div>" +
                "\\s*<div id=\"\\w{3}\\d{2,3}\\w\" class=\"long\"(\\s\\w{5}=\"\\w{7}:\\s\\w{6};\")*>\\s*" +
                "<a href=\"javascript:hide_show_rows\\('\\w{10}','\\w{3}\\d{2,3}',false\\);\">" +
                "<img src=\"/images/ilistrmv.gif\" border=\"0\" alt=\"Näita vähem\" title=\"Näita vähem\"></a>\\s*</div>" +
                "</td>\\s*</tr>";

        String[] curricula = body.split(regex);
        for (int currIndex = 1; currIndex < curricula.length; currIndex++) {
            int redundantContentIndex = curricula[currIndex]
                    .indexOf("<tr> \n        <td class=\"bb\" colspan=\"100\" valign=\"top\">&nbsp;</td> \n       </tr>");
            if (redundantContentIndex >= 0) {
                curricula[currIndex] = curricula[currIndex]
                        .substring(0, redundantContentIndex);
            }
        }
        List<String[]> curriculaWithSemestersPerCurriculum = new ArrayList<>();

        for (int i = 1; i < curricula.length; i++) {
            String[] splittedCurricula = curricula[i].split(semestersRegex);
            curriculaWithSemestersPerCurriculum.add(splittedCurricula);
        }

        int fieldOfStudyNumber = 0;
        for (String[] curriculum : curriculaWithSemestersPerCurriculum) {
            int semesterNumber = 0;
            fieldOfStudyNumber++;
            for (int i = 1; i < curriculum.length; i++) {
                String semester = curriculum[i].trim();

                semesterNumber++;

                Document semesterHTML = Jsoup.parse(semester, "", Parser.xmlParser());

                Elements trElements = semesterHTML.getElementsByTag("tr");

                String subjectType = "Undefined";  // might be K for kohustuslik and "V" for valik
                String fieldOfStudy = trElements.last().child(0).html();

                for (int trIndex = 0; trIndex < trElements.size(); trIndex++) {
                    if (trElements.get(trIndex).getAllElements().size() == 2) {
                        if (trElements.get(trIndex).child(0).html().equals("kohustuslikud ained")) {
                            subjectType = "K";
                        } else if (trElements.get(trIndex).child(0).html().equals("valikained")) {
                            subjectType = "V";
                        }
                    }
                    if (trElements.get(trIndex).getAllElements().size() >= 9) {
                        if (!trElements.get(trIndex).child(0).html().equals("peaeriala") &&
                                !trElements.get(trIndex).child(0).html().equals("ainekood") &&
                                !trElements.get(trIndex).child(0).getElementsByTag("nobr").html().matches("[a-zA-Z]{3}(\\d{4}|\\d{3})(\\d{1}|[a-zA-Z]{1})")) {
                            String currentFieldOfStudy = trElements.get(trIndex).child(0).html();
                            if (!fieldOfStudy.equals(currentFieldOfStudy)) {
                                fieldOfStudy = currentFieldOfStudy;
                                semesterNumber = 1;
                            }
                        }


                        if (trElements.get(trIndex).child(0).getElementsByTag("nobr").html().matches("[a-zA-Z]{3}\\d{4}")) {
                            String subjectCode = trElements.get(trIndex).getElementsByTag("nobr").html();
                            String subjectName = trElements.get(trIndex).child(1).html();
                            String subjectSeason = trElements.get(trIndex).child(8).html();

                            csvPrinter.printRecord(curriculumCode, curriculumVersion, curriculumName, fieldOfStudyNumber, fieldOfStudy,
                                    semesterNumber, subjectSeason, subjectCode, subjectName, subjectType);


                        }
                    }
                }

            }

        }

        System.out.println(curriculumName + " ----> parsed");

    }
}
