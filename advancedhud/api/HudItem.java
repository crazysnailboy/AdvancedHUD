package advancedhud.api;

import net.minecraft.nbt.NBTTagCompound;

public abstract class HudItem {
    public Alignment alignment;
    public int posX;
    public int posY;
    private int id;

    /**
     * Unique name for the HudItem, only used for NBT saving/loading
     * 
     * @return      String value for unique identifier of the {@link HudItem}
     */
    public abstract String getName();
    
    /**
     * Display name for the HudItem in config screen
     * 
     * @return      String value for display name of the {@link HudItem}
     */
    public abstract String getButtonLabel();

    /**
     * Default {@link Alignment} of the HudItem instance. 
     * <p>For resolution-based movement, alignment values allow shifting along the alignment axis.
     * 
     * @return      {@link Alignment}
     */
    public abstract Alignment getDefaultAlignment();

    public abstract int getDefaultPosX();

    public abstract int getDefaultPosY();

    public abstract int getWidth();

    public abstract int getHeight();
    
    public abstract int getDefaultID();

    public HudItem() {
        alignment = getDefaultAlignment();
        posX = getDefaultPosX();
        posY = getDefaultPosY();     
        id = getDefaultID();
    }

    public abstract void render(float paramFloat);

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
     * 
     * Ensures that the HudItem will never be off the screen
     * 
     */
    public void fixBounds() {
        posX = Math.max(0, Math.min(HUDRegistry.screenWidth - getWidth(), posX));
        posY = Math.max(0, Math.min(HUDRegistry.screenHeight - getHeight(), posY));
    }

    public void loadFromNBT(NBTTagCompound nbt) {
        posX = nbt.getInteger("posX");
        posY = nbt.getInteger("posY");
        alignment = Alignment.fromString(nbt.getString("alignment"));
        id = nbt.getInteger("id");
    }

    public void saveToNBT(NBTTagCompound nbt) {
        nbt.setInteger("posX", posX);
        nbt.setInteger("posY", posY);
        nbt.setString("alignment", alignment.toString());
        nbt.setInteger("id", id);
    }
}