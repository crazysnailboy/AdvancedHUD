package advancedhud.api;

public enum RenderStyle {

    DEFAULT("default"),
    SOLID("solidbar"),
    ICON("custom");

    private final String unlocalizedName;

    private RenderStyle(String unlocalizedName) {
        this.unlocalizedName = unlocalizedName;
    }

    public String getUnlocalizedName() {
        return "advancedhud.huditemstyle." + this.unlocalizedName;
    }

    public static RenderStyle fromInteger(int value) {
        return (value >= 0 && value < values().length ? values()[value] : values()[0]);
    }

}