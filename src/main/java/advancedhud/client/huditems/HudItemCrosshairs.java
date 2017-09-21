package advancedhud.client.huditems;

import org.lwjgl.opengl.GL11;
import advancedhud.AdvancedHUD;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class HudItemCrosshairs extends HudItem {

    private static final ResourceLocation crosshairIcons = new ResourceLocation(AdvancedHUD.MODID, "textures/gui/crosshairs.png");
    private int selectedIconX = -1;
    private int selectedIconY = -1;

    @Override
    public String getName() {
        return "crosshair";
    }

    @Override
    public int getDefaultID() {
        return 11;
    }

    @Override
    public Alignment getDefaultAlignment() {
        return Alignment.CENTERCENTER;
    }

    @Override
    public int getDefaultPosX() {
        return HUDRegistry.screenWidth / 2 - 8;
    }

    @Override
    public int getDefaultPosY() {
        return HUDRegistry.screenHeight / 2 - 8;
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public void render(float partialTicks) {

        if (!(enabled || configMode())) return;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR, 1, 0);
        GlStateManager.enableAlpha();

        if (this.selectedIconX >= 0 && this.selectedIconY >= 0) {
            this.mc.renderEngine.bindTexture(crosshairIcons);
            this.drawTexturedModalRect(this.posX, this.posY, this.selectedIconX, this.selectedIconY, 16, 16);
        } else {
            this.mc.renderEngine.bindTexture(Gui.icons);
            this.drawTexturedModalRect(this.posX, this.posY, 0, 0, 16, 16);
        }

        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.disableBlend();
    }

    public int getSelectedIconX() {
        return this.selectedIconX;
    }

    public int getSelectedIconY() {
        return this.selectedIconY;
    }

    public void setSelectedIconX(int selectedIconX) {
        this.selectedIconX = selectedIconX;
    }

    public void setSelectedIconY(int selectedIconY) {
        this.selectedIconY = selectedIconY;
    }

    @Override
    public void loadFromNBT(NBTTagCompound compound) {
        super.loadFromNBT(compound);
        this.selectedIconX = (compound.hasKey("selectedIconX") ? compound.getInteger("selectedIconX") : -1);
        this.selectedIconY = (compound.hasKey("selectedIconY") ? compound.getInteger("selectedIconY") : -1);
    }

    @Override
    public void saveToNBT(NBTTagCompound compound) {
        compound.setInteger("selectedIconX", this.selectedIconX);
        compound.setInteger("selectedIconY", this.selectedIconY);
        super.saveToNBT(compound);
    }

    @Override
    public boolean isMoveable() {
        return false;
    }

    @Override
    public boolean canRotate() {
        return false;
    }

    @Override
    public boolean shouldDrawOnMount() {
        return true;
    }

}
