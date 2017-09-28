package advancedhud.api;

public enum RenderStyle {

    GLYPH("glyph"),
    SOLID("solidbar");

    private final String unlocalizedName;

    private RenderStyle(String unlocalizedName) {
        this.unlocalizedName = unlocalizedName;
    }

    public String getUnlocalizedName() {
        return "advancedhud.huditemstyle." + this.unlocalizedName;
    }

    public RenderStyle getNext() {
        return values()[ordinal() == values().length - 1 ? 0 : ordinal() + 1];
    }

}
