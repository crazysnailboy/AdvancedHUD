package advancedhud.client.huditems;

import org.lwjgl.opengl.GL11;
import advancedhud.AdvancedHUD;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.api.RenderAssist;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class HudItemHotbar extends HudItem {

    private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation ROTATE_WIDGETS = new ResourceLocation(AdvancedHUD.MODID, "textures/gui/rotateWidgets.png");

    @Override
    public String getName() {
        return "hotbar";
    }

    @Override
    public int getDefaultID() {
        return 1;
    }

    @Override
    public Alignment getDefaultAlignment() {
        return (!this.rotated ? Alignment.BOTTOMCENTER : Alignment.CENTERRIGHT);
    }

    @Override
    public int getDefaultPosX() {
        return (!this.rotated ? (HUDRegistry.screenWidth - this.getWidth()) / 2 : HUDRegistry.screenWidth - this.getWidth());
    }

    @Override
    public int getDefaultPosY() {
        return (!this.rotated ? HUDRegistry.screenHeight - this.getHeight() : (HUDRegistry.screenHeight - this.getHeight()) / 2);
    }

    @Override
    public int getWidth() {
        return (!this.rotated ? 182 : 22);
    }

    @Override
    public int getHeight() {
        return (!this.rotated ? 22 : 182);
    }

    @Override
    public void render(float partialTicks) {

        if (!(enabled || configMode())) return;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(WIDGETS);

        EntityPlayer player = (EntityPlayer)this.mc.getRenderViewEntity();
        float zLevel = RenderAssist.zLevel;
        if (!this.configMode()) RenderAssist.zLevel = -90.0F;
        if (!this.rotated) {
            this.drawTexturedModalRect(this.posX, this.posY, 0, 0, 182, 22);
            this.drawTexturedModalRect(this.posX - 1 + player.inventory.currentItem * 20, this.posY - 1, 0, 22, 24, 22);
        } else {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)this.posX + this.getWidth(), this.posY, 0.0F);
            GlStateManager.rotate(90F, 0.0F, 0.0F, 1.0F);
            this.drawTexturedModalRect(0, 0, 0, 0, 182, 22);
            this.mc.renderEngine.bindTexture(ROTATE_WIDGETS);
            this.drawTexturedModalRect(player.inventory.currentItem * 20 - 1, -1, 0, 0, 24, 24);
            GlStateManager.popMatrix();
        }
        if (!this.configMode()) RenderAssist.zLevel = zLevel;

        GlStateManager.disableBlend();
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < 9; ++i) {
            int x = (!this.rotated ? this.posX - 90 + i * 20 + 2 : this.posX - 88);
            int y = (!this.rotated ? this.posY - 6 - 3 : this.posY - 11 + i * 20 + 2);
            this.renderHotbarItem(i, x, y, partialTicks, player);
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
    }

    private void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player) {
        ItemStack itemstack = player.inventory.mainInventory[index];
        if (itemstack != null) {
            float f1 = (float)itemstack.animationsToGo - partialTicks;
            if (f1 > 0.0F) {
                GlStateManager.pushMatrix();
                float f2 = 1.0F + f1 / 5.0F;
                GlStateManager.translate((float)(xPos + 8), (float)(yPos + 12), 0.0F);
                GlStateManager.scale(1.0F / f2, (f2 + 1.0F) / 2.0F, 1.0F);
                GlStateManager.translate((float)(-(xPos + 8)), (float)(-(yPos + 12)), 0.0F);
            }
            this.mc.getRenderItem().renderItemAndEffectIntoGUI(itemstack, xPos + 91, yPos + 12);
            if (f1 > 0.0F) {
                GlStateManager.popMatrix();
            }
            this.mc.getRenderItem().renderItemOverlays(this.mc.fontRendererObj, itemstack, xPos + 91, yPos + 12);
        }
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
