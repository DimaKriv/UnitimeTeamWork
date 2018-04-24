import parser.EapParser;

import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws IOException, SQLException {
        EapParser eapParser = new EapParser();
        eapParser.parseEAP();
    }

}
