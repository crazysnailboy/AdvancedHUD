package advancedhud.client.ui;

import java.util.List;
import org.lwjgl.opengl.GL11;
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

            drawRect(this.huditem.posX, this.huditem.posY, this.huditem.posX + this.huditem.getWidth(), this.huditem.posY + this.huditem.getHeight(), 0x22FFFFFF);
            this.huditem.render(GuiAdvancedHUD.partialTicks);

            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            if (this.hovered) {
                GL11.glPushMatrix();
                GL11.glTranslatef(0, 0, 200F);
                this.drawCenteredString(mc.fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2 + 1, 0xFFFFFF);
                GL11.glPopMatrix();
            }

        }
    }

    public List<String> getTooltip() {
        return this.huditem.getTooltip();
    }

}
