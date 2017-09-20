package advancedhud.client.ui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import advancedhud.AdvancedHUD;
import advancedhud.api.RenderAssist;
import advancedhud.client.huditems.HudItemCrosshairs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonIconGrid extends GuiButton {

    private static final ResourceLocation CROSSHAIR_ICONS = new ResourceLocation(AdvancedHUD.MODID, "textures/gui/crosshairs.png");

    private final HudItemCrosshairs crosshairs;

    public GuiButtonIconGrid(int id, int xPosition, int yPosition, HudItemCrosshairs crosshairs, String buttonText) {
        super(id, xPosition, yPosition, 256, 64, buttonText);
        this.crosshairs = crosshairs;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        RenderAssist.drawRect(this.xPosition - 0.5F, this.yPosition - 0.5F, this.xPosition + 256.5F, this.yPosition + 64.5F, 0x80000000);
        RenderAssist.drawUnfilledRect(this.xPosition - 0.5F, this.yPosition - 0.5F, this.xPosition + 256.5F, this.yPosition + 64.5F, 0xFFFFFFFF);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        //GlStateManager.scale(0.5F, 0.5F, 1.0F);
        mc.getTextureManager().bindTexture(this.CROSSHAIR_ICONS);
        RenderAssist.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 0, 256, 64);
        if (mouseX > this.xPosition && mouseY > this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height) {
            float posX = ((mouseX - this.xPosition) - (mouseX - this.xPosition) % 16) + this.xPosition;
            float posY = ((mouseY - this.yPosition) - (mouseY - this.yPosition) % 16) + this.yPosition;
            int color = (Mouse.isButtonDown(0) ? 0xFF1059F7 : 0xFF1F95FF);
            RenderAssist.drawUnfilledRect(posX - 0.125F, posY - 0.125F, posX + 16.125F, posY + 16.125F, color);
        }
        if (this.crosshairs.getSelectedIconX() >= 0 && this.crosshairs.getSelectedIconY() >= 0) {
            RenderAssist.drawUnfilledRect(this.xPosition + this.crosshairs.getSelectedIconX() - 0.375F, this.yPosition + this.crosshairs.getSelectedIconY() - 0.5F, this.xPosition + this.crosshairs.getSelectedIconX() + 16, this.yPosition + this.crosshairs.getSelectedIconY() + 16F, 0xFFFFFFFF);
        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (mouseX > this.xPosition && mouseY > this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height) {
            this.crosshairs.setSelectedIconX((mouseX - this.xPosition) - (mouseX - this.xPosition) % 16);
            this.crosshairs.setSelectedIconY((mouseY - this.yPosition) - (mouseY - this.yPosition) % 16);
        }
        return super.mousePressed(mc, mouseX, mouseY);
    }

}
