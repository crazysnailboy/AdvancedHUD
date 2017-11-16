package advancedhud.client.ui;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import org.lwjgl.input.Keyboard;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import advancedhud.ReflectionHelper;
import advancedhud.SaveController;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;

public class GuiAdvancedHUDConfiguration extends GuiScreen {

    private static final Field selectedButtonField = ReflectionHelper.getDeclaredField(GuiScreen.class, "selectedButton", "field_146290_a");

    private static boolean asMount = false;
    private static boolean help = true;
    private List<GuiHudItemButton> hudItemButtonList;

    @Override
    public void initGui() {
        super.initGui();
        HUDRegistry.checkForResize();
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

        this.hudItemButtonList = Lists.newArrayList(Iterables.filter(this.buttonList, GuiHudItemButton.class));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        if (help) {
            this.drawCenteredString(this.mc.fontRendererObj, I18n.format("advancedhud.configuration.help.1"), this.width / 2, 40, 0xFFFFFF);
            this.drawCenteredString(this.mc.fontRendererObj, I18n.format("advancedhud.configuration.help.2"), this.width / 2, 50, 0xFFFFFF);
            this.drawCenteredString(this.mc.fontRendererObj, I18n.format("advancedhud.configuration.help.3", I18n.format(String.format("advancedhud.configuration.%1$s", (asMount ? "player" : "mount")))), this.width / 2, 60, 0xFFFFFF);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        for (GuiHudItemButton button : this.hudItemButtonList) {
            if (button.getHoverState(button.isMouseOver()) == 2) {
                this.drawHoveringText(button.getTooltip(), mouseX, mouseY);
                break;
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
                this.mc.displayGuiScreen(new GuiScreenReposition(this, hudItem));
            }
        }
        super.actionPerformed(button);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (GuiButton button : this.buttonList) {
            if (button.mousePressed(this.mc, mouseX, mouseY)) {
                if (mouseButton == 0) {

                    ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(this, button, this.buttonList);
                    if (!MinecraftForge.EVENT_BUS.post(event)) {
                        this.setSelectedButton(event.getButton());
                        event.getButton().playPressSound(this.mc.getSoundHandler());

                        this.actionPerformed(event.getButton());

                        if (this.equals(this.mc.currentScreen)) {
                            MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(this, event.getButton(), this.buttonList));
                        }
                    }

                } else if (mouseButton == 1) {

                    HudItem hudItem = HUDRegistry.getHudItemByID(button.id);
                    if (hudItem != null) {
                        button.playPressSound(this.mc.getSoundHandler());
                        this.mc.displayGuiScreen(hudItem.getConfigScreen());
                    }
                }
                break;
            }
        }
    }

    private void setSelectedButton(GuiButton button) {
        ReflectionHelper.setFieldValue(selectedButtonField, this, button);
    }

}
