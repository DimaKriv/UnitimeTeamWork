package toXmlParser.sessionsetup;


import com.jamesmurty.utils.XMLBuilder;

import javax.xml.parsers.ParserConfigurationException;

public class MainData {
    AcademicSessionSetup academicSessionSetup;

    public MainData(AcademicSessionSetup academicSessionSetup) {
        this.academicSessionSetup = academicSessionSetup;
    }


    public XMLBuilder buildXML() throws ParserConfigurationException {


        XMLBuilder session = XMLBuilder.create("session")
                .attribute("startDate", academicSessionSetup.getDateInFormat(0, 0))
                .attribute("endDate", academicSessionSetup.getDateInFormat(4, 18))
                .attribute("classEndDate", academicSessionSetup.getDateInFormat(4, 15))
                .attribute("examStartDate", academicSessionSetup.getDateInFormat(0, 16))
                .attribute("eventStartDate", academicSessionSetup.getDateInFormat(0, -1))
                .attribute("eventEndDate", academicSessionSetup.getDateInFormat(1, -1));

        return session;

    }


}
