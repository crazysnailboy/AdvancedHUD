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
            this.drawRect(this.huditem.posX, this.huditem.posY, this.huditem.posX + this.huditem.getWidth(), this.huditem.posY + this.huditem.getHeight(), 0x22FFFFFF);
            this.huditem.render(GuiAdvancedHUD.partialTicks);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        }
    }

    public List<String> getTooltip() {
        return this.huditem.getTooltip();
    }

}
