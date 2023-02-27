import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Checker {
    // private data elements
    private static final String desktopPath = "/Users/tomlauth/Desktop/";
    private static final String zipFile = "Uebungsblatt_5.zip";
    private static final String correctionPath = "/Users/tomlauth/Desktop/Korrektur/";

    private static Path checkFilePath = Paths.get(correctionPath + "Check.txt");
    private static File checkFile = checkFilePath.toFile();

    private static final int COUNT_OF_TASKS = 5;

    public static void main(String[] args) {
        try {
            // @TestRuns
            Unzip.unzipFolder(desktopPath + zipFile, correctionPath);
            Unzip.checkCorrrectionFolder(correctionPath);
            Unzip.searchResultFolder(correctionPath);

            // Check File wird hier erstellt
            // Diese Datei wird benutzt um die Korrektur der einzelnen Gruppen zu bewerten
            if (!checkFilePath.toFile().exists()) {
                checkFilePath.toFile().createNewFile();
                System.out.println("Check File wurde erstellt");
            } else {
                System.out.println("Check File exisitiert bereits schon");
            }

            listGroups(correctionPath);
            System.out.println("Es wurde fertig in die Check.txt geschrieben!");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class Unzip {

        public static void unzipFolder(String zipFilePath, String destDir) {
            byte[] buffer = new byte[1024];
            File destDirFile = new File(destDir);
            if (!destDirFile.exists()) {
                destDirFile.mkdir();
            }

            try {
                ZipFile zipFile = new ZipFile(zipFilePath);

                for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements();) {
                    ZipEntry zipEntry = entries.nextElement();
                    String fileName = zipEntry.getName();
                    File newFile = new File(destDir + File.separator + fileName);

                    if (zipEntry.isDirectory()) {
                        newFile.mkdirs();
                    } else {
                        File parent = newFile.getParentFile();
                        if (!parent.exists()) {
                            parent.mkdirs();
                        }
                        InputStream inputStream = zipFile.getInputStream(zipEntry);
                        FileOutputStream fos = new FileOutputStream(newFile);
                        int len;
                        while ((len = inputStream.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();
                        inputStream.close();
                    }
                }

                zipFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Die Methode durchsucht den 'Korrektur' Ordner nach .zip Dateien
        // Wenn eine .zip Datei gefunden worden ist, soll er diese
        // in den 'Result' Ordner entpacken
        public static void checkCorrrectionFolder(String FilePath) {
            File[] folder = new File(FilePath).listFiles();
            File result;
            for (File f : folder) {
                if (f.isDirectory()) // pruefen ob f ein Verzeichnis ist, wenn ja dann wir die Methode rekursiv
                                     // nochmal aufgerufen
                    checkCorrrectionFolder(f.getAbsolutePath());

                if (f.toString().endsWith(".zip")) {
                    // System.out.println("ZipDatei Pfad: " + f.getAbsolutePath() + "\n" +
                    // "destDir Pfad: " + f.getParent());
                    result = new File(f.getParent() + "/Result");
                    unzipFolder(f.getAbsolutePath(), result.toString()); // wenn eine zip-Datei gefunden worden ist soll
                                                                       // diese
                    // noch entpackt werden.
                }
            }
        }

        public static void searchResultFolder(String correctionFolder) {
            File[] folder = new File(correctionFolder).listFiles();
            for (File f : folder) {
                if (f.isDirectory() && !f.getName().equals("1_task")) {
                    if (f.getName().equals("Result")) {
                        // System.out.println("Result Ordner Pfad: " + f.getAbsolutePath());
                        moveFilesFromSubfoldersToFolder(f.getAbsolutePath());
                        System.out.println();
                    }
                    searchResultFolder(f.getAbsolutePath());
                }
            }
        }

        // Bekommt den Result ordner als uebergabe und soll die ganzen subordner
        // durchgehen
        // und die dateien verschieben
        public static void moveFilesFromSubfoldersToFolder(String resultFolder) {
            try {
                File[] result = new File(resultFolder).listFiles(File::isDirectory);
                for (File dirs : result) {
                    String resultPath = dirs.getParent();
                    if (dirs.isDirectory() && !dirs.toString().contains("__MACOSX")) {
                        System.out.println("Aktuelles Verzeichnis: " + dirs);
                        moveFilesFromSubfoldersToFolder(dirs.getAbsolutePath());
                        // Dateien werden nun verschoben
                        File[] files = dirs.listFiles(File::isFile);
                        for (File f : files) {
                            if (f.toString().endsWith(".java") || f.toString().endsWith(".pdf")
                                    || f.toString().endsWith(".txt"))
                                System.out.println("Datei: " + f.getName() + " wurde erfolgreich nach " + resultPath
                                        + " verschoben");
                            Path sourcePath = f.toPath();
                            Path targetPath = new File(resultPath).toPath();
                            Path desPath = targetPath.resolve(f.getName());
                            Files.move(sourcePath, desPath);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void listGroups(String correctionPath) {
        File[] groups = new File(correctionPath).listFiles();
        Arrays.sort(groups);
        for (File group : groups) {
            String groupname = group.getName().replaceAll("^(\\w+)_.*$", "$1");
            System.out.println("\nGruppennamen: " + groupname);
            // normalerweise wird hier die writeIntoFile() Methode aufgerufen
            if (group.isDirectory())
                getResultFolder(group, groupname);
        }
    }

    public static void getResultFolder(File group, String groupname) {
        File[] dirs = group.listFiles();
        for (File dir : dirs) {
            if (dir.isDirectory() && !dir.getName().equals("1_task")) {
                if (dir.getName().equals("Result")) {
                    // writeIntoFile Methode soll hier aufgerufen werden
                    writeIntoFile(dir, groupname);
                    // System.out.println(dir);
                }
                getResultFolder(dir, groupname);
            }
        }
    }

    public static void writeIntoFile(File result, String groupname) {
        File[] files = result.listFiles(e -> e.isFile() && e.getName().endsWith(".java")); // hole mir alle dateien, die
                                                                                           // mit .java enden
        File[] txtFile = result.listFiles(e -> e.isFile() && e.getName().endsWith(".txt")); // hole mir nur die .txt
                                                                                            // Dateien wo die Mitglieder
        // Jede .txt datei durchgehen und diese in die Check.txt schreiben                                                                                    // drinnen stehen
        Arrays.stream(txtFile).forEach(e -> {
            writeMembersToFile(e, groupname);
        });

        // Jede .java datei durchgehen und diese nach umlauten ueberpuefen
        Arrays.stream(files).forEach(e -> {
            System.out.println("Java Datei: " + e);
        });
    }

    public static void writeMembersToFile(File file, String groupname) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(checkFile,true));

            String line, tasks = generateTasks(COUNT_OF_TASKS);
            writer.write("\nGruppennamen: " + groupname + "\n");
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            writer.write("\n" + tasks + "\n");
            writer.newLine();

            reader.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    public static String generateTasks(int tasks) {
        String result = "";
        for (int i = 1; i <= tasks; i++) {
            String tmp = "Aufgabe " + i + ": " + "0" + "\n";
            result += tmp;
        }
        result += "\nGesamt: 0 / 20"  + "\n";
        return result;
    }

    @Deprecated
    public static void checkUmlauts(String file) {
        try {
            File tmp = new File(file);
            File[] files = tmp.listFiles();
            for (File f : files) {
                if (f.toString().endsWith(".java")) {
                    BufferedReader reader = new BufferedReader(new FileReader(f));
                    System.out.println("\nDateiname: " + f.toString());
                    String line;
                    int lineNumber = 0, found = 0;
                    Pattern pattern = Pattern.compile("[äöüÄÖÜß]");
                    while ((line = reader.readLine()) != null) {
                        lineNumber++;
                        Matcher matcher = pattern.matcher(line);
                        while (matcher.find()) {
                            System.out.println(
                                    "Umlaut gefunden in Zeile " + lineNumber + ": " + matcher.group() + " in "
                                            + line);
                            found++;
                        }
                    }
                    if (found == 0)
                        System.out.println("Es wurden keine Umlaute in der Datei gefunden");
                    reader.close();
                }
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Datei: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
