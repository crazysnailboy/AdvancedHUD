package advancedhud.client.huditems;

import org.lwjgl.opengl.GL11;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.client.ui.GuiAdvancedHUDConfiguration;
import advancedhud.client.ui.GuiScreenHudItem;
import advancedhud.client.ui.GuiScreenReposition;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class HudItemTooltips extends HudItem {

    private int remainingHighlightTicks;
    private ItemStack highlightingItemStack;

    @Override
    public String getName() {
        return "itemtooltip";
    }

    @Override
    public String getButtonLabel() {
        return I18n.format("advancedhud.item.itemtooltip.name");
    }

    @Override
    public Alignment getDefaultAlignment() {
        return Alignment.BOTTOMCENTER;
    }

    @Override
    public int getDefaultPosX() {
        return (HUDRegistry.screenWidth - this.getWidth()) / 2;
    }

    @Override
    public int getDefaultPosY() {
        return HUDRegistry.screenHeight - 59;
    }

    @Override
    public int getWidth() {
        return 100;
    }

    @Override
    public int getHeight() {
        return 8;
    }

    @Override
    public int getDefaultID() {
        return 10;
    }

    @Override
    public void render(float partialTicks) {

        mc.mcProfiler.startSection("toolHighlight");

        boolean renderTooltip = false;
        String tooltipText = null;
        int opacity = 255;

        if (this.mc.currentScreen instanceof GuiAdvancedHUDConfiguration || this.mc.currentScreen instanceof GuiScreenReposition) {
            renderTooltip = true;
            tooltipText = this.getButtonLabel();
        } else if (this.mc.gameSettings.heldItemTooltips) {
            if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null) {
                renderTooltip = true;
                tooltipText = this.highlightingItemStack.getDisplayName();
                if (this.highlightingItemStack.hasDisplayName()) tooltipText = TextFormatting.ITALIC + tooltipText;
                tooltipText = this.highlightingItemStack.getItem().getHighlightTip(this.highlightingItemStack, tooltipText);
                opacity = (int)((float)this.remainingHighlightTicks * 256.0F / 10.0F);
                if (opacity > 255) opacity = 255;
            }
        }

        if (renderTooltip && opacity > 0) {
            int posX;
            if (Alignment.isLeft(this.alignment)) {
                posX = this.posX;
            } else if (Alignment.isHorizontalCenter(this.alignment)) {
                posX = this.posX + (this.getWidth() - this.mc.fontRendererObj.getStringWidth(tooltipText)) / 2;
            } else {
                posX = this.posX + this.getWidth() - this.mc.fontRendererObj.getStringWidth(tooltipText);
            }

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

            this.mc.fontRendererObj.drawStringWithShadow(tooltipText, posX, this.posY, 0xFFFFFF | (opacity << 24));

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }

        mc.mcProfiler.endSection();
    }

    @Override
    public void tick() {
        if (this.mc.player != null) {
            ItemStack itemstack = this.mc.player.inventory.getCurrentItem();
            if (itemstack == null) {
                this.remainingHighlightTicks = 0;
            } else if (this.highlightingItemStack != null && itemstack.getItem() == this.highlightingItemStack.getItem() && ItemStack.areItemStackTagsEqual(itemstack, this.highlightingItemStack) && (itemstack.isItemStackDamageable() || itemstack.getMetadata() == this.highlightingItemStack.getMetadata())) {
                if (this.remainingHighlightTicks > 0) {
                    --this.remainingHighlightTicks;
                }
            } else {
                this.remainingHighlightTicks = 40;
            }
            this.highlightingItemStack = itemstack;
        }
    }

    @Override
    public boolean needsTick() {
        return true;
    }

    @Override
    public boolean shouldDrawOnMount() {
        return true;
    }

    @Override
    public GuiScreen getConfigScreen() {
        return new GuiScreenHudItem(this.mc.currentScreen, this);
    }

    @Override
    public boolean canRotate() {
        return false;
    }

}
