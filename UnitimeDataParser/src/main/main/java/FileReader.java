import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileReader {

    public static void read(String file) {

        String csvFile = file;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        int counter = 0;

        try {

            br = new BufferedReader(new java.io.FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                counter += 1;

                String[] country = line.split(cvsSplitBy);

                System.out.println("Tunniplaan [algus= " + country[0] + " , lopp=" + country[1] + "]" + counter);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
