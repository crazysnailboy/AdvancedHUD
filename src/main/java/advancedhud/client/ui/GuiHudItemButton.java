package advancedhud.client.ui;

import advancedhud.api.HudItem;
import advancedhud.client.GuiAdvancedHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GuiHudItemButton extends GuiButton {

    private final HudItem huditem;

    public GuiHudItemButton(HudItem huditem) {
        this(huditem, huditem.getDefaultID(), huditem.posX, huditem.posY, huditem.getWidth(), huditem.getHeight(), huditem.getButtonLabel());
    }

    private GuiHudItemButton(HudItem huditem, int id, int xPosition, int yPosition, int width, int height, String buttonText) {
        super(id, xPosition, yPosition, width, height, buttonText);
        this.huditem = huditem;
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

            drawRect(huditem.posX, huditem.posY, huditem.posX + huditem.getWidth(), huditem.posY + huditem.getHeight(), 0x22FFFFFF);
            huditem.render(GuiAdvancedHUD.partialTicks);

            boolean hoverState = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            if (hoverState) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0, 0, 200F);
                this.drawCenteredString(mc.fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2 + 1, 0xFFFFFF);
                GlStateManager.popMatrix();
            }

        }
    }

}
