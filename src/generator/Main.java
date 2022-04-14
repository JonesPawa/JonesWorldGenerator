package generator;

import generator.content_types.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Collator;
import java.util.*;

import static generator.Settings.SKIN_NAMES;
import static generator.Settings.SKIN_NUMBERS;

public class Main {
    WorldPack skinPack;

    public Main() {
        String packName = Settings.PACK_NAME;

        // Check if a UUID is provided
        UUID headerUUID;
        if (Settings.UPDATE) {
            headerUUID = UUID.fromString(Settings.FIX_UUID);
        } else {
            headerUUID = UUID.randomUUID();
        }
        UUID moduleUUID = UUID.randomUUID();
        skinPack = new WorldPack(packName, headerUUID, moduleUUID);

        // Setup skin pack
        clearDirectory();
        Set<Skin> skins = createSkinList();
        skinPack.setSkins(skins);

        // Setup partner art
        PackImage partnerArt = createPartnerArtImage();
        skinPack.setPartnerArt(partnerArt);

        // Setup key art
        PackImage keyArt = createKeyArtImage();
        skinPack.setMarketingKeyArt(keyArt);

        // Setup pack icon
        PackImage packIcon = createPackImage("packIcon");
        skinPack.setPackIcon(packIcon);

        // Setup panorama
        PackImage panorama = createPackImage("panorama");
        skinPack.setPanorama(panorama);

        // Setup screenshots
        ArrayList<Path> screenshots = createListFrom("screenshots");
        skinPack.setScreenshots(screenshots);

        // Setup screenshots
        ArrayList<Path> screenshotsJPG = createListFrom("screenshotsJPG");
        skinPack.setScreenshotsJPG(screenshotsJPG);

        // Generate the skin pack
        skinPack.generate();
    }

    public static void main(String[] args) {
        new Main();
    }

    private Set<Skin> createSkinList() {
        Set<Skin> skins = new HashSet<>();

        // Creates a new File instance by converting the given pathname string into an abstract pathname
        String pathname = Settings.INPUT_DIRECTORY + "skins/";

        File inputFolder = new File(pathname);

        // Populates the array with names of files and directories
        String[] rawPathNames = Arrays.stream(inputFolder.list()).filter(s -> !s.startsWith(".")).sorted().toArray(String[]::new);

        // Order the path names
        String[] orderedPathNames = orderPathNames(rawPathNames);

        for (int i = 0; i < orderedPathNames.length; i++) {
            System.out.println(i + 1 + ". " + orderedPathNames[i]);
        }

        // Create the skins
        for (int i = 0; i < SKIN_NAMES.length; i++) {
            String skinName = SKIN_NAMES[i];
            int skinNumber = SKIN_NUMBERS[i];
            Path path = Paths.get(pathname + orderedPathNames[i]);
            boolean free = Arrays.stream(Settings.FREE_SKIN_NUMBERS).anyMatch(value -> value == skinNumber);
            SkinType skinType = Settings.SKIN_TYPE;
            if (Arrays.stream(Settings.CUSTOM_SKIN_TYPE_NUMBERS).anyMatch(value -> value == skinNumber)) {
                skinType = SkinType.CUSTOM;
            }
            Skin skin = new Skin(skinName, skinNumber, skinType, path, free);
            skins.add(skin);
        }
        return skins;
    }

    private ArrayList<Path> createListFrom(String folder) {
        ArrayList<Path> list = new ArrayList<>();

        // Creates a new File instance by converting the given pathname string into an abstract pathname
        String pathname = Settings.INPUT_DIRECTORY + folder + File.separatorChar;

        File inputFolder = new File(pathname);

        // Populates the array with names of files and directories
        String[] rawPathNames = Arrays.stream(inputFolder.list()).filter(s -> !s.startsWith(".")).sorted().toArray(String[]::new);

        // Order the path names
        String[] orderedPathNames = orderPathNames(rawPathNames);

        for (int i = 0; i < orderedPathNames.length; i++) {
            System.out.println(i + 1 + ". " + orderedPathNames[i]);
        }

        // Create the skins
        for (int i = 0; i < orderedPathNames.length; i++) {
            Path path = Paths.get(pathname + orderedPathNames[i]);
            list.add(path);
        }
        return list;
    }

    /**
     * Setup key art
     *
     * @return
     */
    private PackImage createKeyArtImage() {
        // Creates a new File instance by converting the given pathname string into an abstract pathname
        String pathname = Settings.INPUT_DIRECTORY + "keyArt/";
        File inputFolder = new File(pathname);

        // Populates the array with names of files and directories
        String[] rawPathNames = Arrays.stream(inputFolder.list()).sorted().toArray(String[]::new);

        if (rawPathNames.length != 1) {
            System.err.println("No or too many key art images!");
        }

        // Get the only file from the folder
        String partnerArtPath = rawPathNames[0];
        return new PackImage(inputFolder + "/" + partnerArtPath);
    }
    /**
     * Setup pack Icon
     *
     * @return
     */
    private PackImage createPackImage(String folder) {
        // Creates a new File instance by converting the given pathname string into an abstract pathname
        String pathname = Settings.INPUT_DIRECTORY + folder + "/";
        File inputFolder = new File(pathname);

        // Populates the array with names of files and directories
        String[] rawPathNames = Arrays.stream(inputFolder.list()).sorted().toArray(String[]::new);

        if (rawPathNames.length != 1) {
            System.err.println("No or too many key art images!");
        }

        // Get the only file from the folder
        String path = rawPathNames[0];
        return new PackImage(inputFolder + "/" + path);
    }

    /**
     * Setup partner art
     *
     * @return
     */
    private PackImage createPartnerArtImage() {
        // Creates a new File instance by converting the given pathname string into an abstract pathname
        String pathname = Settings.INPUT_DIRECTORY + "partnerArt/";
        File inputFolder = new File(pathname);

        // Populates the array with names of files and directories
        String[] rawPathNames = Arrays.stream(inputFolder.list()).sorted().toArray(String[]::new);

        if (rawPathNames.length != 1) {
            System.err.println("No or too many partner art images!");
        }

        // Get the only file from the folder
        String partnerArtPath = rawPathNames[0];
        return new PackImage(inputFolder + "/" + partnerArtPath);
    }

    private String[] orderPathNames(String[] rawPathNames) {
        String[] orderedPathNames = new String[rawPathNames.length];
        for (String pathname : rawPathNames) {
            if (Settings.SORT_BY_ALPHABET) {
                ArrayList<String> namesList = new ArrayList<>(Arrays.asList(rawPathNames));
                namesList.sort(Collator.getInstance());
                for (int i = 0; i < namesList.size(); i++) {
                    orderedPathNames[i] = namesList.get(i);
                }
            } else {
//                System.out.println("Pathname: " + pathname);
                String numberString = pathname.substring(0, 2).replaceAll("[^0-9]+", "");
                int number = Integer.parseInt(numberString) - 1;
                orderedPathNames[number] = pathname;
            }
        }
        return orderedPathNames;
    }

    private void clearDirectory() {
        String file = "output/";
        Path path = Path.of(file);
        try {
            Utils.deleteDirectoryRecursion(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
