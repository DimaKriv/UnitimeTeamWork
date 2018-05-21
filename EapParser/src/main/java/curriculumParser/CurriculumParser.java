package curriculumParser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CurriculumParser {

    private static final String PATH_CSV_FILE = "CSVFiles/curriculum_without_subject.csv";
    private CSVPrinter csvPrinter;

    public void parseCurriculum() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(PATH_CSV_FILE));

        csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("curriculumShortCode", "curriculumLongCode", "curriculumName", "curriculumType",
                        "curriculumMaster"));

        String[] curriculaCodes = {"50001", "50007", "50015", "50020"};

        for (String curriculaCode : curriculaCodes) {
            printAreaCurricula(curriculaCode);
        }

        csvPrinter.flush();

        System.out.println("All information about curricula has been parsed");

    }

    private void printAreaCurricula(String curriculaCode) throws IOException {
        Document doc  = Jsoup.connect("https://ois.ttu.ee/portal/page?_pageid=37,674560&_dad=portal&_schema" +
                "=PORTAL&p_action=view&p_fk_str_yksus_id=" + curriculaCode + "&p_kava_versioon_id=&p_net=internet&p_" +
                "lang=ET&p_rezhiim=0&p_mode=1&p_from=").get();

        int counterTT = doc.body().getElementsByAttributeValue("class", "tt").size();

        for (int i = 0; i < counterTT; i++) {
            Element curriculum = doc.body().getElementsByAttributeValue("class", "tt").get(i);
            String curriculumShortCode = curriculum.getElementsByAttributeValue("class", "s_top").get(1).html();
            String curriculumLongCode = curriculum.getElementsByAttributeValue("class", "s_top").get(2).html();
            String curriculumName = curriculum.getElementsByAttributeValue("class", "s_top").get(3).html();
            String curriculumType = curriculum.getElementsByAttributeValue("class", "s_top border_right_light").get(0).html();
            String curriculumMaster = curriculum.getElementsByAttributeValue("class", "s_top border_right_light").get(1).html();

            csvPrinter.printRecord(curriculumShortCode, curriculumLongCode, curriculumName, curriculumType, curriculumMaster);
        }

        int counterHT = doc.body().getElementsByAttributeValue("class", "ht").size();

        for (int i = 0; i < counterHT; i++) {
            Element curriculum = doc.body().getElementsByAttributeValue("class", "ht").get(i);
            String curriculumShortCode = curriculum.getElementsByAttributeValue("class", "s_top").get(1).html();
            String curriculumLongCode = curriculum.getElementsByAttributeValue("class", "s_top").get(2).html();
            String curriculumName = curriculum.getElementsByAttributeValue("class", "s_top").get(3).html();
            String curriculumType = curriculum.getElementsByAttributeValue("class", "s_top border_right_light").get(0).html();
            String curriculumMaster = curriculum.getElementsByAttributeValue("class", "s_top border_right_light").get(1).html();

            csvPrinter.printRecord(curriculumShortCode, curriculumLongCode, curriculumName, curriculumType, curriculumMaster);
        }
    }
}
