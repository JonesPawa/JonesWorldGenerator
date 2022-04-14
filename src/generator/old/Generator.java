package generator.old;

import generator.Settings;
import generator.content_types.Skin;
import generator.content_types.SkinType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static generator.Settings.*;

public class Generator {

    private static final String CUSTOM_NAME = "custom";
    private static final String CUSTOM_SLIM_NAME = "customSlim";

    public static String formattedSkinPackage = PACK_NAME.replaceAll(" ", "");

    private static List<Skin> skins = new ArrayList<>();


    public static void main(String[] args) throws IOException {
        createSkins();

        String file = "output/";
        Path path = Path.of(file);
        deleteDirectoryRecursion(path);

        generateFile("", "Store Art/" + formattedSkinPackage + "_Thumbnail_0.jpg");
        generateFile("", "Marketing Art/" + formattedSkinPackage + "_MarketingKeyArt.png");
        generateFile("", "Marketing Art/" + formattedSkinPackage + "_PartnerArt.jpg");
        generateFile(lang(), "Content/skin_pack/texts/en_US.lang");
        generateFile(langJson(), "Content/skin_pack/texts/languages.json");
        generateFile(skinsJson(), "Content/skin_pack/skins.json");
        generateFile(manifestJson(), "Content/skin_pack/manifest.json");

        renameImages();
    }

    private static void createSkins() {
        String[] names = new String[SKIN_NAMES.length];
        for (int i = 0; i < SKIN_NAMES.length; i++) {
            names[SKIN_NUMBERS[i] - 1] = SKIN_NAMES[i];
        }

        int counter = 0;
        for (String name : names) {
            skins.add(new Skin(name, counter, SkinType.CUSTOM, null, false));
            counter++;
        }
    }

    private static void renameImages() throws IOException {
        // Creates an array in which we will store the names of files and directories
        String[] rawPathnames;

        // Creates a new File instance by converting the given pathname string
        // into an abstract pathname
        File inputFolder = new File(INPUT_DIRECTORY);
        Path outputDir = Paths.get("/Users/jonas/IdeaProjects/MinecraftPro/JonesSkinGenerator/output/Content/skin_pack");

        // Populates the array with names of files and directories
        rawPathnames = Arrays.stream(inputFolder.list()).sorted().collect(Collectors.toList()).toArray(new String[0]);

        // Order the pathnames
        String[] orderedPathnames = new String[rawPathnames.length];
        for (String pathname : rawPathnames) {
            String numberString = pathname.substring(0, 2).replaceAll("[^0-9]+", "");
            int number = Integer.parseInt(numberString) - 1;
            orderedPathnames[number] = pathname;
        }

        // For each pathname in the pathnames array
        int counter = 0;
        for (String pathname : orderedPathnames) {
            // Print the names of files and directories
            System.out.println(pathname);
            Path oneFile = Paths.get("/Users/jonas/IdeaProjects/MinecraftPro/JonesSkinGenerator/input/" + pathname);
            String geometry;
            if (true) {
                geometry = CUSTOM_NAME;
            } else {
                geometry = CUSTOM_SLIM_NAME;
            }
            Files.copy(oneFile, outputDir.resolve(SKIN_NUMBERS[counter] + SKIN_NAMES[counter].replaceAll(" ", "") + "_" + geometry + ".png"),
                    StandardCopyOption.REPLACE_EXISTING);

            counter++;
        }
    }

    private static void deleteDirectoryRecursion(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectoryRecursion(entry);
                }
            }
        }
        Files.delete(path);
    }

    private static String lang() {
        StringBuilder stringBuilder = new StringBuilder();

        // First line
        stringBuilder.append("skinpack." + formattedSkinPackage + "=" + PACK_NAME);
        stringBuilder.append("\n");

        // Add all skin names
        for (int i = 0; i < skins.size(); i++) {
            String skinName = skins.get(i).getName();
            String strippedName = skinName.replaceAll(" ", "").replaceAll("-", "");

            stringBuilder.append("skin.").append(formattedSkinPackage).append(".");
            stringBuilder.append(strippedName).append("=").append(skinName.replaceAll("_", " "));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private static String langJson() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(
                "[\n" +
                        "  \"en_US\"\n" +
                        "]"
        );
        return stringBuilder.toString();
    }

    private static String manifestJson() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n" +
                "  \"format_version\": 1,\n" +
                "  \"header\": {\n" +
                "    \"name\": \"" + formattedSkinPackage + "\",\n" +
                "    \"version\": [1, 0, 0],\n" +
                "    \"uuid\": \"" + UUID.randomUUID() + "\"\n" +
                "  },\n" +
                "  \"modules\": [\n" +
                "    {\n" +
                "      \"version\": [1, 0, 0],\n" +
                "      \"type\": \"skin_pack\",\n" +
                "      \"uuid\": \"" + UUID.randomUUID() + "\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"metadata\" : {\n" +
                "\t\t\"authors\" : [ \"JonesPawa\" ]\n" +
                "\t}\n" +
                "}\n");
        return stringBuilder.toString();
    }


    private static String skinsJson() {
//        String textureName = "Player";
//        String skinName = "SkinPlayer";
        String geometry;
        if (true) {
            geometry = CUSTOM_NAME;
        } else {
            geometry = CUSTOM_SLIM_NAME;
        }
        StringBuilder stringBuilder = new StringBuilder();

        // First lines
        stringBuilder.append("{\n" +
                "  \"skins\":\n" +
                "  [" + "\n");

        for (int i = 0; i < skins.size(); i++) {
//            String skinName = skinNames[i];
            String skinName = skins.get(i).getName().replaceAll(" ", "");
            String fileName = skins.get(i).getName().replaceAll(" ", "") + "_" + geometry;


            stringBuilder.append("{");
            stringBuilder.append("\n");
            stringBuilder.append("\t");
            stringBuilder.append("\"localization_name\": \"" + skinName);
            stringBuilder.append("\"" + ",");

            stringBuilder.append("\n\t");
            stringBuilder.append("\"geometry\": \"geometry.humanoid.");
            stringBuilder.append(geometry);
            stringBuilder.append("\",");

            stringBuilder.append("\n\t");
            stringBuilder.append("\"texture\": \"");

            //Add number
            if (true) {
                stringBuilder.append(i + 1);
            }

            stringBuilder.append(fileName);

            stringBuilder.append(".png\",");
            stringBuilder.append("\n\t");
            stringBuilder.append("\"type\": \"paid\"");

            stringBuilder.append("\n");
            stringBuilder.append("},");
            stringBuilder.append("\n");
        }
        String result = stringBuilder.substring(0, stringBuilder.length() - 2);

        result += "\n" +
                "  ],\n" +
                "  \"serialize_name\": \"" + formattedSkinPackage + "\",\n" +
                "  \"localization_name\": \"" + formattedSkinPackage + "\"\n" +
                "}\n";

        return result;
    }

    private static void generateFile(String stringToWrite, String outputFile) {
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
}
