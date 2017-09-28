package advancedhud.client.huditems;

import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
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
    public int getDefaultID() {
        return 10;
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
    public boolean canRotate() {
        return false;
    }

    @Override
    public boolean shouldDrawOnMount() {
        return true;
    }

    @Override
    public boolean needsTick() {
        return true;
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
    public void render(float partialTicks) {

        if (!(enabled || configMode())) return;

        boolean renderTooltip = false;
        String tooltipText = null;
        int opacity = 255;

        if (configMode()) {
            renderTooltip = true;
            tooltipText = this.getDisplayName();
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
                posX = this.posX + (this.getWidth() - this.mc.fontRenderer.getStringWidth(tooltipText)) / 2;
            } else {
                posX = this.posX + this.getWidth() - this.mc.fontRenderer.getStringWidth(tooltipText);
            }

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);

            this.mc.fontRenderer.drawStringWithShadow(tooltipText, posX, this.posY, 0xFFFFFF | (opacity << 24));

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

}
