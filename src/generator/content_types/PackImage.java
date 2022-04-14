package generator.content_types;

import java.nio.file.Path;

public class PackImage {
    private Path path;

    public PackImage(String path) {
        this.path = Path.of(path);
    }

    public Path getPath() {
        return path;
    }
}
