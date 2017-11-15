package advancedhud.client.ui;

import java.io.IOException;
import org.lwjgl.input.Keyboard;
import advancedhud.api.RenderStyle;
import advancedhud.client.huditems.HudItemCrosshairs;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenHudItemCrosshairs extends GuiScreen {

    private HudItemCrosshairs hudItem;
    private GuiScreen parentScreen;

    private GuiButton cancelButton;
    private GuiButton enableButton;
    private GuiButton styleButton;
    private GuiButtonIconGrid crosshairButton;

    public GuiScreenHudItemCrosshairs(GuiScreen parentScreen, HudItemCrosshairs hudItem) {
        this.hudItem = hudItem;
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(cancelButton = new GuiButton(-1, this.width - 30, 10, 20, 20, "X"));

        int xPosition = (this.width / 2) - 100;
        int yPosition = (this.height / 4) + -16;
        int yOffset = 0;

        this.buttonList.add(enableButton = new GuiButton(101, xPosition, yPosition + (yOffset += 24), 200, 20, getEnabledButtonText()));
        this.buttonList.add(styleButton = new GuiButton(102, xPosition, yPosition + (yOffset += 24), 200, 20, getStyleButtonText()));
        this.buttonList.add(crosshairButton = new GuiButtonIconGrid(3320, this.width / 2 - 128, yPosition + (yOffset += 24), this.hudItem));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.mc.fontRenderer, this.hudItem.getDisplayName(), this.width / 2, 10, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == this.cancelButton) {
            this.mc.displayGuiScreen(this.parentScreen);
        } else if (button == this.enableButton) {
            this.hudItem.enabled = !this.hudItem.enabled;
            this.enableButton.displayString = getEnabledButtonText();
        } else if (button == this.styleButton) {
            this.hudItem.toggleStyle();
            this.styleButton.displayString = getStyleButtonText();
            this.crosshairButton.visible = (this.hudItem.style == RenderStyle.ICON);
        }
        super.actionPerformed(button);
    }

    private String getRotationButtonText() {
        return I18n.format("advancedhud.huditems.rotation", I18n.format(!this.hudItem.rotated ? "gui.horizontal" : "gui.vertical"));
    }

    private String getEnabledButtonText() {
        return I18n.format("advancedhud.huditems.enabled", I18n.format(this.hudItem.enabled ? "gui.yes" : "gui.no"));
    }

    private String getStyleButtonText() {
        return I18n.format("advancedhud.huditems.style", I18n.format(this.hudItem.style.getUnlocalizedName()));
    }

}
