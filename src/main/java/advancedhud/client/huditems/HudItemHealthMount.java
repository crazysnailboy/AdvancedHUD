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

    @Override
    public void render(float partialTicks) {

        if (!(enabled || configMode())) return;

        EntityPlayer player = (EntityPlayer)this.mc.getRenderViewEntity();
        Entity ridingEntity = player.ridingEntity;
        if (ridingEntity == null && this.configMode()) ridingEntity = new EntityHorse(this.mc.theWorld);
        if (!(ridingEntity instanceof EntityLivingBase)) return;

        EntityLivingBase mount = (EntityLivingBase)ridingEntity;
        int health = (int)Math.ceil(mount.getHealth());
        float healthMax = mount.getMaxHealth();
        int hearts = (int)(healthMax + 0.5F) / 2;
        if (hearts > 30) hearts = 30;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        this.mc.renderEngine.bindTexture(Gui.icons);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int align = (!this.rotated ? this.posX + this.getWidth() : this.posY + this.getHeight());
        int offset = 0;

        for (int heart = 0; hearts > 0; heart += 20) {

            int top = (!this.rotated ? this.posY + offset : 0);
            int left = (!this.rotated ? 0 : this.posX + offset);

            int count = Math.min(hearts, 10);
            hearts -= count;

            for (int i = 0; i < count; ++i) {
                int x = (!this.rotated ? align - i * 8 - 9 : left);
                int y = (!this.rotated ? top : align - i * 8 - 9);

                this.drawTexturedModalRect(x, y, 52, 9, 9, 9);
                this.drawTexturedModalRect(x, y, ((i * 2 + 1 + heart < health) ? 88 : 97), 9, 9, 9);
            }
            offset += 10;
        }

        GlStateManager.disableBlend();
    }

}
