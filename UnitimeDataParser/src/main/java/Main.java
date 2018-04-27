import toXmlParser.*;
import toXmlParser.sessionsetup.AcademicSessionSetup;
import java.sql.SQLException;

public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        BuildingsAndRooms buildingsAndRooms = new BuildingsAndRooms();
        buildingsAndRooms.createXMLFile("TTU", "Fall", "2018");

        Departments departments = new Departments();
        departments.createXMLFile("TTU", "Fall", "2018");

        SubjectAreas subjectAreas = new SubjectAreas();
        subjectAreas.createXMLFile("TTU", "Fall", "2018");

        CourseOffering courseCatalog = new CourseOffering();
        courseCatalog.createXMLFile("TTU", "Fall", "2018");

       // AcademicSessionSetup academicSessionSetup = new AcademicSessionSetup("1123");
       // academicSessionSetup.buildXML();

    }
}
