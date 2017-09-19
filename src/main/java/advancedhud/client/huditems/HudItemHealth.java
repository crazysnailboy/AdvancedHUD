package advancedhud.client.huditems;

import java.util.Random;
import org.lwjgl.opengl.GL11;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

public class HudItemHealth extends HudItem {

    Random rand = new Random();

    @Override
    public String getName() {
        return "health";
    }

    @Override
    public int getDefaultID() {
        return 2;
    }

    @Override
    public Alignment getDefaultAlignment() {
        return (!this.rotated ? Alignment.BOTTOMCENTER : Alignment.CENTERRIGHT);
    }

    @Override
    public int getDefaultPosX() {
        return (!this.rotated ? HUDRegistry.screenWidth / 2 - 91 : HUDRegistry.screenWidth - 39);
    }

    @Override
    public int getDefaultPosY() {
        return (!this.rotated ? HUDRegistry.screenHeight - 39 : HUDRegistry.screenHeight / 2 - 91);
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

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        this.mc.renderEngine.bindTexture(Gui.icons);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        boolean highlight = this.mc.thePlayer.hurtResistantTime / 3 % 2 == 1;
        if (this.mc.thePlayer.hurtResistantTime < 10) {
            highlight = false;
        }

        IAttributeInstance attrMaxHealth = this.mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        int health = MathHelper.ceiling_float_int(this.mc.thePlayer.getHealth());
        int healthLast = MathHelper.ceiling_float_int(this.mc.thePlayer.prevHealth);
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
        if (this.mc.thePlayer.isPotionActive(Potion.poison)) MARGIN += 36;
        else if (this.mc.thePlayer.isPotionActive(Potion.wither)) MARGIN += 72;
        float absorbRemaining = absorb;

        for (int i = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
            int row = MathHelper.ceiling_float_int((float)(i + 1) / 10.0F) - 1;
            int x = (!this.rotated ? left + i % 10 * 8 : left - row * rowHeight);
            int y = (!this.rotated ? top - row * rowHeight : top + i % 10 * 8);

            if (health <= 4) y += this.rand.nextInt(2);
            if (i == regen) y -= 2;

            this.drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);

            if (highlight) {
                if (i * 2 + 1 < healthLast)
                    this.drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9);
                else if (i * 2 + 1 == healthLast)
                    this.drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9);
            }

            if (absorbRemaining > 0.0F) {
                if (absorbRemaining == absorb && absorb % 2.0F == 1.0F)
                    this.drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9);
                else
                    this.drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9);
                absorbRemaining -= 2.0F;
            } else {
                if (i * 2 + 1 < health)
                    this.drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9);
                else if (i * 2 + 1 == health)
                    this.drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9);
            }
        }

        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public boolean isRenderedInCreative() {
        return false;
    }

    @Override
    public boolean shouldDrawOnMount() {
        return true;
    }

}
