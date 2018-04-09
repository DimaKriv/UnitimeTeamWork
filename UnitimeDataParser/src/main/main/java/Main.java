import java.sql.SQLException;

public class Main {
    public static void main(String[] arg) throws SQLException {
        ConnectionSer connection = new ConnectionSer(new FileReader());
        connection.createConnection(connection.getConsoleReader());
    }
}
