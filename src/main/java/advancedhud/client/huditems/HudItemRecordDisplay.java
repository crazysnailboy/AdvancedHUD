package advancedhud.client.huditems;

import java.awt.Color;
import org.lwjgl.opengl.GL11;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import net.minecraft.client.renderer.GlStateManager;

public class HudItemRecordDisplay extends HudItem {

    private int overlayMessageTime;
    private boolean animateOverlayMessageColor;
    private String overlayMessage;

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
    public void render(float partialTicks) {
        if (this.overlayMessageTime > 0) {
            float hue = this.overlayMessageTime - partialTicks;
            int opacity = (int)(hue * 256.0F / 20.0F);
            if (opacity > 255) opacity = 255;

            if (opacity > 0) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(this.posX + this.getWidth() / 2, this.posY + this.getHeight() / 2, 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                int color = this.animateOverlayMessageColor ? Color.HSBtoRGB(hue / 50.0F, 0.7F, 0.6F) & 0xFFFFFF : 0xFFFFFF;
                this.mc.fontRenderer.drawString(this.overlayMessage, -this.mc.fontRenderer.getStringWidth(this.overlayMessage) / 2, -4, color | opacity << 24);
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public boolean needsTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.overlayMessageTime > 0) {
            --this.overlayMessageTime;
        }
    }

    @Override
    public boolean canRotate() {
        return false;
    }

    public void setOverlayMessage(String message, boolean animateColor) {
        this.overlayMessage = message;
        this.overlayMessageTime = 60;
        this.animateOverlayMessageColor = animateColor;
    }

}
