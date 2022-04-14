package generator.content_types;

import java.nio.file.Path;
import java.text.Normalizer;

public class Skin {
    private final String name;
    private final String strippedName;
    private final SkinType skinType;
    private final int number;
    private final Path filePath;
    private final boolean free;

    public Skin(String name, int number, SkinType skinType, Path filePath, boolean free) {
        this.name = name;
        this.number = number;
        String tempName = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        this.strippedName = tempName.replaceAll(" ", "").replaceAll("-", "").replaceAll("[^a-zA-Z0-9]","");
        this.skinType = skinType;
        this.filePath = filePath;
        this.free = free;
    }

    public String getName() {
        return name;
    }

    public String getStrippedName() {
        return strippedName;
    }

    public SkinType getSkinType() {
        return skinType;
    }

    public int getNumber() {
        return number;
    }

    public Path getFilePath() {
        return filePath;
    }

    public boolean isFree() {
        return free;
    }

    @Override
    public String toString() {
        return "Skin{" +
                "name='" + name + '\'' +
                ", strippedName='" + strippedName + '\'' +
                ", skinType=" + skinType +
                ", number=" + number +
                ", filePath=" + filePath +
                '}';
    }
}
