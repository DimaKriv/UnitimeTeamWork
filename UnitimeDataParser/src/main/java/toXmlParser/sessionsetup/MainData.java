package toXmlParser.sessionsetup;


import com.jamesmurty.utils.XMLBuilder;

import javax.xml.parsers.ParserConfigurationException;

public class MainData {
    AcademicSessionSetup academicSessionSetup;

    MainData(AcademicSessionSetup academicSessionSetup) {
        this.academicSessionSetup = academicSessionSetup;
    }


    XMLBuilder buildXML() throws ParserConfigurationException {


        XMLBuilder session = XMLBuilder.create("session")
                .attribute("startDate", academicSessionSetup.getDateInFormat(0, 0))
                .attribute("endDate", academicSessionSetup.getDateInFormat(4, 18))
                .attribute("classEndDate", academicSessionSetup.getDateInFormat(4, 15))
                .attribute("examStartDate", academicSessionSetup.getDateInFormat(0, 16))
                .attribute("eventStartDate", academicSessionSetup.getDateInFormat(0, -1))
                .attribute("eventEndDate", academicSessionSetup.getDateInFormat(1, -1));


//            holidays/managers are not important
//            XMLBuilder holidays = session.element("holidays");
//            XMLBuilder holiday = holidays.element("holiday")
//                    .attribute("date", "2118/9/6");

        return session;

    }


}
