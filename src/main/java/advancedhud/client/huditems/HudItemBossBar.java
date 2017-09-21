package advancedhud.client.huditems;

import org.lwjgl.opengl.GL11;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.boss.BossStatus;

public class HudItemBossBar extends HudItem {

    @Override
    public String getName() {
        return "bossbar";
    }

    @Override
    public int getDefaultID() {
        return 6;
    }

    @Override
    public Alignment getDefaultAlignment() {
        return Alignment.TOPCENTER;
    }

    @Override
    public int getDefaultPosX() {
        return HUDRegistry.screenWidth / 2 - 91;
    }

    @Override
    public int getDefaultPosY() {
        return 0;
    }

    @Override
    public int getWidth() {
        return 182;
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public void render(float partialTicks) {

        if (!(enabled || configMode())) return;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        this.mc.renderEngine.bindTexture(Gui.icons);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        if ((BossStatus.bossName != null && BossStatus.statusBarTime > 0) || this.configMode()) {

            if (BossStatus.bossName != null) --BossStatus.statusBarTime;

            short short1 = 182;
            int j = this.posX;
            int k = (int)(BossStatus.healthScale * (short1 + 1)); if (BossStatus.bossName == null) k = 182;
            int b0 = this.posY + 11;
            this.drawTexturedModalRect(j, b0, 0, 74, short1, 5);
            this.drawTexturedModalRect(j, b0, 0, 74, short1, 5);

            if (k > 0) {
                this.drawTexturedModalRect(j, b0, 0, 79, k, 5);
            }

            String s = BossStatus.bossName != null ? BossStatus.bossName : I18n.format("advancedhud.configuration.title");
            this.mc.fontRendererObj.drawStringWithShadow(s, this.posX + 91 - this.mc.fontRendererObj.getStringWidth(s) / 2, b0 - 10, 0xFFFFFF);
        }

        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public boolean canRotate() {
        return false;
    }

}
