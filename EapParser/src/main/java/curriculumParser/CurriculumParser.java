package curriculumParser;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.json.JSONArray;
import org.json.JSONObject;
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
                .withHeader("curriculumShortCode", "curriculumLongCode", "curriculumName", "curriculumType", "curriculumMaster"));

        String[] curriculaCodes = {"50001", "50007", "50015", "50020"};

        for (String curriculaCode : curriculaCodes) {
            printAreaCurricula(curriculaCode);
        }

//        Request request = new Request.Builder()
//                .url("https://ois2.ttu.ee/uusois/!uus_ois2.ois_public.get_json?j_code=D63613804034D05BDB0E88B33C6FE952&p_from_row=0&p_orderby=ASC_nimetus&p_where=&p_lisa=p_str_id%3D%3E50020%2Cp_tase%3D%3E%27D%27&p_rnd=1526068394333")
//                .build();
//
//        Response response = client.newCall(request).execute();
//        String rawJSON = response.body().string();
//        JSONArray jsonArray = new JSONArray(rawJSON);
//
//        JSONObject jsonObject = new JSONObject(jsonArray.get(1).toString());
//        JSONArray jsonArrayForFirstElement = jsonObject.getJSONArray("andmed");
//
//        for (int i = 0; i < jsonArrayForFirstElement.length(); i++) {
//            System.out.println(jsonArrayForFirstElement.getJSONObject(i).getString("kood_ttu"));
//            System.out.println(jsonArrayForFirstElement.getJSONObject(i).getString("nimetus"));
//        }

        csvPrinter.flush();

        System.out.println("All information about curricula has been parsed");

    }

    private void printAreaCurricula(String curriculaCode) throws IOException {
        Document doc  = Jsoup.connect("https://ois.ttu.ee/portal/page?_pageid=37,674560&_dad=portal&_schema" +
                "=PORTAL&p_action=view&p_fk_str_yksus_id=" + curriculaCode + "&p_kava_versioon_id=&p_net=internet&p_lang=ET&p_rezhiim=0&p_mode=1&p_from=").get();

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
