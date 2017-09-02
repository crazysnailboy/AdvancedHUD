package advancedhud;

import org.lwjgl.input.Keyboard;
import advancedhud.client.ui.GuiAdvancedHUDConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeyRegister {

    public static KeyBinding config = new KeyBinding(I18n.format("advancedhud.key.config"), Keyboard.KEY_H, "key.categories.misc");

    static {
        ClientRegistry.registerKeyBinding(config);
    }

    @SubscribeEvent
    public void KeyInputEvent(KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (config.isPressed() && mc.currentScreen == null) {
            mc.displayGuiScreen(new GuiAdvancedHUDConfiguration());
        }
    }
}
