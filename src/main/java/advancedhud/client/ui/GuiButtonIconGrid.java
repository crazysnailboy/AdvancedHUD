package advancedhud.client.ui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import advancedhud.AdvancedHUD;
import advancedhud.api.RenderAssist;
import advancedhud.client.huditems.HudItemCrosshairs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiButtonIconGrid extends GuiButton {

    private ResourceLocation resourceLocation = new ResourceLocation(AdvancedHUD.MODID, "textures/gui/crosshairs.png");

    private final HudItemCrosshairs crosshairs;

    public GuiButtonIconGrid(int id, int xPosition, int yPosition, HudItemCrosshairs crosshairs, String buttonText) {
        super(id, xPosition, yPosition, 256, 64, buttonText);
        this.crosshairs = crosshairs;
    }

    public GuiButtonIconGrid(int id, int xPosition, int yPosition, HudItemCrosshairs crosshairs, String buttonText, ResourceLocation buttonTexture) {
        super(id, xPosition, yPosition, 256, 64, buttonText);
        this.resourceLocation = buttonTexture;
        this.crosshairs = crosshairs;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        RenderAssist.drawRect(this.x - 0.5F, this.y - 0.5F, this.x + 256.5F, this.y + 64.5F, 0x80000000);
        RenderAssist.drawUnfilledRect(this.x - 0.5F, this.y - 0.5F, this.x + 256.5F, this.y + 64.5F, 0xFFFFFFFF);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        //GL11.glScalef(0.5F, 0.5F, 1F);
        mc.getTextureManager().bindTexture(this.resourceLocation);
        RenderAssist.drawTexturedModalRect(this.x, this.y, 0, 0, 256, 64);
        if (mouseX > this.x && mouseY > this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
            float posX = ((mouseX - this.x) - (mouseX - this.x) % 16) + this.x;
            float posY = ((mouseY - this.y) - (mouseY - this.y) % 16) + this.y;
            int color = 0xFF1F95FF;
            if (Mouse.isButtonDown(0))
                color = 0xFF1059F7;
            RenderAssist.drawUnfilledRect(posX - 0.125F, posY - 0.125F, posX + 16.125F, posY + 16.125F, color);
        }
        if (this.crosshairs.getSelectedIconX() >= 0 && this.crosshairs.getSelectedIconY() >= 0)
            RenderAssist.drawUnfilledRect(this.x + this.crosshairs.getSelectedIconX() - 0.375F, this.y + this.crosshairs.getSelectedIconY() - 0.5F, this.x + this.crosshairs.getSelectedIconX() + 16, this.y + this.crosshairs.getSelectedIconY() + 16F, 0xFFFFFFFF);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (mouseX > this.x && mouseY > this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
            this.crosshairs.setSelectedIconX((mouseX - this.x) - (mouseX - this.x) % 16);
            this.crosshairs.setSelectedIconY((mouseY - this.y) - (mouseY - this.y) % 16);
        }
        //AdvancedHUD.log.info("Selected Icon: "+selectedIconX+","+selectedIconY);
        return super.mousePressed(mc, mouseX, mouseY);
    }

}
