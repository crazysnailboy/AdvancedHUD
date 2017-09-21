package advancedhud.client.huditems;

import org.lwjgl.opengl.GL11;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import net.minecraft.client.gui.Gui;

public class HudItemExperienceBar extends HudItem {

    @Override
    public String getName() {
        return "experiencebar";
    }

    @Override
    public int getDefaultID() {
        return 8;
    }

    @Override
    public Alignment getDefaultAlignment() {
        return (!this.rotated ? Alignment.BOTTOMCENTER : Alignment.CENTERRIGHT);
    }

    @Override
    public int getDefaultPosX() {
        return (!this.rotated ? HUDRegistry.screenWidth / 2 - 91 : HUDRegistry.screenWidth - 29);
    }

    @Override
    public int getDefaultPosY() {
        return (!this.rotated ? HUDRegistry.screenHeight - 29 : HUDRegistry.screenHeight / 2 - 91);
    }

    @Override
    public int getWidth() {
        return (!this.rotated ? 182 : 5);
    }

    @Override
    public int getHeight() {
        return (!this.rotated ? 5 : 182);
    }

    @Override
    public void render(float partialTicks) {

        if (!(enabled || configMode())) return;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        this.mc.renderEngine.bindTexture(Gui.icons);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (this.mc.playerController.gameIsSurvivalOrAdventure() || this.configMode()){

            int left = this.posX;
            int top = this.posY;

            int cap = this.mc.thePlayer.xpBarCap();
            if (cap > 0) {
                short barWidth = 182;
                int filled = (int)(this.mc.thePlayer.experience * (float)(barWidth + 1)); if (this.configMode() && filled == 0) filled = 91;

                GL11.glPushMatrix();
                if (this.rotated) {
                    GL11.glTranslatef(left + 5, top, 0.0F);
                    GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
                } else {
                    GL11.glTranslatef(left, top, 0.0F);
                }
                this.drawTexturedModalRect(0, 0, 0, 64, barWidth, 5);
                if (filled > 0) {
                    this.drawTexturedModalRect(0, 0, 0, 69, filled, 5);
                }
                GL11.glPopMatrix();
            }

            if (this.mc.thePlayer.experienceLevel > 0 || configMode()) {
                int color = 0x80FF20;
                String text = "" + this.mc.thePlayer.experienceLevel;
                int x = left + ((this.getWidth() / 2) - (this.mc.fontRendererObj.getStringWidth(text) / 2));
                int y = top + (!this.rotated ? -4 : (this.getHeight() / 2) - (this.mc.fontRendererObj.FONT_HEIGHT / 2));
                this.mc.fontRendererObj.drawString(text, x + 1, y, 0x000000);
                this.mc.fontRendererObj.drawString(text, x - 1, y, 0x000000);
                this.mc.fontRendererObj.drawString(text, x, y + 1, 0x000000);
                this.mc.fontRendererObj.drawString(text, x, y - 1, 0x000000);
                this.mc.fontRendererObj.drawString(text, x, y, color);
            }
        }

        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public boolean isRenderedInCreative() {
        return false;
    }

}
