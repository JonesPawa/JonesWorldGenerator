package generator.content_types;

import generator.Settings;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;


public class Utils {
    public static void generateFile(String stringToWrite, String outputFile) {
        String file = "output/" + outputFile;
        Path path = Path.of(file);
//        Files.createDirectories(path.getParent());
        Path parentDir = path.getParent();
        if (!Files.exists(parentDir)) {
            try {
                Files.createDirectories(parentDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Files.writeString(path, stringToWrite);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            FileWriter writer = new FileWriter(file);
            writer.append(stringToWrite);
            writer.flush();
            writer.close();
            System.out.println("New File is generated ==> " + outputFile);
        } catch (Exception exp) {
            System.out.println("Exception in generateFile " + exp);
        }
    }

    public static void deleteDirectoryRecursion(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectoryRecursion(entry);
                }
            }
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            System.out.println("No such file");
        }
    }
}
