package advancedhud.client.ui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import advancedhud.AdvancedHUD;
import advancedhud.api.RenderAssist;
import advancedhud.api.RenderStyle;
import advancedhud.client.huditems.HudItemCrosshairs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonIconGrid extends GuiButton {

    private static final ResourceLocation CROSSHAIR_ICONS = new ResourceLocation(AdvancedHUD.MODID, "textures/gui/crosshairs.png");

    private final HudItemCrosshairs crosshairs;

    public GuiButtonIconGrid(int id, int xPosition, int yPosition, HudItemCrosshairs crosshairs) {
        super(id, xPosition, yPosition, 256, 64, "");
        this.crosshairs = crosshairs;
        this.visible = (this.crosshairs.style == RenderStyle.ICON);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            RenderAssist.drawRect(this.x - 0.5F, this.y - 0.5F, this.x + 256.5F, this.y + 64.5F, 0x80000000);
            RenderAssist.drawUnfilledRect(this.x - 0.5F, this.y - 0.5F, this.x + 256.5F, this.y + 64.5F, 0xFFFFFFFF);
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            //GlStateManager.scale(0.5F, 0.5F, 1.0F);
            mc.getTextureManager().bindTexture(this.CROSSHAIR_ICONS);
            RenderAssist.drawTexturedModalRect(this.x, this.y, 0, 0, 256, 64);
            if (mouseX > this.x && mouseY > this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
                float posX = ((mouseX - this.x) - (mouseX - this.x) % 16) + this.x;
                float posY = ((mouseY - this.y) - (mouseY - this.y) % 16) + this.y;
                int color = (Mouse.isButtonDown(0) ? 0xFF1059F7 : 0xFF1F95FF);
                RenderAssist.drawUnfilledRect(posX - 0.125F, posY - 0.125F, posX + 16.125F, posY + 16.125F, color);
            }
            if (this.crosshairs.getSelectedIconX() >= 0 && this.crosshairs.getSelectedIconY() >= 0) {
                RenderAssist.drawUnfilledRect(this.x + this.crosshairs.getSelectedIconX() - 0.375F, this.y + this.crosshairs.getSelectedIconY() - 0.5F, this.x + this.crosshairs.getSelectedIconX() + 16, this.y + this.crosshairs.getSelectedIconY() + 16F, 0xFFFFFFFF);
            }
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (mouseX > this.x && mouseY > this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
            this.crosshairs.setSelectedIconX((mouseX - this.x) - (mouseX - this.x) % 16);
            this.crosshairs.setSelectedIconY((mouseY - this.y) - (mouseY - this.y) % 16);
        }
        return super.mousePressed(mc, mouseX, mouseY);
    }

}
