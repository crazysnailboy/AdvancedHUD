package advancedhud.client.huditems;

import java.util.Random;
import org.lwjgl.opengl.GL11;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
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
    public int getHeight() {
        return (!this.rotated ? 9 : 81);
    }

    @Override
    public void render(float partialTicks) {

        if (!(enabled || configMode())) return;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        this.mc.renderEngine.bindTexture(Gui.ICONS);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        EntityPlayer player = (EntityPlayer)this.mc.getRenderViewEntity();
        int updateCounter = this.mc.ingameGUI.getUpdateCounter();
        int health = MathHelper.ceiling_float_int(player.getHealth());
        boolean highlight = this.healthUpdateCounter > (long)updateCounter && (this.healthUpdateCounter - (long)updateCounter) / 3L % 2L == 1L;

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

        IAttributeInstance attrMaxHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        float healthMax = (float)attrMaxHealth.getAttributeValue();
        float absorb = player.getAbsorptionAmount();

        int healthRows = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);

        this.rand.setSeed((long)(updateCounter * 312871));

        int left = this.posX;
        int top = this.posY;

        int regen = -1;
        if (player.isPotionActive(MobEffects.REGENERATION)) {
            regen = updateCounter % 25;
        }

        final int TOP = 9 * (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
        final int BACKGROUND = (highlight ? 25 : 16);
        int MARGIN = 16;
        if (player.isPotionActive(MobEffects.POISON)) MARGIN += 36;
        else if (player.isPotionActive(MobEffects.WITHER)) MARGIN += 72;
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

        GlStateManager.disableBlend();
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
    public boolean shouldDrawAsPlayer() {
        return true;
    }

}
