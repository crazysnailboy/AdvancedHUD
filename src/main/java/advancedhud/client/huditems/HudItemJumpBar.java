package advancedhud.client.huditems;

import org.lwjgl.opengl.GL11;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.api.RenderAssist;
import advancedhud.client.ui.GuiAdvancedHUDConfiguration;
import advancedhud.client.ui.GuiScreenHudItem;
import advancedhud.client.ui.GuiScreenReposition;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class HudItemJumpBar extends HudItem {

    @Override
    public String getName() {
        return "jumpbar";
    }

    @Override
    public String getButtonLabel() {
        return I18n.format("advancedhud.item.jumpbar.name");
    }

    @Override
    public Alignment getDefaultAlignment() {
        return Alignment.BOTTOMCENTER;
    }

    @Override
    public int getDefaultPosX() {
        return HUDRegistry.screenWidth / 2 - 91;
    }

    @Override
    public int getDefaultPosY() {
        return HUDRegistry.screenHeight - 29;
    }

    @Override
    public int getWidth() {
        return 182;
    }

    @Override
    public int getHeight() {
        return 4;
    }

    @Override
    public int getDefaultID() {
        return 9;
    }

    @Override
    public void render(float partialTicks) {
        RenderAssist.bindTexture(Gui.icons);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        float charge = this.mc.thePlayer.getHorseJumpPower();
        final int barWidth = 182;
        int x = this.posX;
        int filled = (int) (charge * (barWidth + 1));
        int top = this.posY;

        RenderAssist.drawTexturedModalRect(x, top, 0, 84, barWidth, 5);

        if ((this.mc.currentScreen instanceof GuiAdvancedHUDConfiguration || this.mc.currentScreen instanceof GuiScreenReposition) && filled == 0) {
            filled = 182;
        }

        if (filled > 0) {
            RenderAssist.drawTexturedModalRect(x, top, 0, 89, filled, 5);
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean shouldDrawOnMount() {
        return true;
    }

    @Override
    public boolean shouldDrawAsPlayer() {
        return false;
    }

    @Override
    public GuiScreen getConfigScreen() {
        return new GuiScreenHudItem(this.mc.currentScreen, this);
    }

}
