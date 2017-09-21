package advancedhud.client.huditems;

import org.lwjgl.opengl.GL11;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import net.minecraft.client.gui.Gui;

public class HudItemJumpBar extends HudItem {

    @Override
    public String getName() {
        return "jumpbar";
    }

    @Override
    public int getDefaultID() {
        return 9;
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
    public void render(float partialTicks) {

        if (!(enabled || configMode())) return;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.mc.renderEngine.bindTexture(Gui.icons);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        float charge = this.mc.thePlayer.getHorseJumpPower();
        final int barWidth = 182;
        int x = this.posX;
        int filled = (int)(charge * (barWidth + 1)); if (this.configMode() && filled == 0) filled = 182;
        int top = this.posY;

        this.drawTexturedModalRect(x, top, 0, 84, barWidth, 5);

        if (filled > 0) {
            this.drawTexturedModalRect(x, top, 0, 89, filled, 5);
        }

        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public boolean shouldDrawOnMount() {
        return true;
    }

    @Override
    public boolean shouldDrawAsPlayer() {
        return false;
    }

}
