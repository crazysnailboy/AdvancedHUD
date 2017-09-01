package advancedhud.api;

import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Extend this to create your own elements which render on the GUI.<br>Don't worry about saves, they are all done by the non-api part of the mod.
 * @author maxpowa
 *
 */
public abstract class HudItem {
    public Alignment alignment;
    public int posX;
    public int posY;
    private int id;
    private float opacity = 1.0f;
    public boolean rotated = false;
    protected TweenManager manager = null;
    protected Minecraft mc;

    public HudItem() {
        this.alignment = this.getDefaultAlignment();
        this.posX = this.getDefaultPosX();
        this.posY = this.getDefaultPosY();
        this.id = this.getDefaultID();
        this.manager = new TweenManager();
        this.mc = Minecraft.getMinecraft();
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
    public abstract String getButtonLabel();

    /**
     * Default {@link Alignment} of the HudItem instance.<br>For resolution-based movement, alignment values allow shifting along the alignment axis.
     * @return {@link Alignment}
     */
    public abstract Alignment getDefaultAlignment();

    public abstract int getDefaultPosX();

    public abstract int getDefaultPosY();

    public abstract int getWidth();

    public abstract int getHeight();

    /**
     * Button ID for configuration screen, 0-25 are reserved for Vanilla use.
     */
    public abstract int getDefaultID();

    /**
     * Define custom GuiScreen instances for your own configuration screen.
     */
    public abstract GuiScreen getConfigScreen();

    public abstract void render(float partialTicks);

    /**
     * If you don't want any rotation, you can simply make this method return.
     */
    public void rotate() {
        this.rotated = !this.rotated;
    }

    /**
     * Called upon .updateTick(). If you use this, make sure you set {@link HudItem#needsTick()} to true.
     */
    public void tick() {
    }

    /**
     * @param delta Delta time (in seconds) since this method was last called
     */
    public void update(float delta) {
        this.manager.update(delta); // Tween manager expects delta to be in seconds.
    }

    public boolean needsTween() {
        return false;
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

    public boolean isEnabledByDefault() {
        return true;
    }

    public boolean isRenderedInCreative() {
        return true;
    }

    /**
     * Ensures that the HudItem will never be off the screen
     */
    public void fixBounds() {
        this.posX = Math.max(0, Math.min(HUDRegistry.screenWidth - this.getWidth(), this.posX));
        this.posY = Math.max(0, Math.min(HUDRegistry.screenHeight - this.getHeight(), this.posY));
    }

    public void loadFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("posX")) {
            this.posX = compound.getInteger("posX");
        } else {
            this.posX = this.getDefaultPosX();
        }
        if (compound.hasKey("posY")) {
            this.posY = compound.getInteger("posY");
        } else {
            this.posY = this.getDefaultPosY();
        }
        if (compound.hasKey("alignment")) {
            this.alignment = Alignment.fromString(compound.getString("alignment"));
        } else {
            this.alignment = this.getDefaultAlignment();
        }
        if (compound.hasKey("id")) {
            this.id = compound.getInteger("id");
        } else {
            this.id = this.getDefaultID();
        }
        if (compound.hasKey("rotated")) {
            this.rotated = compound.getBoolean("rotated");
        } else {
            this.rotated = false;
        }
    }

    public void saveToNBT(NBTTagCompound compound) {
        compound.setInteger("posX", this.posX);
        compound.setInteger("posY", this.posY);
        compound.setString("alignment", this.alignment.toString());
        compound.setInteger("id", this.id);
        compound.setBoolean("rotated", this.rotated);
    }

    public boolean shouldDrawOnMount() {
        return false;
    }

    public boolean shouldDrawAsPlayer() {
        return true;
    }

    public boolean canRotate() {
        return true;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public float getOpacity() {
        return this.opacity;
    }

    public interface TweenEngine<T extends HudItem> extends TweenAccessor<T> {
    }
}