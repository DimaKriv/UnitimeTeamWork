import java.util.Scanner;

public class ConsoleReader {

    public String readAndReturnPath() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter full private key path: ");
            if (!scanner.hasNext()) {
                System.out.println("empty...");
            } else if (scanner.hasNext()) {
                return scanner.next();
            }
        }
    }
}
