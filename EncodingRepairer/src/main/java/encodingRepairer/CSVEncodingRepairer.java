package encodingRepairer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class CSVEncodingRepairer {

    private final String PATH_TO_CSV_FILES = "../andmed/TUNNIPLAAN";

    public void repairEncodingInCSVFiles() {
        List<File> files = getFilesList(PATH_TO_CSV_FILES);

        for (File oldFile : files) {

            // rename current file to filename + .temp
            String oldFilePath = oldFile.getPath();
            String renamedFilePath = oldFilePath + ".temp";
            File renamedFile = new File(renamedFilePath);
            boolean isRenamed = oldFile.renameTo(renamedFile);

            System.out.println("Rename to " + renamedFile.getName() + " success: " + isRenamed);

            if (isRenamed) {
                try {
                    // create new empty file with old name of current file
                    File newFile = new File(oldFilePath);
//                    boolean isCreated = newFile.createNewFile();
//                    System.out.println("Create " + newFile.getName() + " success: " + isCreated);

                    BufferedReader reader = new BufferedReader(new FileReader(renamedFile));
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(newFile), StandardCharsets.UTF_8));

                    String currentLine;

                    while ((currentLine = reader.readLine()) != null) {

                        // estonian special letters
                        currentLine = currentLine.replace("\\u00c3\\u0083\\u00c2\\u00b6", "ö");
                        currentLine = currentLine.replace("\\u00c3\\u0083\\u00c2\\u00b5", "õ");
                        currentLine = currentLine.replace("\\u00c3\\u0083\\u00c2\\u00a4", "ä");
                        currentLine = currentLine.replace("\\u00c3\\u0083\\u00c2\\u00bc", "ü");
                        currentLine = currentLine.replace("\\u00c3\\u0085\\u00c2\\u00a1", "š");
                        currentLine = currentLine.replace("\\u00c3\\u0085\\u00c2\\u00be", "ž");
                        currentLine = currentLine.replace("\\u00c3\\u0083\\u00c2\\u009c", "Ü");
                        currentLine = currentLine.replace("\\u00c3\\u0083\\u00c2\\u0095", "Õ");
                        currentLine = currentLine.replace("\\u00c3\\u0083\\u00c2\\u0084", "Ä");

                        // special symbols
                        currentLine = currentLine.replace("\\t", " ");
                        currentLine = currentLine.replace("\\r\\n", " ");
                        currentLine = currentLine.replace("\\/", "/");
                        currentLine = currentLine.replace("\\\"", "\'");

                        // unknown symbols
                        currentLine = currentLine.replace("\\u00c3\\u00a2\\u00c2\\u0080\\u00c2\\u00a2", "");
                        currentLine = currentLine.replace("\\u00c3\\u00a2\\u00c2\\u0080\\u00c2\\u0093", "");
                        currentLine = currentLine.replace("\\u00c3\\u00af\\u00c2\\u0081\\u00c2\\u008a", "");
                        currentLine = currentLine.replace("\\u00c3\\u00a2\\u00c2\\u0080\\u00c2\\u009e", "");
                        currentLine = currentLine.replace("\\u00c3\\u00a2\\u00c2\\u0080\\u00c2\\u009c", "");
                        currentLine = currentLine.replace("\\u00c3\\u00a2\\u00c2\\u0080\\u00c2\\u00a6", "");
                        currentLine = currentLine.replace("\\u00c3\\u0082\\u00c2\\u00b4", "");

                        currentLine = currentLine.trim();

                        writer.write(currentLine);
                        writer.newLine();
                    }

                    reader.close();
                    writer.close();

                    boolean isDeleted = renamedFile.delete();
                    System.out.println("Delete " + renamedFile.getName() + " success: " + isDeleted + '\n');


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * List all the files under a directory
     * @param directoryName to be listed
     */
    private List<File> getFilesList(String directoryName){
        List<File> filesList = new LinkedList<>();

        File directory = new File(directoryName);
        Optional<File[]> filesArrayOptional = Optional.ofNullable(directory.listFiles());
        if (filesArrayOptional.isPresent()) {
            File[] filesArray = filesArrayOptional.get();
            filesList = Arrays.asList(filesArray);
        }
        return filesList;
    }

    private void readLineFromFile(File file) throws IOException {

    }

    private void writeLineToFile(File file) throws IOException {

    }
}
