package advancedhud;

import advancedhud.client.GuiAdvancedHUD;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import net.minecraft.client.Minecraft;

public class TickHandler {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final TickHandler handler = new TickHandler();

    private boolean guiLoaded = false;


    public static void register() {
        FMLCommonHandler.instance().bus().register(handler);
    }

    private static void unregister() {
        FMLCommonHandler.instance().bus().unregister(handler);
    }


    @SubscribeEvent
    public void RenderTickEvent(RenderTickEvent event) {
        if ((event.type == Type.RENDER || event.type == Type.CLIENT) && event.phase == Phase.END) {

            if (!this.guiLoaded && mc.ingameGUI != null) {
                mc.ingameGUI = new GuiAdvancedHUD(mc);
                this.guiLoaded = true;
            }

            if (guiLoaded) {
                unregister();
            }
        }
    }

}
