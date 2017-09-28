package advancedhud;

import advancedhud.client.GuiAdvancedHUD;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;

public class TickHandler {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final TickHandler handler = new TickHandler();

    private boolean guiLoaded = false;


    public static void register() {
        MinecraftForge.EVENT_BUS.register(handler);
    }

    private static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(handler);
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
