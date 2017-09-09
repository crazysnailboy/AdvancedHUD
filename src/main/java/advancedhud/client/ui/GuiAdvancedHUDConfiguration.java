package advancedhud.client.ui;

import org.lwjgl.input.Keyboard;
import advancedhud.SaveController;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiAdvancedHUDConfiguration extends GuiScreen {

    private static boolean asMount = false;
    private static boolean help = true;

    @Override
    public void initGui() {
        super.initGui();
        this.addButtons();
    }

    @SuppressWarnings("unchecked")
    private void addButtons() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(-1, HUDRegistry.screenWidth - 30, 10, 20, 20, "X"));
        for (HudItem huditem : HUDRegistry.getHudItemList()) {
            if (asMount && huditem.shouldDrawOnMount()) {
                this.buttonList.add(new GuiHudItemButton(huditem.getDefaultID(), huditem.posX, huditem.posY, huditem.getWidth(), huditem.getHeight(), huditem.getButtonLabel()));
            } else if (!asMount && huditem.shouldDrawAsPlayer()) {
                this.buttonList.add(new GuiHudItemButton(huditem.getDefaultID(), huditem.posX, huditem.posY, huditem.getWidth(), huditem.getHeight(), huditem.getButtonLabel()));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        if (!HUDRegistry.checkForResize()) {
            this.initGui();
        }

        if (help) {
            this.drawCenteredString(this.mc.fontRendererObj, I18n.format("advancedhud.configuration.help.1"), this.width / 2, 17, 0xFFFFFF);
            this.drawCenteredString(this.mc.fontRendererObj, I18n.format("advancedhud.configuration.help.2"), this.width / 2, 27, 0xFFFFFF);
            this.drawCenteredString(this.mc.fontRendererObj, I18n.format("advancedhud.configuration.help.3", I18n.format("advancedhud.configuration.", (asMount ? "player" : "mount"))), this.width / 2, 37, 0xFFFFFF);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 19) {
            HUDRegistry.resetAllDefaults();
            this.initGui();
        } else if (keyCode == Keyboard.KEY_M) {
            asMount = !asMount;
            this.initGui();
        } else if (keyCode == Keyboard.KEY_F1) {
            help = !help;
        }
        SaveController.saveConfig("config");
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == -1) {
            this.mc.displayGuiScreen(null);
            SaveController.saveConfig("config");
        }
        if (button instanceof GuiHudItemButton) {
            HudItem hudItem = HUDRegistry.getHudItemByID(button.id);
            if (hudItem != null && hudItem.isMoveable()) {
                this.mc.displayGuiScreen(new GuiScreenReposition(this, hudItem));
            }
        }
        super.actionPerformed(button);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 1) {
            for (Object button : this.buttonList) {
                GuiButton guibutton = (GuiButton)button;

                if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
                    this.mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                    HudItem hudItem = HUDRegistry.getHudItemByID(guibutton.id);
                    if (hudItem != null) {
                        this.mc.displayGuiScreen(hudItem.getConfigScreen());
                    }
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
