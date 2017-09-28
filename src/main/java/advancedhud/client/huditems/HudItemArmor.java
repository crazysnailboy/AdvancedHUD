package advancedhud.client.huditems;

import org.lwjgl.opengl.GL11;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.api.RenderAssist;
import advancedhud.api.RenderStyle;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.common.ForgeHooks;

public class HudItemArmor extends HudItem {

    @Override
    public String getName() {
        return "armor";
    }

    @Override
    public int getDefaultID() {
        return 5;
    }

    @Override
    public Alignment getDefaultAlignment() {
        return (!this.rotated ? Alignment.BOTTOMCENTER : Alignment.CENTERRIGHT);
    }

    @Override
    public int getDefaultPosX() {
        return (!this.rotated ? HUDRegistry.screenWidth / 2 - 91 : HUDRegistry.screenWidth - 49);
    }

    @Override
    public int getDefaultPosY() {
        return (!this.rotated ? HUDRegistry.screenHeight - 49 : HUDRegistry.screenHeight / 2 - 91);
    }

    @Override
    public int getWidth() {
        return (!this.rotated ? 81 : 9);
    }

    @Override
    public int getHeight() {
        return (!this.rotated ? 9 : 81);
    }

    @Override
    public boolean isRenderedInCreative() {
        return false;
    }

    @Override
    public boolean canChangeStyle() {
        return true;
    }

    @Override
    public void render(float partialTicks) {

        if (!(enabled || configMode())) return;

        int level = ForgeHooks.getTotalArmorValue(this.mc.thePlayer);
        if (this.configMode() && level == 0) level = 10;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (this.style == RenderStyle.GLYPH) {
            renderIconStrip(level);
        } else if (this.style == RenderStyle.SOLID) {
            renderSolidBar(level);
        }

        GL11.glDisable(GL11.GL_BLEND);
    }

    private void renderIconStrip(int level) {

        this.mc.renderEngine.bindTexture(Gui.icons);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int left = this.posX;
        int top = this.posY;

        for (int i = 1; level > 0 && i < 20; i += 2) {
            if (i < level) {
                this.drawTexturedModalRect(left, top, 34, 9, 9, 9);
            } else if (i == level) {
                this.drawTexturedModalRect(left, top, 25, 9, 9, 9);
            } else if (i > level) {
                this.drawTexturedModalRect(left, top, 16, 9, 9, 9);
            }
            if (!this.rotated) {
                left += 8;
            } else {
                top += 8;
            }
        }
    }

    private void renderSolidBar(int level) {

        float fill = (level / 20.0F);
        int color = 0xE6E6FF;

        if (!this.rotated) {
            RenderAssist.renderSolidBar(this.posX, this.posY, this.getWidth(), this.getHeight(), fill, color, false);
        } else {
            GL11.glPushMatrix();
            GL11.glTranslatef(this.posX, this.posY, 0.0F);
            GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-this.posX - this.getHeight(), -this.posY, 0.0F);
            RenderAssist.renderSolidBar(this.posX, this.posY, this.getHeight(), this.getWidth(), fill, color, false);
            GL11.glPopMatrix();
        }
    }

}
