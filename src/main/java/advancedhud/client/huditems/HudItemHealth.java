package advancedhud.client.huditems;

import java.util.Random;
import org.lwjgl.opengl.GL11;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.api.RenderAssist;
import advancedhud.api.RenderStyle;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

public class HudItemHealth extends HudItem {

    private static final Random rand = new Random();

    public HudItemHealth() {
        this.styles = new RenderStyle[] { RenderStyle.DEFAULT, RenderStyle.SOLID };
    }

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
    public boolean canChangeStyle() {
        return true;
    }

    @Override
    public boolean canMirror() {
        return true;
    }

    @Override
    public boolean isRenderedInCreative() {
        return false;
    }

    @Override
    public boolean shouldDrawOnMount() {
        return true;
    }

    @Override
    public void render(float partialTicks) {

        if (!(enabled || configMode())) return;

        int health = MathHelper.ceiling_float_int(this.mc.thePlayer.getHealth());
        int healthLast = MathHelper.ceiling_float_int(this.mc.thePlayer.prevHealth);
        float healthMax = (float)this.mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
        float absorb = this.mc.thePlayer.getAbsorptionAmount();
        boolean highlight = (this.mc.thePlayer.hurtResistantTime < 10 ? false : this.mc.thePlayer.hurtResistantTime / 3 % 2 == 1);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (this.mirrored) {
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glScalef(-1.0F, 1.0F, 1.0F);
            GL11.glTranslatef(-this.posX * 2 - this.getWidth(), 0.0F, 0.0F);
        }

        if (this.style == RenderStyle.DEFAULT) {
            renderIconStrip(health, healthLast, healthMax, absorb, highlight);
        } else if (this.style == RenderStyle.SOLID) {
            renderSolidBar(health, healthLast, healthMax, absorb, highlight);
        }

        if (this.mirrored) {
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
        }

        GL11.glDisable(GL11.GL_BLEND);
    }

    private void renderIconStrip(int health, int healthLast, float healthMax, float absorb, boolean highlight) {

        this.mc.renderEngine.bindTexture(Gui.icons);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int healthRows = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);
        int regen = (this.mc.thePlayer.isPotionActive(Potion.regeneration) ? this.mc.ingameGUI.getUpdateCounter() % 25 : -1);

        this.rand.setSeed((long)(this.mc.ingameGUI.getUpdateCounter() * 312871));

        int left = this.posX;
        int top = this.posY;

        final int textureX = 16 + (this.mc.thePlayer.isPotionActive(Potion.poison) ? 36 : (this.mc.thePlayer.isPotionActive(Potion.wither) ? 72 : 0));
        final int textureY = 9 * (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);

        float absorbRemaining = absorb;

        for (int i = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
            int row = MathHelper.ceiling_float_int((float)(i + 1) / 10.0F) - 1;
            int x = (!this.rotated ? left + i % 10 * 8 : left - row * rowHeight); // int x = (!this.rotated ? (!rightToLeft ? (left + (i % 10 * 8)) : (left + 81 - (i % 10 * 8) - 9)) : (left - row * rowHeight));
            int y = (!this.rotated ? top - row * rowHeight : (top + 81 - (i % 10 * 8) - 9)); // int y = (!this.rotated ? top - row * rowHeight : top + i % 10 * 8);

            if (health <= 4) y += this.rand.nextInt(2);
            if (i == regen) y -= 2;

            this.drawTexturedModalRect(x, y, (highlight ? 25 : 16), textureY, 9, 9);

            if (highlight) {
                if (i * 2 + 1 < healthLast)
                    this.drawTexturedModalRect(x, y, textureX + 54, textureY, 9, 9);
                else if (i * 2 + 1 == healthLast)
                    this.drawTexturedModalRect(x, y, textureX + 63, textureY, 9, 9);
            }

            if (absorbRemaining > 0.0F) {
                if (absorbRemaining == absorb && absorb % 2.0F == 1.0F)
                    this.drawTexturedModalRect(x, y, textureX + 153, textureY, 9, 9);
                else
                    this.drawTexturedModalRect(x, y, textureX + 144, textureY, 9, 9);
                absorbRemaining -= 2.0F;
            } else {
                if (i * 2 + 1 < health)
                    this.drawTexturedModalRect(x, y, textureX + 36, textureY, 9, 9);
                else if (i * 2 + 1 == health)
                    this.drawTexturedModalRect(x, y, textureX + 45, textureY, 9, 9);
            }
        }
    }

    private void renderSolidBar(int health, int healthLast, float healthMax, float absorb, boolean highlight) {

        float[] fills = new float[] {
            (health / (healthMax + absorb)),
            ((healthMax - health) / (healthMax + absorb)),
            (absorb / (healthMax + absorb))
        };

        int[] colors = new int[] {
            (this.mc.thePlayer.isPotionActive(Potion.poison) ? 0xCEC049 : (this.mc.thePlayer.isPotionActive(Potion.wither) ? 0x404040 : 0xFF0000)),
            0x000000,
            0xFFFF00
        };

        if (!this.rotated) {
            RenderAssist.renderSolidBar(this.posX, this.posY, this.getWidth(), this.getHeight(), fills, colors, highlight);
        } else {
            GL11.glPushMatrix();
            GL11.glTranslatef(this.posX, this.posY, 0.0F);
            GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-this.posX - this.getHeight(), -this.posY, 0.0F);
            RenderAssist.renderSolidBar(this.posX, this.posY, this.getHeight(), this.getWidth(), fills, colors, highlight);
            GL11.glPopMatrix();
        }
    }

}
