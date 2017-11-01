package advancedhud.api;

import org.lwjgl.opengl.GL11;
import advancedhud.AdvancedHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

/**
 * Some methods which are usually in GuiIngame, but since we don't have direct access when rendering in HudItem, you may need to use these.
 * @author maxpowa
 *
 */
public class RenderAssist {

    private static final ResourceLocation ICONS = new ResourceLocation(AdvancedHUD.MODID, "textures/gui/ahud_icons.png");
    private static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Controls render "level" for layering textures overtop one another.
     */
    public static float zLevel;

//    public static void drawUnfilledCircle(float posX, float posY, float radius, int num_segments, int color) {
//        float a = (color >> 24 & 255) / 255.0F;
//        float r = (color >> 16 & 255) / 255.0F;
//        float g = (color >> 8 & 255) / 255.0F;
//        float b = (color & 255) / 255.0F;
//        Tessellator tessellator = Tessellator.instance;
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glDisable(GL11.GL_TEXTURE_2D);
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        GL11.glColor4f(r, g, b, a);
//        tessellator.startDrawing(GL11.GL_LINE_LOOP);
//        for (int i = 0; i < num_segments; i++) {
//            double theta = 2.0f * Math.PI * i / num_segments; // get the current angle
//            double x = radius * Math.cos(theta); // calculate the x component
//            double y = radius * Math.sin(theta); // calculate the y component
//            tessellator.addVertex(x + posX, y + posY, 0.0D); // output vertex
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
        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(r, g, b, a);
        tessellator.startDrawing(GL11.GL_LINE_LOOP);
        tessellator.addVertex(x1, y2, 0.0D);
        tessellator.addVertex(x2, y2, 0.0D);
        tessellator.addVertex(x2, y1, 0.0D);
        tessellator.addVertex(x1, y1, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
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
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + 0, y + height, zLevel, (textureX + 0) * f, (textureY + height) * f);
        tessellator.addVertexWithUV(x + width, y + height, zLevel, (textureX + width) * f, (textureY + height) * f);
        tessellator.addVertexWithUV(x + width, y + 0, zLevel, (textureX + width) * f, (textureY + 0) * f);
        tessellator.addVertexWithUV(x + 0, y + 0, zLevel, (textureX + 0) * f, (textureY + 0) * f);
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
        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GL11.glColor4f(r, g, b, a);
        tessellator.startDrawingQuads();
        tessellator.addVertex(left, bottom, 0.0D);
        tessellator.addVertex(right, bottom, 0.0D);
        tessellator.addVertex(right, top, 0.0D);
        tessellator.addVertex(left, top, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

//    /**
//     * Renders the specified item of the inventory slot at the specified location.
//     */
//    public static void renderInventorySlot(int slot, int x, int y, float partialTick, Minecraft mc) {
//        RenderItem itemRenderer = new RenderItem();
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
//            itemRenderer.renderItemAndEffectIntoGUI(mc.fontRendererObj, mc.getTextureManager(), itemstack, x, y);
//
//            if (f1 > 0.0F) {
//                GL11.glPopMatrix();
//            }
//
//            itemRenderer.renderItemOverlayIntoGUI(mc.fontRendererObj, mc.getTextureManager(), itemstack, x, y);
//        }
//    }


    public static void renderSolidBar(int x, int y, int width, int height, float fill, int color, boolean highlight) {

        mc.renderEngine.bindTexture(ICONS);

        if (fill < 1.0F) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            renderSolidBarPart(x, y, 8, 48, width, height, 1.0F);
        }
        if (fill > 0.0F) {
            float r = (color >> 16 & 255) / 255.0F;
            float g = (color >> 8 & 255) / 255.0F;
            float b = (color & 255) / 255.0F;
            GL11.glColor4f(r, g, b, 1.0F);
            renderSolidBarPart(x, y, 0, 48, width, height, fill);
        }
    }

    private static void renderSolidBarPart(int x, int y, int textureX, int textureY, int width, int height, float fill) {

        int fillWidth = (int)(width * fill);

        int widthLeft = Math.min(fillWidth, 4);
        int widthRight = 4 + fillWidth - width;
        int widthCenter = Math.min(width - 8, fillWidth - 4);

        int heightTop = Math.min(height - 2, 4);
        int heightBottom = Math.min(height - 2, 4);
        int heightCenter = height - heightTop - heightBottom;

        float u0 = textureX / 256.0F;
        float u1 = (textureX + widthLeft) / 256.0F;
        float u2 = (textureX + 4) / 256.0F;
        float u3 = (textureX + 4 + widthRight) / 256.0F;

        float v0 = textureY / 256.0F;
        float v1 = (textureY + heightTop) / 256.0F;
        float v2 = (textureY + 8 - heightBottom) / 256.0F;
        float v3 = (textureY + 8) / 256.0F;

        if (widthLeft > 0) {
            if (heightTop > 0) {
                drawSpriteUV(x, y, widthLeft, heightTop, u0, v0, u1, v1);
            }
            if (heightCenter > 0) {
                drawSpriteUV(x, y + heightTop, widthLeft, heightCenter, u0, v1, u1, v2);
            }
            if (heightBottom > 0) {
                drawSpriteUV(x, y + height - heightBottom, widthLeft, heightTop, u0, v2, u1, v3);
            }
        }
        if (widthCenter > 0) {
            if (heightTop > 0) {
                drawSpriteUV(x + widthLeft, y, widthCenter, heightTop, u1, v0, u2, v1);
            }
            if (heightCenter > 0) {
                drawSpriteUV(x + widthLeft, y + heightTop, widthCenter, heightCenter, u1, v1, u2, v2);
            }
            if (heightBottom > 0) {
                drawSpriteUV(x + widthLeft, y + height - heightBottom, widthCenter, heightBottom, u1, v2, u2, v3);
            }
        }
        if (widthRight > 0) {
            if (heightTop > 0) {
                drawSpriteUV(x + width - 4, y, widthRight, heightTop, u2, v0, u3, v1);
            }
            if (heightCenter > 0) {
                drawSpriteUV(x + width - 4, y + heightTop, widthRight, heightCenter, u2, v1, u3, v2);
            }
            if (heightBottom > 0) {
                drawSpriteUV(x + width - 4, y + height - heightBottom, widthRight, heightBottom, u2, v2, u3, v3);
            }
        }
    }


