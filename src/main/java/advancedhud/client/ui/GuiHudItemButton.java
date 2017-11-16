package advancedhud.client.ui;

import java.util.List;
import advancedhud.api.HudItem;
import advancedhud.client.GuiAdvancedHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiHudItemButton extends GuiButton {

    private final HudItem huditem;

    public GuiHudItemButton(HudItem huditem) {
        super(huditem.getDefaultID(), huditem.posX, huditem.posY, huditem.getWidth(), huditem.getHeight(), huditem.getDisplayName());
        this.huditem = huditem;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            this.drawRect(huditem.posX, huditem.posY, huditem.posX + huditem.getWidth(), huditem.posY + huditem.getHeight(), 0x22FFFFFF);
            this.huditem.render(GuiAdvancedHUD.partialTicks);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
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
