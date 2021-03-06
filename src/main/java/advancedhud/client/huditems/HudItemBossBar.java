package advancedhud.client.huditems;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import advancedhud.ReflectionHelper;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.BossInfo;
import net.minecraftforge.client.GuiIngameForge;

public class HudItemBossBar extends HudItem {

    private static final ResourceLocation GUI_BARS_TEXTURES = new ResourceLocation("textures/gui/bars.png");
    private static final Field mapBossInfosField = ReflectionHelper.getDeclaredField(GuiBossOverlay.class, "mapBossInfos", "field_184060_g");

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
    public boolean canRotate() {
        return false;
    }

    @Override
    public void render(float partialTicks) {

        if (!(enabled || configMode())) return;

        this.mc.renderEngine.bindTexture(Gui.ICONS);

        int x = this.posX;
        int y = this.posY + 11;
        this.drawTexturedModalRect(x, y, 0, 74, 182, 5);
        this.drawTexturedModalRect(x, y, 0, 74, 182, 5);
        this.drawTexturedModalRect(x, y, 0, 79, 182, 5);

        String s = I18n.format("advancedhud.configuration.title");
        this.mc.fontRenderer.drawStringWithShadow(s, this.posX + 91 - this.mc.fontRenderer.getStringWidth(s) / 2, y - 10, 0xFFFFFF);
    }

    @Override
    public void render(float partialTicks, Gui gui) {

        if (!(enabled || configMode())) return;

        Map<UUID, BossInfoClient> bossInfos = ReflectionHelper.getFieldValue(mapBossInfosField, ((GuiIngameForge)gui).getBossOverlay());

        if (!bossInfos.isEmpty()) {
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            for (BossInfoClient bossInfo : bossInfos.values()) {

                int x = this.posX;
                int y = this.posY + 11;

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.renderEngine.bindTexture(GUI_BARS_TEXTURES);

                this.drawTexturedModalRect(x, y, 0, bossInfo.getColor().ordinal() * 5 * 2, 182, 5);

                if (bossInfo.getOverlay() != BossInfo.Overlay.PROGRESS) {
                    this.drawTexturedModalRect(x, y, 0, 80 + (bossInfo.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
                }

                int i = (int)(bossInfo.getPercent() * 182.0F);
                if (i > 0) {
                    this.drawTexturedModalRect(x, y, 0, bossInfo.getColor().ordinal() * 5 * 2 + 5, i, 5);
                    if (bossInfo.getOverlay() != BossInfo.Overlay.PROGRESS) {
                        this.drawTexturedModalRect(x, y, 0, 80 + (bossInfo.getOverlay().ordinal() - 1) * 5 * 2 + 5, i, 5);
                    }
                }

                String s = bossInfo.getName().getFormattedText();
                this.mc.fontRenderer.drawStringWithShadow(s, this.posX + (91 - this.mc.fontRenderer.getStringWidth(s) / 2), y - 9, 0xFFFFFF);

                if (y >= scaledresolution.getScaledHeight() / 3) {
                    break;
                }

                y += 10 + this.mc.fontRenderer.FONT_HEIGHT;
            }
        }
    }

}
