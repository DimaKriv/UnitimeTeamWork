package toXmlParser.sessionsetup;

import com.jamesmurty.utils.XMLBuilder;

import javax.xml.parsers.ParserConfigurationException;


public class ExaminationPeriods {


    AcademicSessionSetup academicSessionSetup;


    public ExaminationPeriods(AcademicSessionSetup academicSessionSetup) {
        this.academicSessionSetup = academicSessionSetup;
    }


    public XMLBuilder buildExaminationPeriods() throws ParserConfigurationException {

        XMLBuilder examinationPeriods = XMLBuilder.create("examinationPeriods");

        XMLBuilder finalExaminationPeriods = examinationPeriods.element("periods")
                .attribute("type", "final");


        //counted as main final examinations lasts for 4 weeks, no preferences yet.
        //exams which lasts more than 1.5H doesn't counted yet. Need to do something with it.

        //loop for weeks
        for (int i = 16; i < 20; i++) {
            //loop for week-days
            for (int j = 0; j < 4; j++) {
                //loop for times
                for (int l = 0; l < academicSessionSetup.FINAL_EXAM_TIMES.length; l++) {


                    finalExaminationPeriods.element("period")
                            .attribute("date", academicSessionSetup.getDateInFormat(j, i))
                            .attribute("startTime", academicSessionSetup.FINAL_EXAM_TIMES[l])
                            .attribute("length", "90");
                }
            }


        }

        //no midterm examinations needed?.
//        XMLBuilder midTerm = examinationPeriods.element("periods")
//                .attribute("type", "midterm");
//        midTerm.element("period")
//                .attribute("date", academicSessionSetup.getDateInFormat(2, 8))
//                .attribute("startTime", "1830")
//                .attribute("length", "90");

        return examinationPeriods;
    }


}
