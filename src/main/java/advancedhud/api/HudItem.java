package advancedhud.api;

import java.util.ArrayList;
import java.util.List;
import advancedhud.client.ui.GuiAdvancedHUDConfiguration;
import advancedhud.client.ui.GuiScreenHudItem;
import advancedhud.client.ui.GuiScreenReposition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

/**
 * Extend this to create your own elements which render on the GUI.<br>Don't worry about saves, they are all done by the non-api part of the mod.
 * @author maxpowa
 *
 */
public abstract class HudItem {

    protected static final Minecraft mc = Minecraft.getMinecraft();

    public Alignment alignment;
    public int posX;
    public int posY;
    private int id;
    public boolean rotated = false;
    public boolean enabled = true;
    public RenderStyle style = RenderStyle.GLYPH;


    public HudItem() {
        this.alignment = this.getDefaultAlignment();
        this.posX = this.getDefaultPosX();
        this.posY = this.getDefaultPosY();
        this.id = this.getDefaultID();
    }


    /**
     * Unique name for the HudItem, only used for NBT saving/loading
     * @return String value for unique identifier of the {@link HudItem}
     */
    public abstract String getName();

    /**
     * Display name for the HudItem in config screen
     * @return String value for display name of the {@link HudItem}
     */
    public String getDisplayName() {
        return I18n.format(String.format("advancedhud.item.%s.name", this.getName()));
    }

    /**
     * Button ID for configuration screen, 0-25 are reserved for Vanilla use.
     */
    public abstract int getDefaultID();

    /**
     * Default {@link Alignment} of the HudItem instance.<br>For resolution-based movement, alignment values allow shifting along the alignment axis.
     * @return {@link Alignment}
     */
    public abstract Alignment getDefaultAlignment();

    public abstract int getDefaultPosX();

    public abstract int getDefaultPosY();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract void render(float partialTicks);

    /**
     * This override is currently only used by HudItemBossbar.
     */
    public void render(float partialTicks, Gui gui) {
        this.render(partialTicks);
    }

    /**
     * Define custom GuiScreen instances for your own configuration screen.
     */
    public GuiScreen getConfigScreen() {
        return new GuiScreenHudItem(this.mc.currentScreen, this);
    }

    /**
     * Called upon .updateTick(). If you use this, make sure you set {@link HudItem#needsTick()} to true.
     */
    public void tick() {
    }

    /**
     * Set this to true if you require the {@link HudItem#tick()} method to run
     */
    public boolean needsTick() {
        return false;
    }

    public boolean isMoveable() {
        return true;
    }

    public boolean canRotate() {
        return true;
    }

    public boolean canChangeStyle() {
        return false;
    }

    public boolean isEnabledByDefault() {
        return true;
    }

    public boolean isRenderedInCreative() {
        return true;
    }

    public boolean shouldDrawOnMount() {
        return false;
    }

    public boolean shouldDrawAsPlayer() {
        return true;
    }

    /**
     * Ensures that the HudItem will never be off the screen
     */
    public void fixBounds(ScaledResolution res) {
        this.posX = Math.max(0, Math.min(res.getScaledWidth() - this.getWidth(), this.posX));
        this.posY = Math.max(0, Math.min(res.getScaledHeight() - this.getHeight(), this.posY));
    }

    public void fixBounds() {
        this.posX = Math.max(0, Math.min(HUDRegistry.screenWidth - this.getWidth(), this.posX));
        this.posY = Math.max(0, Math.min(HUDRegistry.screenHeight - this.getHeight(), this.posY));
    }

    public void loadFromNBT(NBTTagCompound compound) {
        this.posX = (compound.hasKey("posX") ? compound.getInteger("posX") : this.getDefaultPosX());
        this.posY = (compound.hasKey("posY") ? compound.getInteger("posY") : this.getDefaultPosY());
        this.alignment = (compound.hasKey("alignment") ? Alignment.fromString(compound.getString("alignment")) : this.getDefaultAlignment());
        this.id = (compound.hasKey("id") ? compound.getInteger("id") : this.getDefaultID());
        this.rotated = (compound.hasKey("rotated") ? compound.getBoolean("rotated") : false);
        this.enabled = (compound.hasKey("enabled") ? compound.getBoolean("enabled") : true);
        this.style = (compound.hasKey("style") ? RenderStyle.values()[compound.getInteger("style")] : RenderStyle.GLYPH);
    }

    public void saveToNBT(NBTTagCompound compound) {
        compound.setInteger("posX", this.posX);
        compound.setInteger("posY", this.posY);
        compound.setString("alignment", this.alignment.toString());
        compound.setInteger("id", this.id);
        compound.setBoolean("rotated", this.rotated);
        compound.setBoolean("enabled", this.enabled);
        compound.setInteger("style", this.style.ordinal());
    }

    public boolean configMode() {
        return (mc.currentScreen instanceof GuiAdvancedHUDConfiguration || mc.currentScreen instanceof GuiScreenReposition || mc.currentScreen instanceof GuiScreenHudItem);
    }

    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        RenderAssist.drawTexturedModalRect(x, y, textureX, textureY, width, height);
    }

    public void drawRect(int left, int top, int right, int bottom, int color) {
        RenderAssist.drawRect(left, top, right, bottom, color);
    }

    public List<String> getTooltip() {
        List<String> tooltip = new ArrayList<String>();
        tooltip.add(TextFormatting.YELLOW + this.getDisplayName());
        tooltip.add(TextFormatting.GRAY + I18n.format("advancedhud.huditems.rotation", TextFormatting.RESET + I18n.format(!this.rotated ? "gui.horizontal" : "gui.vertical")));
        tooltip.add(TextFormatting.GRAY + I18n.format("advancedhud.huditems.enabled", TextFormatting.RESET + I18n.format(this.enabled ? "gui.yes" : "gui.no")));
        return tooltip;
    }

}