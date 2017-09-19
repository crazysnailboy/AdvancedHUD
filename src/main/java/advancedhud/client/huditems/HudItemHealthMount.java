package advancedhud.client.huditems;

import org.lwjgl.opengl.GL11;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityHorse;

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

        Entity ridingEntity = this.mc.thePlayer.ridingEntity;
        if (ridingEntity == null && this.configMode()) ridingEntity = new EntityHorse(this.mc.theWorld);
        if (!(ridingEntity instanceof EntityLivingBase)) return;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.mc.renderEngine.bindTexture(Gui.icons);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int left_align = this.posX + 81;
        int right_height = 1;

        EntityLivingBase mount = (EntityLivingBase)ridingEntity;
        int health = (int)Math.ceil(mount.getHealth());
        double healthMax = mount.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
        int hearts = (int)Math.ceil(((float)healthMax + 0.5F) / 2F);

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

                right_height = i + 1;
            }
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

    @Override
    public boolean isRenderedInCreative() {
        return true;
    }

}
