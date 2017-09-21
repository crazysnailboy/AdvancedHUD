package advancedhud.client.ui;

import java.util.List;
import advancedhud.api.HudItem;
import advancedhud.client.GuiAdvancedHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GuiHudItemButton extends GuiButton {

    private final HudItem huditem;

    public GuiHudItemButton(HudItem huditem) {
        super(huditem.getDefaultID(), huditem.posX, huditem.posY, huditem.getWidth(), huditem.getHeight(), huditem.getDisplayName());
        this.huditem = huditem;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {

            drawRect(huditem.posX, huditem.posY, huditem.posX + huditem.getWidth(), huditem.posY + huditem.getHeight(), 0x22FFFFFF);
            huditem.render(GuiAdvancedHUD.partialTicks);

            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            if (this.hovered) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0, 0, 200F);
                this.drawCenteredString(mc.fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2 + 1, 0xFFFFFF);
                GlStateManager.popMatrix();
            }

        }
    }

    public List<String> getTooltip() {
        return this.huditem.getTooltip();
    }

    @Override
    public int getHoverState(boolean mouseOver) {
        return super.getHoverState(mouseOver);
    }

}
