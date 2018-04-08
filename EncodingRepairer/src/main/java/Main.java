import encodingRepairer.CSVEncodingRepairer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        CSVEncodingRepairer repairer = new CSVEncodingRepairer();
        repairer.repairEncodingInCSVFiles();
    }
}
