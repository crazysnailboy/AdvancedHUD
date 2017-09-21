package advancedhud.client.ui;

import java.util.List;
import advancedhud.api.HudItem;
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
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {

            drawRect(huditem.posX, huditem.posY, huditem.posX + huditem.getWidth(), huditem.posY + huditem.getHeight(), 0x22FFFFFF);
            huditem.render(partialTicks);

            boolean hoverState = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            if (hoverState) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0, 0, 200F);
                this.drawCenteredString(mc.fontRenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2 + 1, 0xFFFFFF);
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
