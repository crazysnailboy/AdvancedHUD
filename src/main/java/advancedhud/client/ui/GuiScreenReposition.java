package advancedhud.client.ui;

import java.io.IOException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import advancedhud.SaveController;
import advancedhud.api.Alignment;
import advancedhud.api.HudItem;
import advancedhud.client.GuiAdvancedHUD;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenReposition extends GuiScreen {
    protected GuiScreen parentScreen;
    protected HudItem hudItem;
    protected boolean axisAlign;
    protected int oldPosX;
    protected int oldPosY;
    private static boolean help = true;

    public GuiScreenReposition(GuiScreen parentScreen, HudItem hudItem) {
        this.parentScreen = parentScreen;
        this.hudItem = hudItem;
        this.oldPosX = hudItem.posX;
        this.oldPosY = hudItem.posY;
    }

    @Override
    public void handleMouseInput() throws IOException {
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        this.hudItem.posX = mouseX - this.hudItem.getWidth() / 2;
        this.hudItem.posY = mouseY - this.hudItem.getHeight() / 2;

        if (this.axisAlign) {
            if (this.hudItem.posX > this.oldPosX - 5 && this.hudItem.posX < this.oldPosX + 5) {
                this.hudItem.posX = this.oldPosX;
            }
            if (this.hudItem.posY > this.oldPosY - 5 && this.hudItem.posY < this.oldPosY + 5) {
                this.hudItem.posY = this.oldPosY;
            }
        }

        this.hudItem.fixBounds();

        super.handleMouseInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        if (help) {
            this.drawCenteredString(this.mc.fontRenderer, I18n.format("advancedhud.reposition.controls"), this.width / 2, 16, 0xFFFFFF);
            this.drawCenteredString(this.mc.fontRenderer, I18n.format("advancedhud.reposition.alignment", Alignment.calculateAlignment(mouseX, mouseY)), this.width / 2, 26, 0xFFFFFF);
        }

        drawRect(this.hudItem.posX, this.hudItem.posY, this.hudItem.posX + this.hudItem.getWidth(), this.hudItem.posY + this.hudItem.getHeight(), 0x22FFFFFF);
        this.hudItem.render(GuiAdvancedHUD.partialTicks);

        if (this.axisAlign) {
            int x = this.oldPosX + this.hudItem.getWidth() / 2;
            int y = this.oldPosY + this.hudItem.getHeight() / 2;
            drawRect(x - 1, 0, x + 1, this.height, 0x40000000);
            drawRect(0, y - 1, this.width, y + 1, 0x40000000);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            this.hudItem.alignment = Alignment.calculateAlignment(mouseX, mouseY);
            this.mc.displayGuiScreen(this.parentScreen);
            SaveController.saveConfig("config");
        }
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        super.handleKeyboardInput();

        if (Keyboard.getEventKey() == 29) {
            this.axisAlign = Keyboard.getEventKeyState();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            this.hudItem.posX = this.oldPosX;
            this.hudItem.posY = this.oldPosY;
            this.mc.displayGuiScreen(this.parentScreen);
            SaveController.saveConfig("config");
        } else if (keyCode == 19) {
            //hudItem.rotated = false;
            this.hudItem.posX = this.hudItem.getDefaultPosX();
            this.hudItem.posY = this.hudItem.getDefaultPosY();
            this.hudItem.alignment = this.hudItem.getDefaultAlignment();
            this.hudItem.fixBounds();
            this.mc.displayGuiScreen(this.parentScreen);
            SaveController.saveConfig("config");
        }
    }
}