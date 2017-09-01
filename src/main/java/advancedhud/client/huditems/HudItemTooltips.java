package advancedhud.client.huditems;

import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.client.ui.GuiAdvancedHUDConfiguration;
import advancedhud.client.ui.GuiScreenHudItem;
import advancedhud.client.ui.GuiScreenReposition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

public class HudItemTooltips extends HudItem {

    private String itemName;
    private String itemRarityColorCode;
    private int stringColor;
    private float alpha;
    private int updateCounter;
    private boolean resetFadeTimer;

    @Override
    public String getButtonLabel() {
        return "TOOLTIP";
    }

    @Override
    public String getName() {
        return "itemtooltip";
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
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.currentScreen instanceof GuiAdvancedHUDConfiguration || mc.currentScreen instanceof GuiScreenReposition) {
            this.itemName = "TOOLTIP";
        }

        if (this.itemName != null && !this.itemName.isEmpty()) {
            FontRenderer fontrenderer = mc.fontRendererObj;
            int posX;
            if (Alignment.isLeft(this.alignment)) {
                posX = this.posX;
            } else if (Alignment.isHorizontalCenter(this.alignment)) {
                posX = this.posX + (this.getWidth() - fontrenderer.getStringWidth(this.itemName)) / 2;
            } else {
                posX = this.posX + this.getWidth() - fontrenderer.getStringWidth(this.itemName);
            }

            fontrenderer.drawStringWithShadow(this.itemRarityColorCode + this.itemName, posX, this.posY, this.stringColor);
        }
    }

    @Override
    public void tick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            ItemStack currentItem = mc.thePlayer.inventory.getCurrentItem();
            String currentName = currentItem == null ? "" : currentItem.getDisplayName();

            this.resetFadeTimer = !currentName.equals(this.itemName);
            this.itemName = currentName;

            if (currentItem != null) {
                this.itemRarityColorCode = currentItem.getRarity().rarityColor.toString();
                this.stringColor = 0xFFFFFF;
            }
        }

        if (this.resetFadeTimer) {
            this.alpha = 1.0F;

            int fadeSpeed = 8 * 20;

            this.updateCounter = HUDRegistry.updateCounter + fadeSpeed;
            this.resetFadeTimer = false;
        } else {
            this.alpha = (this.updateCounter - HUDRegistry.updateCounter) / 20.0F;
            this.alpha = Math.min(Math.max(this.alpha, 0.0F), 1.0F);
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
        return new GuiScreenHudItem(Minecraft.getMinecraft().currentScreen, this);
    }

    @Override
    public boolean canRotate() {
        return false;
    }

}
