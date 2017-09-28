package advancedhud.client.huditems;

import java.awt.Color;
import org.lwjgl.opengl.GL11;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import net.minecraft.client.renderer.OpenGlHelper;

public class HudItemRecordDisplay extends HudItem {

    private int recordPlayingUpFor;
    private boolean recordIsPlaying;
    private String recordPlaying;

    @Override
    public String getName() {
        return "record";
    }

    @Override
    public int getDefaultID() {
        return 12;
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
    public boolean canRotate() {
        return false;
    }

    @Override
    public boolean needsTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.recordPlayingUpFor > 0) {
            --this.recordPlayingUpFor;
        }
    }

    @Override
    public void render(float partialTicks) {

        if (!(enabled || configMode())) return;

        if (this.recordPlayingUpFor > 0) {
            float hue = this.recordPlayingUpFor - partialTicks;
            int opacity = (int)(hue * 256.0F / 20.0F);
            if (opacity > 255) opacity = 255;

            if (opacity > 0) {
                GL11.glPushMatrix();
                GL11.glTranslatef(this.posX + this.getWidth() / 2, this.posY + this.getHeight() / 2, 0.0F);
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                int color = this.recordIsPlaying ? Color.HSBtoRGB(hue / 50.0F, 0.7F, 0.6F) & 0xFFFFFF : 0xFFFFFF;
                this.mc.fontRendererObj.drawString(this.recordPlaying, -this.mc.fontRendererObj.getStringWidth(this.recordPlaying) / 2, -4, color | opacity << 24);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }
        }
    }

    public void setRecordPlaying(String recordName, boolean isPlaying) {
        this.recordPlaying = recordName;
        this.recordPlayingUpFor = 60;
        this.recordIsPlaying = isPlaying;
    }

}
