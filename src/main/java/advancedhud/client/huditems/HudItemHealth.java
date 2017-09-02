package advancedhud.client.huditems;

import java.util.Random;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.api.RenderAssist;
import advancedhud.client.ui.GuiScreenHudItem;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

public class HudItemHealth extends HudItem {

    Random rand = new Random();
    float prevHealth = 0;
    long lastSystemTime = 0L;

    @Override
    public String getName() {
        return "health";
    }

    @Override
    public String getButtonLabel() {
        return I18n.format("advancedhud.item.health.name");
    }

    @Override
    public Alignment getDefaultAlignment() {
        return this.rotated ? Alignment.CENTERRIGHT : Alignment.BOTTOMCENTER;
    }

    @Override
    public int getDefaultPosX() {
        if (this.rotated)
            return HUDRegistry.screenWidth - 39;
        return HUDRegistry.screenWidth / 2 - 91;
    }

    @Override
    public int getDefaultPosY() {
        if (this.rotated)
            return HUDRegistry.screenHeight / 2 - 91;
        return HUDRegistry.screenHeight - 39;
    }

    @Override
    public boolean isRenderedInCreative() {
        return false;
    }

    @Override
    public int getWidth() {
        return this.rotated ? 9 : 81;
    }

    @Override
    public int getHeight() {
        return this.rotated ? 81 : 9;
    }

    @Override
    public void render(float partialTicks) {
        this.mc.renderEngine.bindTexture(Gui.icons);
        boolean highlight = this.mc.thePlayer.hurtResistantTime / 3 % 2 == 1;

        if (this.mc.thePlayer.hurtResistantTime < 10) {
            highlight = false;
        }

        if (this.mc.getSystemTime() - this.lastSystemTime > 1000L) {
            this.prevHealth = this.mc.thePlayer.getHealth();
            this.lastSystemTime = this.mc.getSystemTime();
        }

        IAttributeInstance attrMaxHealth = this.mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        int health = MathHelper.ceiling_float_int(this.mc.thePlayer.getHealth());
        int healthLast = MathHelper.ceiling_float_int(this.prevHealth); // int healthLast = MathHelper.ceiling_float_int(this.mc.thePlayer.prevHealth);
        float healthMax = (float)attrMaxHealth.getAttributeValue();
        float absorb = this.mc.thePlayer.getAbsorptionAmount();

        int healthRows = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);

        this.rand.setSeed((long)(this.mc.ingameGUI.getUpdateCounter() * 312871));

        int left = this.posX;
        int top = this.posY;

        int regen = -1;
        if (this.mc.thePlayer.isPotionActive(Potion.regeneration)) {
            regen = this.mc.ingameGUI.getUpdateCounter() % 25;
        }

        final int TOP = 9 * (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
        final int BACKGROUND = (highlight ? 25 : 16);
        int MARGIN = 16;
        if (this.mc.thePlayer.isPotionActive(Potion.poison))
            MARGIN += 36;
        else if (this.mc.thePlayer.isPotionActive(Potion.wither))
            MARGIN += 72;
        float absorbRemaining = absorb;
        if (!this.rotated) {
            for (int i = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
                //int b0 = (highlight ? 1 : 0);
                int row = MathHelper.ceiling_float_int((float)(i + 1) / 10.0F) - 1;
                int x = left + i % 10 * 8;
                int y = top - row * rowHeight;

                if (health <= 4)
                    y += this.rand.nextInt(2);
                if (i == regen)
                    y -= 2;

                RenderAssist.drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);

                if (highlight) {
                    if (i * 2 + 1 < healthLast)
                        RenderAssist.drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9); //6
                    else if (i * 2 + 1 == healthLast)
                        RenderAssist.drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9); //7
                }

                if (absorbRemaining > 0.0F) {
                    if (absorbRemaining == absorb && absorb % 2.0F == 1.0F)
                        RenderAssist.drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9); //17
                    else
                        RenderAssist.drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9); //16
                    absorbRemaining -= 2.0F;
                } else {
                    if (i * 2 + 1 < health)
                        RenderAssist.drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9); //4
                    else if (i * 2 + 1 == health)
                        RenderAssist.drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9); //5
                }
            }
        } else {
            for (int i = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
                //int b0 = (highlight ? 1 : 0);
                int row = MathHelper.ceiling_float_int((float)(i + 1) / 10.0F) - 1;
                int x = left - row * rowHeight;
                int y = top + i % 10 * 8;

                if (health <= 4)
                    y += this.rand.nextInt(2);
                if (i == regen)
                    y -= 2;

                RenderAssist.drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);

                if (highlight) {
                    if (i * 2 + 1 < healthLast)
                        RenderAssist.drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9); //6
                    else if (i * 2 + 1 == healthLast)
                        RenderAssist.drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9); //7
                }

                if (absorbRemaining > 0.0F) {
                    if (absorbRemaining == absorb && absorb % 2.0F == 1.0F)
                        RenderAssist.drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9); //17
                    else
                        RenderAssist.drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9); //16
                    absorbRemaining -= 2.0F;
                } else {
                    if (i * 2 + 1 < health)
                        RenderAssist.drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9); //4
                    else if (i * 2 + 1 == health)
                        RenderAssist.drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9); //5
                }
            }

        }
    }

    @Override
    public int getDefaultID() {
        return 2;
    }

    @Override
    public boolean shouldDrawOnMount() {
        return true;
    }

    @Override
    public boolean shouldDrawAsPlayer() {
        return true;
    }

    @Override
    public GuiScreen getConfigScreen() {
        return new GuiScreenHudItem(this.mc.currentScreen, this);
    }
}
