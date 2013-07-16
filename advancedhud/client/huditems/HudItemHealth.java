package advancedhud.client.huditems;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.api.RenderAssist;

public class HudItemHealth extends HudItem {
    
    public String getName()
    {
      return "Health Bar";
    }

    public Alignment getDefaultAlignment()
    {
      return Alignment.BOTTOMCENTER;
    }

    public int getDefaultPosX()
    {
      return HUDRegistry.screenWidth / 2 - 91;
    }

    public int getDefaultPosY()
    {
      return HUDRegistry.screenHeight - 39;
    }

    public boolean isRenderedInCreative()
    {
      return false;
    }

    @Override
    public String getButtonLabel() {
        return "HEALTHBAR";
    }

    @Override
    public int getWidth() {
        return 90;
    }

    @Override
    public int getHeight() {
        return 9;
    }

    @Override
    public void render(float partialTicks) {
        Minecraft mc = HUDRegistry.getMinecraftInstance();
        RenderAssist.bind("textures/gui/icons.png");
        mc.mcProfiler.startSection("health");

        boolean highlight = mc.thePlayer.hurtResistantTime / 3 % 2 == 1;

        if (mc.thePlayer.hurtResistantTime < 10)
        {
            highlight = false;
        }

        AttributeInstance attrMaxHealth = mc.thePlayer.func_110148_a(SharedMonsterAttributes.field_111267_a);
        int health = MathHelper.ceiling_float_int(mc.thePlayer.func_110143_aJ());
        int healthLast = MathHelper.ceiling_float_int(mc.thePlayer.prevHealth);
        float healthMax = (float)attrMaxHealth.func_111126_e();
        float absorb = mc.thePlayer.func_110139_bj();

        int healthRows = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);

        Random rand = new Random();
        rand.setSeed((long)(mc.ingameGUI.getUpdateCounter() * 312871));

        int regen = -1;
        if (mc.thePlayer.isPotionActive(Potion.regeneration))
        {
            regen = mc.ingameGUI.getUpdateCounter() % 25;
        }

        final int TOP =  9 * (mc.theWorld.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
        final int BACKGROUND = (highlight ? 25 : 16);
        int MARGIN = 16;
        if (mc.thePlayer.isPotionActive(Potion.poison))      MARGIN += 36;
        else if (mc.thePlayer.isPotionActive(Potion.wither)) MARGIN += 72;
        float absorbRemaining = absorb;

        for (int i = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F) - 1; i >= 0; --i)
        {
            int row = MathHelper.ceiling_float_int((float)(i + 1) / 10.0F) - 1;
            int x = posX + i % 10 * 8;
            int y = posY - row * (rowHeight+2);

            if (health <= 4) y += rand.nextInt(2);
            if (i == regen) y -= 2;

            RenderAssist.drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);

            if (highlight)
            {
                if (i * 2 + 1 < healthLast)
                    RenderAssist.drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9); //6
                else if (i * 2 + 1 == healthLast)
                    RenderAssist.drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9); //7
            }

            if (absorbRemaining > 0.0F)
            {
                if (absorbRemaining == absorb && absorb % 2.0F == 1.0F)
                    RenderAssist.drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9); //17
                else
                    RenderAssist.drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9); //16
                absorbRemaining -= 2.0F;
            }
            else
            {
                if (i * 2 + 1 < health)
                    RenderAssist.drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9); //4
                else if (i * 2 + 1 == health)
                    RenderAssist.drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9); //5
            }
        }

        mc.mcProfiler.endSection();
    }

    @Override
    public int getDefaultID() {
        return 2;
    }
    
}
