package advancedhud.client.ui;

import java.io.IOException;
import org.lwjgl.input.Keyboard;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import advancedhud.SaveController;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;

public class GuiAdvancedHUDConfiguration extends GuiScreen {

    private static boolean asMount = false;
    private static boolean help = true;

    @Override
    public void initGui() {
        super.initGui();
        this.addButtons();
    }

    private void addButtons() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(-1, HUDRegistry.screenWidth - 30, 10, 20, 20, "X"));

        for (HudItem huditem : HUDRegistry.getHudItemList()) {
            if (asMount ? huditem.shouldDrawOnMount() : huditem.shouldDrawAsPlayer()) {
                this.buttonList.add(new GuiHudItemButton(huditem));
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
            this.drawCenteredString(this.mc.fontRendererObj, I18n.format("advancedhud.configuration.help.1"), this.width / 2, 40, 0xFFFFFF);
            this.drawCenteredString(this.mc.fontRendererObj, I18n.format("advancedhud.configuration.help.2"), this.width / 2, 50, 0xFFFFFF);
            this.drawCenteredString(this.mc.fontRendererObj, I18n.format("advancedhud.configuration.help.3", I18n.format(String.format("advancedhud.configuration.%1$s", (asMount ? "player" : "mount")))), this.width / 2, 60, 0xFFFFFF);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        for (GuiHudItemButton button : Lists.newArrayList(Iterables.filter(this.buttonList, GuiHudItemButton.class))) {
            if (button.getHoverState(button.isMouseOver()) == 2) {
                this.drawHoveringText(button.getTooltip(), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_R) {
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
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == -1) {
            this.mc.displayGuiScreen(null);
            SaveController.saveConfig("config");
        }
        if (button instanceof GuiHudItemButton) {
            HudItem hudItem = HUDRegistry.getHudItemByID(button.id);
            if (hudItem != null && hudItem.isMoveable()) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiScreenReposition(this, hudItem));
            }
        }
        super.actionPerformed(button);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 1) {
            for (GuiButton button : this.buttonList) {
                if (button.mousePressed(this.mc, mouseX, mouseY)) {
                    this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F)); // mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                    HudItem hudItem = HUDRegistry.getHudItemByID(button.id);
                    if (hudItem != null) {
                        Minecraft.getMinecraft().displayGuiScreen(hudItem.getConfigScreen());
                    }
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
