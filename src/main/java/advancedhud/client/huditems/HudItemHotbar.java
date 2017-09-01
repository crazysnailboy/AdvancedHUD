package advancedhud.client.huditems;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import advancedhud.AdvancedHUD;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.api.RenderAssist;
import advancedhud.client.ui.GuiScreenHudItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class HudItemHotbar extends HudItem {

    private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation ROTATE_WIDGETS = new ResourceLocation(AdvancedHUD.MODID, "textures/gui/rotateWidgets.png");

    @Override
    public String getName() {
        return "hotbar";
    }

    @Override
    public String getButtonLabel() {
        return I18n.format("advancedhud.item.hotbar.name");
    }

    @Override
    public Alignment getDefaultAlignment() {
        if (this.rotated)
            return Alignment.CENTERRIGHT;
        return Alignment.BOTTOMCENTER;
    }

    @Override
    public int getDefaultPosX() {
        if (this.rotated)
            return HUDRegistry.screenWidth - this.getWidth();
        return (HUDRegistry.screenWidth - this.getWidth()) / 2;
    }

    @Override
    public int getDefaultPosY() {
        if (this.rotated)
            return (HUDRegistry.screenHeight - this.getHeight()) / 2;
        return HUDRegistry.screenHeight - this.getHeight();
    }

    @Override
    public int getWidth() {
        if (this.rotated)
            return 22;
        return 182;
    }

    @Override
    public int getHeight() {
        if (this.rotated)
            return 182;
        return 22;
    }

    @Override
    public void render(float partialTicks) {
        Minecraft mc = HUDRegistry.getMinecraftInstance();

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        InventoryPlayer inv = mc.thePlayer.inventory;
        if (!this.rotated) {
            RenderAssist.bindTexture(WIDGETS);
            RenderAssist.drawTexturedModalRect(this.posX, this.posY, 0, 0, 182, 22);
            RenderAssist.drawTexturedModalRect(this.posX - 1 + inv.currentItem * 20, this.posY - 1, 0, 22, 24, 22);

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            GL11.glPopAttrib();

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderHelper.enableGUIStandardItemLighting();

            for (int i = 0; i < 9; ++i) {
                int x = this.posX - 90 + i * 20 + 2;
                int z = this.posY - 6 - 3;
                RenderAssist.renderInventorySlot(i, x, z, partialTicks, mc);
            }

            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        } else {
            GL11.glTranslatef((float) this.posX + this.getWidth(), this.posY, 0.0F);
            GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
            RenderAssist.bindTexture(WIDGETS);
            RenderAssist.drawTexturedModalRect(0, 0, 0, 0, 182, 22);
            RenderAssist.bindTexture(ROTATE_WIDGETS);
            RenderAssist.drawTexturedModalRect(inv.currentItem * 20 - 1, -1, 0, 0, 24, 24);

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            GL11.glPopAttrib();

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderHelper.enableGUIStandardItemLighting();

            for (int i = 0; i < 9; ++i) {
                int x = this.posX - 88;
                int z = this.posY - 11 + i * 20 + 2;
                RenderAssist.renderInventorySlot(i, x, z, partialTicks, mc);
            }

            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }
    }

    @Override
    public int getDefaultID() {
        return 1;
    }

    @Override
    public boolean shouldDrawOnMount() {
        return true;
    }

    @Override
    public boolean shouldDrawAsPlayer() {
        return true;
    }

    @Override
    public GuiScreen getConfigScreen() {
        return new GuiScreenHudItem(Minecraft.getMinecraft().currentScreen, this);
    }

    @Override
    public void loadFromNBT(NBTTagCompound compound) {
        super.loadFromNBT(compound);
    }

    @Override
    public void saveToNBT(NBTTagCompound compound) {
        super.saveToNBT(compound);
    }

    @Override
    public void rotate() {
        super.rotate();
        // posX = getDefaultPosX();
        // posY = getDefaultPosY();
    }

}
