package advancedhud.client.huditems;

import java.awt.Color;
import org.lwjgl.opengl.GL11;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.client.GuiAdvancedHUD;
import advancedhud.client.ui.GuiScreenHudItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class HudItemRecordDisplay extends HudItem {

    private int recordPlayingUpFor;
    private boolean recordIsPlaying;
    private String recordPlaying;

    @Override
    public String getName() {
        return "record";
    }

    @Override
    public String getButtonLabel() {
        return "RECORDDISPLAY";
    }

    @Override
    public Alignment getDefaultAlignment() {
        return Alignment.BOTTOMCENTER;
    }

    @Override
    public int getDefaultPosX() {
        return HUDRegistry.screenWidth / 2 - 90;
    }

    @Override
    public int getDefaultPosY() {
        return HUDRegistry.screenHeight - 48;
    }

    @Override
    public int getWidth() {
        return 180;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public int getDefaultID() {
        return 12;
    }

    @Override
    public GuiScreen getConfigScreen() {
        return new GuiScreenHudItem(Minecraft.getMinecraft().currentScreen, this);
    }

    @Override
    public void render(float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        if (this.recordPlayingUpFor > 0) {
            float hue = this.recordPlayingUpFor - partialTicks;
            int opacity = (int) (hue * 256.0F / 20.0F);
            if (opacity > 255) {
                opacity = 255;
            }

            if (opacity > 0) {
                GL11.glTranslatef(this.posX + this.getWidth() / 2, this.posY + this.getHeight() / 2, 0.0F);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                int color = this.recordIsPlaying ? Color.HSBtoRGB(hue / 50.0F, 0.7F, 0.6F) & 0xFFFFFF : 0xFFFFFF;
                mc.fontRendererObj.drawString(this.recordPlaying, -mc.fontRendererObj.getStringWidth(this.recordPlaying) / 2, -4, color | opacity << 24);
            }
        }
    }

    @Override
    public boolean needsTick() {
        return true;
    }

    @Override
    public void tick() {
        if (Minecraft.getMinecraft().ingameGUI instanceof GuiAdvancedHUD) {
            GuiAdvancedHUD ingame = (GuiAdvancedHUD) Minecraft.getMinecraft().ingameGUI;
            if (ingame.recordPlaying != null && !ingame.recordPlaying.equals(this.recordPlaying)) {
                this.recordPlaying = ingame.recordPlaying;
                this.recordIsPlaying = ingame.recordIsPlaying;
                this.recordPlayingUpFor = ingame.recordPlayingUpFor * 2;
            }
        }

        if (this.recordPlayingUpFor > 0) {
            --this.recordPlayingUpFor;
        }
    }

    @Override
    public boolean canRotate() {
        return false;
    }

}
