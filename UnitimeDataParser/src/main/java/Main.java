import toXmlParser.*;

import java.sql.SQLException;

public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
//        BuildingsAndRooms buildingsAndRooms = new BuildingsAndRooms();
//        buildingsAndRooms.buildXML();

//        Departments departments = new Departments();
//        departments.buildXML();
//
//        SubjectAreas subjectAreas = new SubjectAreas();
//        subjectAreas.buildXML();
//
//        CourseCatalog courseCatalog = new CourseCatalog();
//        courseCatalog.buildXML();

        AcademicSessionSetup academicSessionSetup = new AcademicSessionSetup();
        academicSessionSetup.buildXML();

    }
}
