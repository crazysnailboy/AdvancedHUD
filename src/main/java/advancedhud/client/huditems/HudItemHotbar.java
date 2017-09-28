package advancedhud.client.huditems;

import org.lwjgl.opengl.GL11;
import advancedhud.AdvancedHUD;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.api.RenderAssist;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

public class HudItemHotbar extends HudItem {

    private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation ROTATE_WIDGETS = new ResourceLocation(AdvancedHUD.MODID, "textures/gui/rotate_widgets.png");

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
    public boolean shouldDrawOnMount() {
        return true;
    }

    @Override
    public void render(float partialTicks) {

        if (!(enabled || configMode())) return;

        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(WIDGETS);

            EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
            ItemStack itemstack = entityplayer.getHeldItemOffhand();
            EnumHandSide enumhandside = entityplayer.getPrimaryHand().opposite();

            float zLevel = RenderAssist.zLevel;
            if (!this.configMode()) RenderAssist.zLevel = -90.0F;
            if (!this.rotated) {

                this.drawTexturedModalRect(this.posX, this.posY, 0, 0, 182, 22);
                this.drawTexturedModalRect(this.posX - 1 + entityplayer.inventory.currentItem * 20, this.posY - 1, 0, 22, 24, 22);

                if (!itemstack.isEmpty()) {
                    if (enumhandside == EnumHandSide.LEFT) {
                        this.drawTexturedModalRect(this.posX - 29, this.posY - 1, 24, 22, 29, 24);
                    } else {
                        this.drawTexturedModalRect(this.posX + 182, this.posY - 1, 53, 22, 29, 24);
                    }
                }

            } else {

                GlStateManager.pushMatrix();
                GlStateManager.translate((float)this.posX + this.getWidth(), this.posY, 0.0F);
                GlStateManager.rotate(90F, 0.0F, 0.0F, 1.0F);

                this.drawTexturedModalRect(0, 0, 0, 0, 182, 22);

                if (itemstack != null) {
                    if (enumhandside == EnumHandSide.LEFT) {
                        this.drawTexturedModalRect(-29, -1, 24, 22, 29, 24);
                    } else {
                        this.drawTexturedModalRect(182, -1, 53, 22, 29, 24);
                    }
                }

                this.mc.renderEngine.bindTexture(ROTATE_WIDGETS);
                this.drawTexturedModalRect(entityplayer.inventory.currentItem * 20 - 1, -1, 0, 0, 24, 24);

                GlStateManager.popMatrix();

            }
            if (!this.configMode()) RenderAssist.zLevel = zLevel;

            GlStateManager.enableRescaleNormal();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.enableGUIStandardItemLighting();

            for (int i = 0; i < 9; ++i) {
                int x = 91 + (!this.rotated ? this.posX - 90 + (i * 20) + 2 : this.posX - 88);
                int y = 12 + (!this.rotated ? this.posY - 6 - 3 : this.posY - 11 + (i * 20) + 2);
                this.renderHotbarItem(x, y, partialTicks, entityplayer, entityplayer.inventory.mainInventory.get(i));
            }

            if (!itemstack.isEmpty()) {
                if (!this.rotated) {
                    if (enumhandside == EnumHandSide.LEFT) {
                        this.renderHotbarItem(this.posX - 26, this.posY + 3, partialTicks, entityplayer, itemstack);
                    } else {
                        this.renderHotbarItem(this.posX + 182 + 10, this.posY + 3, partialTicks, entityplayer, itemstack);
                    }
                } else {
                    if (enumhandside == EnumHandSide.LEFT) {
                        this.renderHotbarItem(this.posX + 3, this.posY - 26, partialTicks, entityplayer, itemstack);
                    } else {
                        this.renderHotbarItem(this.posX + 3, this.posY + 182 + 10, partialTicks, entityplayer, itemstack);
                    }
                }
            }

            if (this.mc.gameSettings.attackIndicator == 2) {
                float f1 = this.mc.player.getCooledAttackStrength(0.0F);
                if (f1 < 1.0F) {

                    int x;
                    int y;
                    if (!this.rotated) {
                        x = this.posX + (enumhandside == EnumHandSide.RIGHT ? -22 : this.getWidth() + 6);
                        y = this.posY + 3;
                    } else {
                        x = this.posX + 3;
                        y = this.posY + (enumhandside == EnumHandSide.RIGHT ? -22 : this.getHeight() + 6);
                    }

                    this.mc.getTextureManager().bindTexture(Gui.ICONS);
                    int k1 = (int)(f1 * 19.0F);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    this.drawTexturedModalRect(x, y, 0, 94, 18, 18);
                    this.drawTexturedModalRect(x, y + 18 - k1, 18, 112 - k1, 18, k1);
                }
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }
    }

    private void renderHotbarItem(int posX, int posY, float partialTicks, EntityPlayer player, ItemStack stack) {
        if (!stack.isEmpty()) {
            float f = (float)stack.getAnimationsToGo() - partialTicks;
            if (f > 0.0F) {
                GlStateManager.pushMatrix();
                float f1 = 1.0F + f / 5.0F;
                GlStateManager.translate((float)(posX + 8), (float)(posY + 12), 0.0F);
                GlStateManager.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                GlStateManager.translate((float)(-(posX + 8)), (float)(-(posY + 12)), 0.0F);
            }
            this.mc.getRenderItem().renderItemAndEffectIntoGUI(player, stack, posX, posY);
            if (f > 0.0F) {
                GlStateManager.popMatrix();
            }
            this.mc.getRenderItem().renderItemOverlays(this.mc.fontRenderer, stack, posX, posY);
        }
    }

}
