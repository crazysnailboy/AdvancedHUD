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

    private GuiHudItemButton(HudItem huditem, int id, int x, int y, int width, int height, String buttonText) {
        super(id, x, y, width, height, buttonText);
        this.huditem = huditem;
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.displayString = buttonText;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {

            drawRect(huditem.posX, huditem.posY, huditem.posX + huditem.getWidth(), huditem.posY + huditem.getHeight(), 0x22FFFFFF);
            huditem.render(GuiAdvancedHUD.partialTicks);

            boolean hoverState = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            if (hoverState) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0, 0, 200F);
                this.drawCenteredString(mc.fontRenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2 + 1, 0xFFFFFF);
                GlStateManager.popMatrix();
            }

        }
    }

}
