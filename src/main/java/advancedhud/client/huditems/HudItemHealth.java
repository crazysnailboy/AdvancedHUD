package advancedhud.client.huditems;

import java.util.Random;
import org.lwjgl.opengl.GL11;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.api.RenderAssist;
import advancedhud.api.RenderStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.MathHelper;

public class HudItemHealth extends HudItem {

    private static final Random rand = new Random();

    private long lastSystemTime = 0L;
    private long healthUpdateCounter = 0L;
    private int playerHealth = 0;
    private int lastPlayerHealth = 0;

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
    public boolean canChangeStyle() {
        return true;
    }

    @Override
    public int getHeight() {
        return (!this.rotated ? 9 : 81);
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

        EntityPlayer player = (EntityPlayer)this.mc.getRenderViewEntity();
        int health = MathHelper.ceil(player.getHealth());
        int updateCounter = this.mc.ingameGUI.getUpdateCounter();

        if (health < this.playerHealth && player.hurtResistantTime > 0) {
            this.lastSystemTime = Minecraft.getSystemTime();
            this.healthUpdateCounter = (long)(updateCounter + 20);
        } else if (health > this.playerHealth && player.hurtResistantTime > 0) {
            this.lastSystemTime = Minecraft.getSystemTime();
            this.healthUpdateCounter = (long)(updateCounter + 10);
        }
        if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
            this.playerHealth = health;
            this.lastPlayerHealth = health;
            this.lastSystemTime = Minecraft.getSystemTime();
        }
        this.playerHealth = health;

        int healthLast = this.lastPlayerHealth;
        float healthMax = (float)player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
        float absorb = player.getAbsorptionAmount();
        boolean highlight = this.healthUpdateCounter > (long)updateCounter && (this.healthUpdateCounter - (long)updateCounter) / 3L % 2L == 1L;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        if (this.style == RenderStyle.GLYPH) {
            renderIconStrip(health, healthLast, healthMax, absorb, highlight, player, updateCounter);
        } else if (this.style == RenderStyle.SOLID) {
            renderSolidBar(health, healthLast, healthMax, absorb, highlight, player);
        }

        GlStateManager.disableBlend();
    }

    private void renderIconStrip(int health, int healthLast, float healthMax, float absorb, boolean highlight, EntityPlayer player, int updateCounter) {

        this.mc.renderEngine.bindTexture(Gui.ICONS);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int healthRows = MathHelper.ceil((healthMax + absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);
        int regen = (player.isPotionActive(MobEffects.REGENERATION) ? updateCounter % 25 : -1);

        this.rand.setSeed((long)(updateCounter * 312871));

        int left = this.posX;
        int top = this.posY;

        final int textureX = 16 + (player.isPotionActive(MobEffects.POISON) ? 36 : (player.isPotionActive(MobEffects.WITHER) ? 72 : 0));
        final int textureY = 9 * (this.mc.world.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);

        float absorbRemaining = absorb;

        for (int i = MathHelper.ceil((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
            int row = MathHelper.ceil((float)(i + 1) / 10.0F) - 1;
            int x = (!this.rotated ? left + i % 10 * 8 : left - row * rowHeight);
            int y = (!this.rotated ? top - row * rowHeight : top + i % 10 * 8);

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
                else if (i * 2 + 1 == health) this.drawTexturedModalRect(x, y, textureX + 45, textureY, 9, 9);
            }

        }
    }

    private void renderSolidBar(int health, int healthLast, float healthMax, float absorb, boolean highlight, EntityPlayer player) {

        float fill = (health / healthMax);
        int color = (player.isPotionActive(MobEffects.POISON) ? 0xCEC049 : (player.isPotionActive(MobEffects.WITHER) ? 0x404040 : 0xFF0000));

        if (!this.rotated) {
            RenderAssist.renderSolidBar(this.posX, this.posY, this.getWidth(), this.getHeight(), fill, color, highlight);
        } else {
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.posX, this.posY, 0.0F);
            GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(-this.posX - this.getHeight(), -this.posY, 0.0F);
            RenderAssist.renderSolidBar(this.posX, this.posY, this.getHeight(), this.getWidth(), fill, color, false);
            GlStateManager.popMatrix();
        }
    }

}
