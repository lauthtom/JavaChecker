/* 
TODOS:
- gibt uebersichtlicher die verschiedenen Ergebnisse der Gruppen aus (Bessere Ausgabe)
- Wenn eine .zip Datei mehrere .zip Dateien enthaelt, dann soll das Programm alle .zip Dateien auch entpacken (rekursiv)
- Eine Klasse schreiben, die mir eine BewertungXY.txt erstellt und mir eine Vorlage erstellt und schon die vorhandenen Mitglieder.txt zu den jeweiligen Gruppen schreiben
- Eventuell eine schoenere Eingabe per Kommandozeilenparameter ... 
- Kommandozeilenparameter bitte noch in der main pruefen, fehlermeldung sollte ein Beispiel fuer einen Aufruf zeigen

Bedienung: 
- Die "String path" variable benoetigt einen pfad zur .zip Datei und name braucht den namen der .zip Datei
- Die unzip.() Methode braucht noch den Gruppen Namen z.B. (UXGXY) und einen pfad 

Tests: 
- Testen ob die Version auch auf einem Windows System funktioniert (evtl. Typ pruefung) --> hat funktioniert, keine weitere typ pruefeung notwendig 
*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Checker {
    private static final String desktopPath = "/Users/tomlauth/Desktop/";
    private static final String zipFile = "Uebungsblatt_5.zip";
    private static final String correctionPath = "/Users/tomlauth/Desktop/Korrektur";

    public static void main(String[] args) {

        // @TestRuns
        Unzip.testUnzip(desktopPath + zipFile, correctionPath);
        Unzip.checkCorrrectionFolder(correctionPath);
        Unzip.searchResultFolder(correctionPath);
    }

    // TODO: Folgendes Problem
    // Wenn in einer Zip datei unterordner vorhanden entpackt er diese nicht richtig
    // ...
    public class Unzip {

        // Private class Dataelements
        private static FileInputStream fis;

        /*
         * // Methode zum entpacken einer .zip Datei
         * public static void unzip(String zipFilePath, String destDir) {
         * File dir = new File(destDir);
         * if (!dir.exists())
         * dir.mkdir(); // Falls destDir noch nicht exisitert, dann wird er hier
         * erstellt
         * 
         * byte[] buffer = new byte[1024]; // zwischenspeicher zum lesen und schreib von
         * daten
         * try {
         * fis = new FileInputStream(zipFilePath);
         * ZipInputStream zis = new ZipInputStream(fis);
         * ZipEntry ze = zis.getNextEntry();
         * while (ze != null) {
         * String fileName = ze.getName();
         * File newFile = new File(destDir + File.separator + fileName);
         * System.out.println("Unzipping to " + newFile.getAbsolutePath());
         * new File(newFile.getParent()).mkdirs(); // erstellt unterordner von der zip
         * datei
         * FileOutputStream fos = new FileOutputStream(newFile);
         * int len;
         * while ((len = zis.read(buffer)) > 0) {
         * fos.write(buffer, 0, len);
         * }
         * fos.close();
         * zis.closeEntry(); // schliesst den aktuellen Zip Eintrag
         * ze = zis.getNextEntry();
         * }
         * zis.closeEntry();
         * zis.close();
         * fis.close();
         * } catch (IOException e) {
         * e.printStackTrace();
         * }
         * }
         */

        public static void testUnzip(String zipFilePath, String destDir) {
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
                    System.out.println("ZipDatei Pfad: " + f.getAbsolutePath() + "\n" +
                            "destDir Pfad: " + f.getParent());
                    result = new File(f.getParent() + "/Result");
                    testUnzip(f.getAbsolutePath(), result.toString()); // wenn eine zip-Datei gefunden worden ist soll
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
                        System.out.println("Result Ordner Pfad: " + f.getAbsolutePath());
                        moveFilesFromSubfoldersToFolder(f.getAbsolutePath());
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
                System.out.println("Result Pfad 2: " + resultFolder);
                // File[] result = new File(resultFolder).listFiles(File::isDirectory);
                // for (File file : result) {
                //     // System.out.println("Datei: " + file.getName());
                //     if (!file.getPath().toString().contains("__MACOSX")) {
                //         // System.out.println("Pfad: " + file.getPath());
                //         moveFilesFromSubfoldersToFolder(file.toString());
                //         File[] subfolder = file.listFiles(File::isDirectory); 
                //         for (File f : subfolder) {
                //             File[] files = f.listFiles();
                //             for (File ff : files) {
                //                 if (ff.toString().endsWith(".java") || ff.toString().endsWith(".pdf") || ff.toString().endsWith(".txt"))
                //                 System.out.println("Datei: " + ff.getAbsolutePath());
                //             }
                //         }
                //         // for (File f : files) {
                //         //     System.out.println("Dateien: " + f.toPath());
                //         //     Path sourcePath = f.toPath();
                //         //     Path desPath = new File(file.getParent()).toPath();
                //             // System.out.println("Source: " + sourcePath.toAbsolutePath() + "\n" + 
                //                 // "Destination: " + desPath.toAbsolutePath());
                //             // Files.move(sourcePath, desPath);
                //         // }
                //         // System.out.println("Dateien: " + f.getAbsolutePath());
                //         // System.out.println("Dir gefunden: " + file.getAbsolutePath() + "\n" +
                //         // "Result Pfad: " + file.getParent());
                //     }
                // }
                // for (File subfolder : subfolders) {
                // File[] files = subfolder.listFiles();
                // for (File file : files) {
                // if (file.isFile() && !file.exists()) {
                // Path sourcePath = file.toPath();
                // Path destPath = new File(resultFolder, file.getName()).toPath();
                // Files.move(sourcePath, destPath);
                // }
                // }
                // }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // TODO: Brauch ich die Klasse ueberhaupt noch, wenn ich den 'Result' Ordner
    // direkt mit der unzip Methode erstelle ?
    private class MoveFiles {

        private static void moveJavaFiles(String sourceDir, String targetDir) {
            File source = new File(sourceDir), target = new File(targetDir);

            if (!source.exists())
                System.out.println("Dateipfad exisitiert nicht!.");

            if (!target.exists()) {
                System.out.println("Zielpfad existiert nicht, wird aber jetzt erstellt!");
                target.mkdir();
                moveJavaFiles(source, target);
            } else {
                System.out.println("Target Directory is already there");
            }

        }

        // Die methode bekommt einen dateipfad (normalerweise die UXGXY Gruppen)
        // erstellt einen "Result" Ordner und verschiebt alle dateien in den Ordner
        // Geht auch rekursiv in die verschiedenen Unterordner (falls vorhanden) und
        // verschiebt die Dateien auch
        private static void moveJavaFiles(File source, File target) {
            File[] files = source.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        moveJavaFiles(file, target);
                    } else if (file.toString().endsWith(".java") || file.toString().endsWith(".txt")
                            || file.toString().endsWith(".pdf")) {
                        File newFile = new File(target.getAbsolutePath() + File.separator + file.getName());
                        if (file.renameTo(newFile)) {
                            System.out.println(
                                    file.getAbsolutePath() + " wurde verschoben zu " + newFile.getAbsolutePath());
                        } else {
                            System.out.println(file.getAbsolutePath() + " konnte nicht verschoben werden");
                        }
                    }
                }
            }
        }

    }

    // Die Methode ueberprueft eine Java Datei nach Umlauten
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