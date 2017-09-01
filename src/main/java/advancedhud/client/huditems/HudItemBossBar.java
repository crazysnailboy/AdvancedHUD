package advancedhud.client.huditems;

import org.lwjgl.opengl.GL11;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.api.RenderAssist;
import advancedhud.client.ui.GuiAdvancedHUDConfiguration;
import advancedhud.client.ui.GuiScreenHudItem;
import advancedhud.client.ui.GuiScreenReposition;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.boss.BossStatus;

public class HudItemBossBar extends HudItem {

    @Override
    public String getName() {
        return "bossbar";
    }

    @Override
    public String getButtonLabel() {
        return I18n.format("advancedhud.item.bossbar.name");
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
    public int getDefaultID() {
        return 6;
    }

    @Override
    public void render(float partialTicks) {
        this.mc.renderEngine.bindTexture(Gui.icons);
        if (BossStatus.bossName != null && BossStatus.statusBarTime > 0 || this.mc.currentScreen instanceof GuiAdvancedHUDConfiguration || this.mc.currentScreen instanceof GuiScreenReposition) {
            if (BossStatus.bossName != null) {
                --BossStatus.statusBarTime;
            }
            short short1 = 182;
            int j = this.posX;
            int k = (int)(BossStatus.healthScale * (short1 + 1));
            int b0 = this.posY + 11;
            RenderAssist.drawTexturedModalRect(j, b0, 0, 74, short1, 5);
            RenderAssist.drawTexturedModalRect(j, b0, 0, 74, short1, 5);

            if (BossStatus.bossName == null) {
                k = 182;
            }
            if (k > 0) {
                RenderAssist.drawTexturedModalRect(j, b0, 0, 79, k, 5);
            }

            String s = BossStatus.bossName != null ? BossStatus.bossName : I18n.format("advancedhud.configuration.title");
            this.mc.fontRendererObj.drawStringWithShadow(s, this.posX + 91 - this.mc.fontRendererObj.getStringWidth(s) / 2, b0 - 10, 0xFFFFFF);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(Gui.icons);
        }
    }

    @Override
    public GuiScreen getConfigScreen() {
        return new GuiScreenHudItem(this.mc.currentScreen, this);
    }

    @Override
    public boolean canRotate() {
        return false;
    }
}
