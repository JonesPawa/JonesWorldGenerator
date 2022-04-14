package generator.content_types;

import generator.Settings;
import generator.old.Generator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class WorldPack {
    private final UUID headerUUID;
    private final UUID moduleUUID;
    private final String packName;
    private final String packNameFormatted;
    private PackImage marketingKeyArt;
    private PackImage thumbnail;
    private PackImage partnerArt;
    private Set<Skin> skins;
    private ArrayList<Path> screenshots = new ArrayList<>();
    private ArrayList<Path> screenshotsJPG = new ArrayList<>();
    private PackImage packIcon;
    private PackImage panorama;

    public WorldPack(String packName, UUID headerUUID, UUID moduleUUID) {
        this.headerUUID = headerUUID;
        this.packName = packName;
        this.packNameFormatted = packName.replaceAll(" ", "");
        this.moduleUUID = moduleUUID;
    }

    public void setMarketingKeyArt(PackImage marketingKeyArt) {
        this.marketingKeyArt = marketingKeyArt;
    }

    public void setThumbnail(PackImage thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setPartnerArt(PackImage partnerArt) {
        this.partnerArt = partnerArt;
    }

    public void setSkins(Set<Skin> skins) {
        this.skins = skins;
    }

    public void setScreenshots(ArrayList<Path> screenshots) {
        this.screenshots = screenshots;
    }

    public void setScreenshotsJPG(ArrayList<Path> screenshotsJPG) {
        this.screenshotsJPG = screenshotsJPG;
    }

    public void setPackIcon(PackImage packIcon) {
        this.packIcon = packIcon;
    }

    public void setPanorama(PackImage panorama) {
        this.panorama = panorama;
    }

    public void generate() {
//        Utils.generateFile("", "Store Art/" + packNameFormatted + "_Thumbnail_0.jpg");
//        Utils.generateFile("", "Marketing Art/" + packNameFormatted + "_MarketingKeyArt.png");
//        Utils.generateFile("", "Marketing Art/" + packNameFormatted + "_PartnerArt.jpg");


        Utils.generateFile(lang(), "Content/skin_pack/texts/en_US.lang");
        Utils.generateFile(langJson(), "Content/skin_pack/texts/languages.json");
        Utils.generateFile(skinsJson(), "Content/skin_pack/skins.json");
        Utils.generateFile(manifestJson(), "Content/skin_pack/manifest.json");

        generateImages();
        generatePartnerArt();
        generateKeyArt();
        generateThumbnail();

        generateImage(panorama.getPath(), "Store Art", "Panorama_0.jpg");
        generateImage(packIcon.getPath(), "Store Art", "PackIcon_0.jpg");

        for (int i = 0; i < screenshots.size(); i++) {
            Path path = screenshots.get(i);
            generateImage(path, "Marketing Art", "MarketingScreenshot_" + i + ".png");
        }

        for (int i = 0; i < screenshotsJPG.size(); i++) {
            Path path = screenshotsJPG.get(i);
            generateImage(path, "Store Art", "Screenshot_" + i + ".jpg");
        }

        // Zip the folder
        generateZipFile();

    }


    /**
     * Generates the en_US.lang file
     *
     * @return
     */
    private String lang() {
        StringBuilder stringBuilder = new StringBuilder();

        // First line
        stringBuilder.append("skinpack." + packNameFormatted + "=" + packName);
        stringBuilder.append("\n");

        // Add all skin names
        for (Skin skin : skins) {
            String skinName = skin.getName();
            String strippedName = skin.getStrippedName();

            stringBuilder.append("skin.").append(packNameFormatted).append(".");
            stringBuilder.append(strippedName).append("=").append(skinName);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Generates the languages.json file
     *
     * @return
     */
    private String langJson() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(
                "[\n" +
                        "  \"en_US\"\n" +
                        "]"
        );
        return stringBuilder.toString();
    }

    /**
     * Generates the skins.json file
     *
     * @return
     */
    private String skinsJson() {
        StringBuilder stringBuilder = new StringBuilder();

        // First lines
        stringBuilder.append("{\n" +
                "  \"skins\":\n" +
                "  [" + "\n");

        Skin[] orderedSkins = new Skin[skins.size()];
        for (Skin skin : skins) {
            orderedSkins[skin.getNumber() - 1] = skin;
        }

        for (Skin skin : orderedSkins) {
            String skinName = skin.getStrippedName();
            String fileName = skin.getStrippedName() + "_" + skin.getSkinType().getName();

            stringBuilder.append("{");
            stringBuilder.append("\n");
            stringBuilder.append("\t");
            stringBuilder.append("\"localization_name\": \"" + skinName);
            stringBuilder.append("\"" + ",");

            stringBuilder.append("\n\t");
            stringBuilder.append("\"geometry\": \"geometry.humanoid.");
            stringBuilder.append(skin.getSkinType().getName());
            stringBuilder.append("\",");

            stringBuilder.append("\n\t");
            stringBuilder.append("\"texture\": \"");

//            //Add number
            stringBuilder.append(skin.getNumber());

            stringBuilder.append(fileName);

            stringBuilder.append(".png\",");
            stringBuilder.append("\n\t");


            // Check if the skin is free
            if (skin.isFree()) {
                stringBuilder.append("\"type\": \"free\"");
            } else {
                stringBuilder.append("\"type\": \"paid\"");
            }

            stringBuilder.append("\n");
            stringBuilder.append("},");
            stringBuilder.append("\n");
        }
        String result = stringBuilder.substring(0, stringBuilder.length() - 2);

        result += "\n" +
                "  ],\n" +
                "  \"serialize_name\": \"" + packNameFormatted + "\",\n" +
                "  \"localization_name\": \"" + packNameFormatted + "\"\n" +
                "}\n";

        return result;
    }

    /**
     * Generates the manifest.json file
     *
     * @return
     */
    private String manifestJson() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n" +
                "  \"format_version\": 1,\n" +
                "  \"header\": {\n" +
                "    \"name\": \"" + packNameFormatted + "\",\n" +
                "    \"version\": [1, 0, 0],\n" +
                "    \"uuid\": \"" + headerUUID + "\"\n" +
                "  },\n" +
                "  \"modules\": [\n" +
                "    {\n" +
                "      \"version\": [1, 0, 0],\n" +
                "      \"type\": \"skin_pack\",\n" +
                "      \"uuid\": \"" + moduleUUID + "\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"metadata\" : {\n" +
                "\t\t\"authors\" : [ \"JonesPawa\" ]\n" +
                "\t}\n" +
                "}\n");
        return stringBuilder.toString();
    }

    /**
     * Generate the partner art image in the output directory
     */
    private void generatePartnerArt() {
        String outputDirectory = Settings.OUTPUT_DIRECTORY + "Marketing Art/";
        Path outputPath = Path.of(outputDirectory + packNameFormatted + "_PartnerArt.jpg");

        // Create directory
        try {
            Files.createDirectories(outputPath.getParent());
        } catch (IOException e) {
            System.out.println("Fail");
            e.printStackTrace();
        }

        try {
            System.out.println(outputPath);
            Files.copy(partnerArt.getPath(), outputPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate the partner art image in the output directory
     */
    private void generateKeyArt() {
        String outputDirectory = Settings.OUTPUT_DIRECTORY + "Marketing Art/";
        Path outputPath = Path.of(outputDirectory + packNameFormatted + "_MarketingKeyArt.jpg");

        try {
            Files.copy(marketingKeyArt.getPath(), outputPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate the image
     *
     * @param inputPath    the path to the input image
     * @param outputFolder the name of the output folder
     * @param fileName     the name after the pack name
     */
    private void generateImage(Path inputPath, String outputFolder, String fileName) {
        String outputDirectory = Settings.OUTPUT_DIRECTORY + outputFolder + "/";
        Path outputPath = Path.of(outputDirectory + packNameFormatted + "_" + fileName);

        try {
            Files.copy(inputPath, outputPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate the partner art image in the output directory
     */
    private void generateThumbnail() {
        String outputDirectory = Settings.OUTPUT_DIRECTORY + "Store Art/";
        Path outputPath = Path.of(outputDirectory + packNameFormatted + "_Thumbnail_0.jpg");

        // Create directory
        try {
            Files.createDirectories(outputPath.getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Files.copy(marketingKeyArt.getPath(), outputPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Resize image
        try {
            BufferedImage bufferedImage = ImageIO.read(outputPath.toFile());
            BufferedImage resizedImage = resizeImage(bufferedImage, 800, 450);
            ImageIO.write(resizedImage, "jpg", outputPath.toFile());
        } catch (IOException ignored) {
        }
    }

    BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }


    /**
     * Generate the images in the output directory
     */
    private void generateImages() {
        String outputDirectory = Settings.OUTPUT_DIRECTORY + "Content/skin_pack";
        Path outputDir = Paths.get(outputDirectory);

        for (Skin skin : skins) {
            try {
                String outputFileName = skin.getNumber() + skin.getStrippedName() + "_" + skin.getSkinType().getName() + ".png";
                Path outputPath = outputDir.resolve(outputFileName);
                Files.copy(skin.getFilePath(), outputPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generates the zip file
     */
    private void generateZipFile() {
        String sourcePath = Settings.OUTPUT_DIRECTORY;
        String zipName = Generator.formattedSkinPackage + ".zip";
        String zipPath = Settings.ZIP_DIRECTORY + zipName;
        try {
            zipFolder(sourcePath, zipPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Zips the folder
     *
     * @param sourcDirPath
     * @param zipPath
     * @throws IOException
     */
    public void zipFolder(String sourcDirPath, String zipPath) throws IOException {
        Path zipPaths = Paths.get(zipPath);
        File file = new File(zipPath).getParentFile();
        file.mkdirs();
        Files.deleteIfExists(zipPaths);
        Path zipFile = Files.createFile(zipPaths);

        Path sourceDirPath = Paths.get(sourcDirPath);
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile));
             Stream<Path> paths = Files.walk(sourceDirPath)) {
            paths.filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString());
                        try {
                            zipOutputStream.putNextEntry(zipEntry);
                            Files.copy(path, zipOutputStream);
                            zipOutputStream.closeEntry();
                        } catch (IOException e) {
                            System.err.println(e);
                        }
                    });
        }

        System.out.println("Zip will be created at : " + zipFile);
    }

    @Override
    public String toString() {
        return "SkinPack{" +
                "headerUUID=" + headerUUID +
                ", moduleUUID=" + moduleUUID +
                ", packName='" + packName + '\'' +
                ", marketingKeyArt=" + marketingKeyArt +
                ", thumbnail=" + thumbnail +
                ", partnerArt=" + partnerArt +
                ", skins=" + skins +
                ", packNameFormatted='" + packNameFormatted + '\'' +
                '}';
    }
}
