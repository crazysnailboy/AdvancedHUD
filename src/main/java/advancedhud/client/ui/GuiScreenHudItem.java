package advancedhud.client.ui;

import java.io.IOException;
import org.lwjgl.input.Keyboard;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.client.huditems.HudItemCrosshairs;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenHudItem extends GuiScreen {

    private HudItem hudItem;
    private GuiScreen parentScreen;

    public GuiScreenHudItem(GuiScreen parentScreen, HudItem hudItem) {
        this.hudItem = hudItem;
        this.parentScreen = parentScreen;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(-1, HUDRegistry.screenWidth - 30, 10, 20, 20, "X"));
        if (this.hudItem.canRotate()) {
            this.buttonList.add(new GuiButton(100, HUDRegistry.screenWidth / 2 - 50, HUDRegistry.screenHeight / 2 - 10, 100, 20, I18n.format("advancedhud.huditems.rotate")));
        }
        if (this.hudItem instanceof HudItemCrosshairs) {
            HudItemCrosshairs cross = (HudItemCrosshairs)this.hudItem;
            this.buttonList.add(new GuiButtonIconGrid(3320, HUDRegistry.screenWidth / 2 - 128, 40, cross, I18n.format("advancedhud.huditems.crosshairselector")));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        if (!HUDRegistry.checkForResize()) {
            this.initGui();
        }

        this.drawCenteredString(this.mc.fontRendererObj, this.hudItem.getButtonLabel(), HUDRegistry.screenWidth / 2, 10, 0xFFFFFF);
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
            this.hudItem.rotate();
        }
        super.actionPerformed(button);
    }

}
