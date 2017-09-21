package advancedhud.client.ui;

import java.io.IOException;
import org.lwjgl.input.Keyboard;
import advancedhud.api.HudItem;
import advancedhud.client.huditems.HudItemCrosshairs;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenHudItem extends GuiScreen {

    private HudItem hudItem;
    private GuiScreen parentScreen;

    private GuiButton rotateButton;
    private GuiButton enableButton;

    public GuiScreenHudItem(GuiScreen parentScreen, HudItem hudItem) {
        this.hudItem = hudItem;
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(-1, this.width - 30, 10, 20, 20, "X"));

        int xPosition = (this.width / 2) - 100;
        int yPosition = (this.height / 4) + -16;

        this.buttonList.add(rotateButton = new GuiButton(100, xPosition, yPosition + 24, 200, 20, getRotationButtonText()));
        if (!this.hudItem.canRotate()) rotateButton.enabled = false;

        this.buttonList.add(enableButton = new GuiButton(101, xPosition, yPosition + 48, 200, 20, getEnabledButtonText()));

        if (this.hudItem instanceof HudItemCrosshairs) {
            HudItemCrosshairs cross = (HudItemCrosshairs)this.hudItem;
            this.buttonList.add(new GuiButtonIconGrid(3320, this.width / 2 - 128, 40, cross, I18n.format("advancedhud.huditems.crosshairselector")));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.mc.fontRendererObj, this.hudItem.getDisplayName(), this.width / 2, 10, 0xFFFFFF);
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
        if (button.id == -1) {
            this.mc.displayGuiScreen(this.parentScreen);
        } else if (button.id == 100) {
            this.hudItem.rotated = !this.hudItem.rotated;
            this.rotateButton.displayString = getRotationButtonText();
        } else if (button.id == 101) {
            this.hudItem.enabled = !this.hudItem.enabled;
            this.enableButton.displayString = getEnabledButtonText();
        }
        super.actionPerformed(button);
    }

    private String getRotationButtonText() {
        return I18n.format("advancedhud.huditems.rotation", I18n.format(!this.hudItem.rotated ? "gui.horizontal" : "gui.vertical"));
    }

    private String getEnabledButtonText() {
        return I18n.format("advancedhud.huditems.enabled", I18n.format(this.hudItem.enabled ? "gui.yes" : "gui.no"));
    }

}
