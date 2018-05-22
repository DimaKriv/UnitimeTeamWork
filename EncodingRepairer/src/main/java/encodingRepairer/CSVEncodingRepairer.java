package encodingRepairer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CSVEncodingRepairer {

    private String pathToPropertiesFile;
    private String pathToCsvFilesDirectory;

    public CSVEncodingRepairer() {
        pathToPropertiesFile = "properties.txt";
        pathToCsvFilesDirectory = getPathToCsvFilesDirectoryFromPropertiesFile(pathToPropertiesFile);
    }

    public CSVEncodingRepairer(String pathToPropertiesFile, String pathToCsvFilesDirectory) {
        this.pathToPropertiesFile = pathToPropertiesFile;
        this.pathToCsvFilesDirectory = pathToCsvFilesDirectory;
    }

    /**
     * Gets path to directory in which are placed incorrectly encoded files from properties file
     * (this path should be written in properties file like PathToCsvFilesDirectory=SOME_PATH_HERE)
     *
     * @param pathToPropertiesFile path to properties file location (including filename)
     * @return path to directory in which should be repaired incorrectly encoded files
     * if properties file is presented and PathToCsvFilesDirectory value is not empty or
     * otherwise returns . (path to current repairer location)
     */
    private String getPathToCsvFilesDirectoryFromPropertiesFile(String pathToPropertiesFile) {
        File propertiesFile = new File(pathToPropertiesFile);
        String pathToCsvFilesDirectory = ".";
        if (propertiesFile.canRead()) {
            try {
                Optional<String> pathToCsvFilesDirectoryOptional = Files.lines(Paths.get(pathToPropertiesFile), Charset.forName("UTF-8"))
                        .filter(line -> line.contains("PathToCsvFilesDirectory")).findFirst();
                if (pathToCsvFilesDirectoryOptional.isPresent()) {
                    pathToCsvFilesDirectory = pathToCsvFilesDirectoryOptional.get();
                    if (pathToCsvFilesDirectory.split("=").length > 1) {
                        // get value from string like PathToCsvFilesDirectory=SOME_VALUE (value is after equals sign)
                        pathToCsvFilesDirectory = pathToCsvFilesDirectory.split("=")[1];
                        pathToCsvFilesDirectory = pathToCsvFilesDirectory.trim();
                    } else {
                        pathToCsvFilesDirectory = ".";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                pathToCsvFilesDirectory = ".";
            }
        }
        return pathToCsvFilesDirectory;
    }

    /**
     * List all the files under a directory
     *
     * @param directoryName to be listed
     */
    private List<File> getFilesList(String directoryName) {
        List<File> filesList = new LinkedList<>();

        File directory = new File(directoryName);
        Optional<File[]> filesArrayOptional = Optional.ofNullable(directory.listFiles());
        if (filesArrayOptional.isPresent()) {
            File[] filesArray = filesArrayOptional.get();
            filesList = Arrays.asList(filesArray);
        }
        return filesList;
    }

    /**
     * Get file extension (using pattern like filename.extension)
     *
     * @param file file, which extension to get
     * @return extension of file (like extension) if it is presented or
     * empty string if there is not fount file extension
     */
    private String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    /**
     * Get list of files with csv extension from unchecked files list
     *
     * @param files list of files from which files with csv extension will be gotten
     * @return list of files with csv extension (like filename.csv)
     */
    private List<File> getCSVFilesList(List<File> files) {
        List<File> csvFilesList = new ArrayList<>();
        for (File file : files) {
            if (getFileExtension(file).equals("csv")) {
                csvFilesList.add(file);
            }
        }
        return csvFilesList;
    }

    public void repairEncodingInCSVFiles() {
        List<File> files = getFilesList(pathToCsvFilesDirectory);
        List<File> csvFiles = getCSVFilesList(files);

        for (File oldFile : csvFiles) {

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
                    boolean isCreated = newFile.createNewFile();
                    System.out.println("Create " + newFile.getName() + " success: " + isCreated);

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(new FileInputStream(renamedFile), Charset.forName("UTF-8").newDecoder()));
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(newFile), Charset.forName("UTF-8").newEncoder()));

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
}
