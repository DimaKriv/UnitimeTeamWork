import toXmlParser.*;
import toXmlParser.sessionsetup.AcademicSessionSetup;

import java.sql.SQLException;

public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
//        BuildingsAndRooms buildingsAndRooms = new BuildingsAndRooms();
//        buildingsAndRooms.createXMLFile("TTU", "Fall", "2018");
//
//        Departments departments = new Departments();
//        departments.createXMLFile("TTU", "Fall", "2018");
//
//        SubjectAreas subjectAreas = new SubjectAreas();
//        subjectAreas.createXMLFile("TTU", "Fall", "2018");
//
//        CourseOffering courseCatalog = new CourseOffering();
//        courseCatalog.createXMLFile("TTU", "Fall", "2018");
//
//        StaffParser staffParser = new StaffParser();
//        staffParser.createXMLFile("TTU", "Fall", "2018");
//
//        //sessionID you can take from SESSION_AJAD
//        AcademicSessionSetup academicSessionSetup = new AcademicSessionSetup("1083");
//        academicSessionSetup.createXMLFile("TTUTESTTIMEPATTERNS","FALL","2018");
//
//        Preferences preferences = new Preferences();
//        preferences.createXMLFile("TTU", "Fall", "2018");

        Curricula curricula = new Curricula();
        curricula.createXMLFile("TTU", "Fall", "2018");

    }
}
