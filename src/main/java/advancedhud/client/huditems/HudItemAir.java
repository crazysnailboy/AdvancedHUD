package advancedhud.client.huditems;

import org.lwjgl.opengl.GL11;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.api.RenderAssist;
import advancedhud.api.RenderStyle;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;

public class HudItemAir extends HudItem {

    public HudItemAir() {
        this.styles = new RenderStyle[] { RenderStyle.DEFAULT, RenderStyle.SOLID };
    }

    @Override
    public String getName() {
        return "air";
    }

    @Override
    public int getDefaultID() {
        return 3;
    }

    @Override
    public Alignment getDefaultAlignment() {
        return (!this.rotated ? Alignment.BOTTOMCENTER : Alignment.CENTERRIGHT);
    }

    @Override
    public int getDefaultPosX() {
        return (!this.rotated ? HUDRegistry.screenWidth / 2 + 10 : HUDRegistry.screenWidth - 49);
    }

    @Override
    public int getDefaultPosY() {
        return (!this.rotated ? HUDRegistry.screenHeight - 49 : HUDRegistry.screenHeight / 2 + 10);
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
    public boolean canChangeStyle() {
        return true;
    }

    @Override
    public boolean canMirror() {
        return true;
    }

    @Override
    public boolean isRenderedInCreative() {
        return false;
    }

    @Override
    public void render(float partialTicks) {

        if (!((enabled && this.mc.thePlayer.isInsideOfMaterial(Material.water)) || configMode())) return;

        int air = this.mc.thePlayer.getAir();
        int full = MathHelper.ceiling_double_int((air - 2) * 10.0D / 300.0D);
        int partial = MathHelper.ceiling_double_int(air * 10.0D / 300.0D) - full;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (this.mirrored) {
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glScalef(-1.0F, 1.0F, 1.0F);
            GL11.glTranslatef(-this.posX * 2 - this.getWidth(), 0.0F, 0.0F);
        }

        if (this.style == RenderStyle.DEFAULT) {
            renderIconStrip(full, partial);
        } else if (this.style == RenderStyle.SOLID) {
            renderSolidBar(full, partial);
        }

        if (this.mirrored) {
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
        }

        GL11.glDisable(GL11.GL_BLEND);
    }

    private void renderIconStrip(int full, int partial) {

        this.mc.renderEngine.bindTexture(Gui.icons);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        for (int i = 0; i < full + partial; ++i) {
            int x = (!this.rotated ? this.posX + 81 - (i * 8) - 9 : this.posX);
            int y = (!this.rotated ? this.posY : this.posY + 81 - (i * 8) - 9);
            int textureX = (i < full ? 16 : 25);
            this.drawTexturedModalRect(x, y, textureX, 18, 9, 9);
        }
    }

    private void renderSolidBar(int full, int partial) {

        float fill = ((full + partial) / 10.0F);
        int color = 0x0080FF;

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
