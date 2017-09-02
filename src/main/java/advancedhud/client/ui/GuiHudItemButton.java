package advancedhud.client.ui;

import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.client.GuiAdvancedHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GuiHudItemButton extends GuiButton {

    public GuiHudItemButton(int id, int xPosition, int yPosition, int width, int height, String buttonText) {
        super(id, xPosition, yPosition, width, height, buttonText);
        this.id = id;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
        this.displayString = buttonText;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            GlStateManager.pushMatrix(); // GL11.glPushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F); // GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean hoverState = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            for (HudItem huditem : HUDRegistry.getHudItemList()) {
                if (huditem.getButtonLabel().equalsIgnoreCase(this.displayString)) {
                    drawRect(huditem.posX, huditem.posY, huditem.posX + huditem.getWidth(), huditem.posY + huditem.getHeight(), 0x22FFFFFF);
                    huditem.render(GuiAdvancedHUD.partialTicks);
                }
            }
            GlStateManager.popMatrix(); // GL11.glPopAttrib();

            if (hoverState) {
                GlStateManager.pushMatrix(); // GL11.glPushMatrix();
                GlStateManager.translate(0, 0, 200F); // GL11.glTranslatef(0, 0, 200F);
                this.drawCenteredString(mc.fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2 + 1, 0xFFFFFF);
                GlStateManager.popMatrix(); // GL11.glPopAttrib();
            }

        }
    }
}
