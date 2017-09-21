package advancedhud.client.huditems;

import org.lwjgl.opengl.GL11;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class HudItemAir extends HudItem {

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
    public void render(float partialTicks) {

        if (!(enabled || configMode())) return;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        this.mc.renderEngine.bindTexture(Gui.ICONS);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int left = this.posX + 81;
        int top = this.posY;

        EntityPlayer player = (EntityPlayer)this.mc.getRenderViewEntity();
        if (player.isInsideOfMaterial(Material.WATER) || this.configMode()) {

            int air = player.getAir();
            int full = MathHelper.ceil((double)(air - 2) * 10.0D / 300.0D);
            int partial = MathHelper.ceil((double)air * 10.0D / 300.0D) - full;

            for (int i = 0; i < full + partial; ++i) {
                if (!this.rotated)
                    this.drawTexturedModalRect(left - i * 8 - 9, top, i < full ? 16 : 25, 18, 9, 9);
                else
                    this.drawTexturedModalRect(left - 81, top + 72 - i * 8, i < full ? 16 : 25, 18, 9, 9);
            }

        }

        GlStateManager.disableBlend();
    }

    @Override
    public boolean isRenderedInCreative() {
        return false;
    }

}
