import curriculumBySubject.CurriculumBySubject;
import curriculumParser.CurriculumParser;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
//        CurriculumParser curriculumParser = new CurriculumParser();
//        curriculumParser.parseCurriculum();

        CurriculumBySubject curriculumBySubject = new CurriculumBySubject();
        curriculumBySubject.runParser();
    }

}
