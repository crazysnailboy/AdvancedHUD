package advancedhud.client.huditems;

import org.lwjgl.opengl.GL11;
import advancedhud.AdvancedHUD;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.api.RenderAssist;
import advancedhud.client.ui.GuiScreenHudItem;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class HudItemCrosshairs extends HudItem {

    private static final ResourceLocation CROSSHAIR_ICONS = new ResourceLocation(AdvancedHUD.MODID, "textures/gui/crosshairs.png");

    private int selectedIconX = -1;
    private int selectedIconY = -1;

    @Override
    public String getName() {
        return "crosshair";
    }

    @Override
    public String getButtonLabel() {
        return I18n.format("advancedhud.item.crosshair.name");
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
    public int getDefaultID() {
        return 11;
    }

    @Override
    public void render(float partialTicks) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        OpenGlHelper.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR, 1, 0);
        if (this.selectedIconX >= 0 && this.selectedIconY >= 0) {
            RenderAssist.bindTexture(HudItemCrosshairs.CROSSHAIR_ICONS);
            RenderAssist.drawTexturedModalRect(this.posX, this.posY, this.selectedIconX, this.selectedIconY, 16, 16);
        } else {
            RenderAssist.bindTexture(Gui.icons);
            RenderAssist.drawTexturedModalRect(this.posX, this.posY, 0, 0, 16, 16);
        }
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
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
        if (compound.hasKey("selectedIconX")) {
            this.selectedIconX = compound.getInteger("selectedIconX");
        } else {
            this.selectedIconX = -1;
        }
        if (compound.hasKey("selectedIconY")) {
            this.selectedIconY = compound.getInteger("selectedIconY");
        } else {
            this.selectedIconX = -1;
        }
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
    public boolean shouldDrawOnMount() {
        return true;
    }

    @Override
    public GuiScreen getConfigScreen() {
        return new GuiScreenHudItem(this.mc.currentScreen, this);
    }

    @Override
    public boolean canRotate() {
        return false;
    }
}
