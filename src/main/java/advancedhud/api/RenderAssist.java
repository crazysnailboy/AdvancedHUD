package advancedhud.api;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

/**
 * Some methods which are usually in GuiIngame, but since we don't have direct access when rendering in HudItem, you may need to use these.
 * @author maxpowa
 *
 */
public class RenderAssist {

    /**
     * Controls render "level" for layering textures overtop one another.
     */
    public static float zLevel;

//    public static void drawUnfilledCircle(float posX, float posY, float radius, int num_segments, int color) {
//        float a = (color >> 24 & 255) / 255.0F;
//        float r = (color >> 16 & 255) / 255.0F;
//        float g = (color >> 8 & 255) / 255.0F;
//        float b = (color & 255) / 255.0F;
//        Tessellator tessellator = Tessellator.getInstance();
//        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glDisable(GL11.GL_TEXTURE_2D);
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        GL11.glColor4f(r, g, b, a);
//        worldRenderer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION); // tessellator.startDrawing(GL11.GL_LINE_LOOP);
//        for (int i = 0; i < num_segments; i++) {
//            double theta = 2.0f * Math.PI * i / num_segments; // get the current angle
//            double x = radius * Math.cos(theta); // calculate the x component
//            double y = radius * Math.sin(theta); // calculate the y component
//            worldRenderer.pos(x + posX, y + posY, 0.0D).endVertex(); // tessellator.addVertex(x + posX, y + posY, 0.0D); // output vertex
//        }
//        tessellator.draw();
//        GL11.glEnable(GL11.GL_TEXTURE_2D);
//        GL11.glDisable(GL11.GL_BLEND);
//    }

//    public static void drawCircle(float posX, float posY, float radius, int num_segments, int color) {
//        float a = (color >> 24 & 255) / 255.0F;
//        float r = (color >> 16 & 255) / 255.0F;
//        float g = (color >> 8 & 255) / 255.0F;
//        float b = (color & 255) / 255.0F;
//        // Tessellator tessellator = Tessellator.instance;
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glDisable(GL11.GL_TEXTURE_2D);
//        // GL11.glShadeModel(GL11.GL_SMOOTH);
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        GL11.glColor4f(r, g, b, a);
//
//        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
//        GL11.glVertex2f(posX, posY); // center of circle
//        for (int i = num_segments; i >= 0; i--) {
//            double theta = i * (Math.PI * 2) / num_segments;
//            GL11.glVertex2d(posX + radius * Math.cos(theta), posY + radius * Math.sin(theta));
//        }
//        GL11.glEnd();
//
//        GL11.glEnable(GL11.GL_TEXTURE_2D);
//        GL11.glDisable(GL11.GL_BLEND);
//    }

    /**
     * Draws a color rectangle outline with the specified coordinates and color.<br>Color must have all four hex elements (0xFFFFFFFF)
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color
     */
    public static void drawUnfilledRect(float x1, float y1, float x2, float y2, int color) {
        float j1;

        if (x1 < x2) {
            j1 = x1;
            x1 = x2;
            x2 = j1;
        }

        if (y1 < y2) {
            j1 = y1;
            y1 = y2;
            y2 = j1;
        }

        float a = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.color(r, g, b, a);
        worldRenderer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
        worldRenderer.pos(x1, y2, 0.0D).endVertex();
        worldRenderer.pos(x2, y2, 0.0D).endVertex();
        worldRenderer.pos(x2, y1, 0.0D).endVertex();
        worldRenderer.pos(x1, y1, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

//    public static void drawHorizontalLine(int x1, int y1, int x2, int y2) {
//        if (y1 < x1) {
//            int i1 = x1;
//            x1 = y1;
//            y1 = i1;
//        }
//
//        drawRect(x1, x2, y1 + 1, x2 + 1, y2);
//    }

//    public static void drawVerticalLine(int x1, int y1, int x2, int y2) {
//        if (x2 < y1) {
//            int i1 = y1;
//            y1 = x2;
//            x2 = i1;
//        }
//
//        drawRect(x1, y1 + 1, x1 + 1, x2, y2);
//    }

    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
     */
    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        float f = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(x + 0, y + height, RenderAssist.zLevel).tex((textureX + 0) * f, (textureY + height) * f).endVertex();
        worldRenderer.pos(x + width, y + height, RenderAssist.zLevel).tex((textureX + width) * f, (textureY + height) * f).endVertex();
        worldRenderer.pos(x + width, y + 0, RenderAssist.zLevel).tex((textureX + width) * f, (textureY + 0) * f).endVertex();
        worldRenderer.pos(x + 0, y + 0, RenderAssist.zLevel).tex((textureX + 0) * f, (textureY + 0) * f).endVertex();
        tessellator.draw();
    }

    /**
     * Draws a solid color rectangle with the specified coordinates and color (ARGB format). Args: x1, y1, x2, y2, color
     */
    public static void drawRect(float left, float top, float right, float bottom, int color) {
        float j1;

        if (left < right) {
            j1 = left;
            left = right;
            right = j1;
        }

        if (top < bottom) {
            j1 = top;
            top = bottom;
            bottom = j1;
        }

        float a = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(r, g, b, a);
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldRenderer.pos(left, bottom, 0.0D).endVertex();
        worldRenderer.pos(right, bottom, 0.0D).endVertex();
        worldRenderer.pos(right, top, 0.0D).endVertex();
        worldRenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

//    /**
//     * Renders the specified item of the inventory slot at the specified location.
//     */
//    public static void renderInventorySlot(int slot, int x, int y, float partialTick, Minecraft mc) {
//        RenderItem itemRenderer = mc.getRenderItem(); // RenderItem itemRenderer = new RenderItem();
//        ItemStack itemstack = mc.thePlayer.inventory.mainInventory[slot];
//        x += 91;
//        y += 12;
//
//        if (itemstack != null) {
//            float f1 = itemstack.animationsToGo - partialTick;
//
//            if (f1 > 0.0F) {
//                GL11.glPushMatrix();
//                float f2 = 1.0F + f1 / 5.0F;
//                GL11.glTranslatef(x + 8, y + 12, 0.0F);
//                GL11.glScalef(1.0F / f2, (f2 + 1.0F) / 2.0F, 1.0F);
//                GL11.glTranslatef(-(x + 8), -(y + 12), 0.0F);
//            }
//
//            itemRenderer.renderItemAndEffectIntoGUI(itemstack, x, y); // itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, x, y);
//
//            if (f1 > 0.0F) {
//                GL11.glPopMatrix();
//            }
//
//            itemRenderer.renderItemOverlayIntoGUI(mc.fontRendererObj, itemstack, x, y, (String)null); // itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, x, y);
//        }
//    }

}