    public static void renderSolidBar(int x, int y, int width, int height, float[] fills, int[] colors, boolean highlight) {

        mc.renderEngine.bindTexture(ICONS);

        float fillTotal = 0.0F;
        for (float value : fills) {
            fillTotal += value;
        }

        if (fillTotal < 1.0F) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            renderSolidBarPart(x, y, 8, 48, width, height, 1.0F);
        }
        if (fillTotal > 0.0F) {
            int offsetX = 0;
            int[] fillWidths = getFillWidths(width, fills);

            for (int i = 0; i < fills.length; i++) {
                int color = colors[i];
                float r = (color >> 16 & 255) / 255.0F;
                float g = (color >> 8 & 255) / 255.0F;
                float b = (color & 255) / 255.0F;
                GL11.glColor4f(r, g, b, 1.0F);

                if (i > 0) offsetX += fillWidths[i - 1];
                renderSolidBarPart(x, y, 0, 48, width, height, offsetX, fillWidths[i]);
            }
        }
    }

    private static int[] getFillWidths(int width, float[] fills) {

        int[] result = new int[fills.length];
        int total = 0;
        float fillTotal = 0.0F;

        for ( int i = 0 ; i < result.length ; i++ ) {

            float fill = (fills[i] * width);
            result[i] = Math.round(fill);

            total += result[i];
            fillTotal += fill;

        }

        int diff = Math.round(fillTotal) - total;
        if (diff != 0) {
            result[0] += diff;
        }

        return result;
    }

    private static void renderSolidBarPart(int x, int y, int textureX, int textureY, int width, int height, int offsetX, int fillWidth) {

        if (fillWidth == 0) return;

        int widthLeft = (offsetX == 0 ? Math.min(fillWidth, 4) : 0);
        int widthRight = 4 + (fillWidth + offsetX) - width;
        int widthCenter = Math.min(width - 8, fillWidth - (offsetX == 0 ? 4 : 0)); if (widthCenter + offsetX == width && widthRight > 0) widthCenter -= widthRight;

        int heightTop = Math.min(height - 2, 4);
        int heightBottom = Math.min(height - 2, 4);
        int heightCenter = height - heightTop - heightBottom;

        float u0 = textureX / 256.0F;
        float u1 = (textureX + (widthLeft > 0 ? widthLeft : 4)) / 256.0F;
        float u2 = (textureX + 4) / 256.0F;
        float u3 = (textureX + 4 + widthRight) / 256.0F;

        float v0 = textureY / 256.0F;
        float v1 = (textureY + heightTop) / 256.0F;
        float v2 = (textureY + 8 - heightBottom) / 256.0F;
        float v3 = (textureY + 8) / 256.0F;

        if (widthLeft > 0) {
            if (heightTop > 0) {
                drawSpriteUV(x, y, widthLeft, heightTop, u0, v0, u1, v1);
            }
            if (heightCenter > 0) {
                drawSpriteUV(x, y + heightTop, widthLeft, heightCenter, u0, v1, u1, v2);
            }
            if (heightBottom > 0) {
                drawSpriteUV(x, y + height - heightBottom, widthLeft, heightTop, u0, v2, u1, v3);
            }
        }
        if (widthCenter > 0) {
            if (heightTop > 0) {
                drawSpriteUV(x + offsetX + widthLeft, y, widthCenter, heightTop, u1, v0, u2, v1);
            }
            if (heightCenter > 0) {
                drawSpriteUV(x + offsetX + widthLeft, y + heightTop, widthCenter, heightCenter, u1, v1, u2, v2);
            }
            if (heightBottom > 0) {
                drawSpriteUV(x + offsetX + widthLeft, y + height - heightBottom, widthCenter, heightBottom, u1, v2, u2, v3);
            }
        }
        if (widthRight > 0) {
            if (heightTop > 0) {
                drawSpriteUV(x + width - 4, y, widthRight, heightTop, u2, v0, u3, v1);
            }
            if (heightCenter > 0) {
                drawSpriteUV(x + width - 4, y + heightTop, widthRight, heightCenter, u2, v1, u3, v2);
            }
            if (heightBottom > 0) {
                drawSpriteUV(x + width - 4, y + height - heightBottom, widthRight, heightBottom, u2, v2, u3, v3);
            }
        }
    }

    private static void drawSpriteUV(int x, int y, int width, int height, float u1, float v1, float u2, float v2) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, zLevel, u1, v2);
        tessellator.addVertexWithUV(x + width, y + height, zLevel, u2, v2);
        tessellator.addVertexWithUV(x + width, y, zLevel, u2, v1);
        tessellator.addVertexWithUV(x, y, zLevel, u1, v1);
        tessellator.draw();
    }

}
