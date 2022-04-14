package generator.content_types;

public enum SkinType {
    CUSTOM_SLIM("customSlim"), CUSTOM("custom");

    private final String name;

    SkinType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
