package advancedhud.client.huditems;

import org.lwjgl.opengl.GL11;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;

public class HudItemHealthMount extends HudItem {

    @Override
    public String getName() {
        return "healthmount";
    }

    @Override
    public int getDefaultID() {
        return 7;
    }

    @Override
    public Alignment getDefaultAlignment() {
        return Alignment.BOTTOMCENTER;
    }

    @Override
    public int getDefaultPosX() {
        return HUDRegistry.screenWidth / 2 + 10;
    }

    @Override
    public int getDefaultPosY() {
        return HUDRegistry.screenHeight - 39;
    }

    @Override
    public int getWidth() {
        return 81;
    }

    @Override
    public int getHeight() {
        return 9;
    }

    @Override
    public void render(float partialTicks) {
        EntityPlayer player = (EntityPlayer)this.mc.getRenderViewEntity();
        Entity ridingEntity = player.getRidingEntity(); if (ridingEntity == null && this.configMode()) ridingEntity = new EntityHorse(this.mc.world);
        if (!(ridingEntity instanceof EntityLivingBase)) return;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        this.mc.renderEngine.bindTexture(Gui.ICONS);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int right_height = 1; // 39?
        int left_align = this.posX + 81;

        EntityLivingBase mount = (EntityLivingBase)ridingEntity;
        int health = (int)Math.ceil(mount.getHealth());
        float healthMax = mount.getMaxHealth();
        int hearts = (int)(healthMax + 0.5F) / 2;

        if (hearts > 30) hearts = 30;

        final int MARGIN = 52;
        final int BACKGROUND = MARGIN;
        final int HALF = MARGIN + 45;
        final int FULL = MARGIN + 36;

        for (int heart = 0; hearts > 0; heart += 20) {

            int top = this.posY + 1 - right_height;

            int rowCount = Math.min(hearts, 10);
            hearts -= rowCount;

            for (int i = 0; i < rowCount; ++i) {
                int x = left_align - i * 8 - 9;
                this.drawTexturedModalRect(x, top, BACKGROUND, 9, 9, 9);

                if (i * 2 + 1 + heart < health) {
                    this.drawTexturedModalRect(x, top, FULL, 9, 9, 9);
                } else if (i * 2 + 1 + heart == health) {
                    this.drawTexturedModalRect(x, top, HALF, 9, 9, 9);
                }

                right_height = i + 1; //right_height += 10;
            }
        }

        GlStateManager.disableBlend();
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
    public boolean isRenderedInCreative() {
        return true;
    }

}
