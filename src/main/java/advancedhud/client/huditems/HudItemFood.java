package advancedhud.client.huditems;

import java.util.Random;
import org.lwjgl.opengl.GL11;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.api.RenderAssist;
import advancedhud.api.RenderStyle;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.MobEffects;

public class HudItemFood extends HudItem {

    Random rand = new Random();

    @Override
    public String getName() {
        return "food";
    }

    @Override
    public int getDefaultID() {
        return 4;
    }

    @Override
    public Alignment getDefaultAlignment() {
        return (!this.rotated ? Alignment.BOTTOMCENTER : Alignment.CENTERRIGHT);
    }

    @Override
    public int getDefaultPosX() {
        return (!this.rotated ? HUDRegistry.screenWidth / 2 + 10 : HUDRegistry.screenWidth - 39);
    }

    @Override
    public int getDefaultPosY() {
        return (!this.rotated ? HUDRegistry.screenHeight - 39 : HUDRegistry.screenHeight / 2 + 10);
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
    public boolean isRenderedInCreative() {
        return false;
    }

    @Override
    public void render(float partialTicks) {

        if (!(enabled || configMode())) return;

        int level = this.mc.thePlayer.getFoodStats().getFoodLevel();
        boolean hunger = this.mc.thePlayer.isPotionActive(MobEffects.HUNGER);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        if (this.style == RenderStyle.GLYPH) {
            renderIconStrip(level, hunger);
        } else if (this.style == RenderStyle.SOLID) {
            renderSolidBar(level, hunger);
        }

        GlStateManager.disableBlend();
    }

    private void renderIconStrip(int level, boolean hunger) {

        this.mc.renderEngine.bindTexture(Gui.ICONS);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int left = this.posX + 81;
        int top = this.posY;
        float saturationLevel = this.mc.thePlayer.getFoodStats().getSaturationLevel();
        int updateCounter = this.mc.ingameGUI.getUpdateCounter();

        for (int i = 0; i < 10; ++i) {

            int idx = i * 2 + 1;
            int x = (!this.rotated ? left - i * 8 - 9 : left - 81);
            int y = (!this.rotated ? top : top + 82 - i * 8 - 9);
            int icon = 16;
            byte background = 0;

            if (hunger) {
                icon += 36;
                background = 13;
            }

            if (saturationLevel <= 0.0F && updateCounter % (level * 3 + 1) == 0) {
                y = top + this.rand.nextInt(3) - 1;
                if (this.rotated) {
                    x = left - 81 + this.rand.nextInt(3) - 1;
                    y = top + 82 - i * 8 - 9;
                }
            }

            this.drawTexturedModalRect(x, y, 16 + background * 9, 27, 9, 9);

            if (idx < level) {
                this.drawTexturedModalRect(x, y, icon + 36, 27, 9, 9);
            } else if (idx == level) {
                this.drawTexturedModalRect(x, y, icon + 45, 27, 9, 9);
            }
        }
    }

    private void renderSolidBar(int level, boolean hunger) {

        float fill = (level / 20.0F);
        int color = (hunger ? 0x9CBA12 : 0xE7801A);

        if (!this.rotated) {
            RenderAssist.renderSolidBar(this.posX, this.posY, this.getWidth(), this.getHeight(), fill, color, false);
        } else {
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.posX, this.posY, 0.0F);
            GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(-this.posX - this.getHeight(), -this.posY, 0.0F);
            RenderAssist.renderSolidBar(this.posX, this.posY, this.getHeight(), this.getWidth(), fill, color, false);
            GlStateManager.popMatrix();
        }
    }

}
