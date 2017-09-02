package advancedhud;

import org.apache.logging.log4j.Logger;
import advancedhud.api.HUDRegistry;
import advancedhud.client.huditems.HudItemAir;
import advancedhud.client.huditems.HudItemArmor;
import advancedhud.client.huditems.HudItemBossBar;
import advancedhud.client.huditems.HudItemCrosshairs;
import advancedhud.client.huditems.HudItemExperienceBar;
import advancedhud.client.huditems.HudItemFood;
import advancedhud.client.huditems.HudItemHealth;
import advancedhud.client.huditems.HudItemHealthMount;
import advancedhud.client.huditems.HudItemHotbar;
import advancedhud.client.huditems.HudItemJumpBar;
import advancedhud.client.huditems.HudItemRecordDisplay;
import advancedhud.client.huditems.HudItemTooltips;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = AdvancedHUD.MODID, name = AdvancedHUD.NAME, version = AdvancedHUD.VERSION)
public class AdvancedHUD {

    public final static String MODID = "advancedhud";
    public final static String VERSION = "${version}";
    public final static String NAME = "AdvancedHUD";

    public static Logger log;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log = event.getModLog();
    }

    @EventHandler
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new TickHandler());
        MinecraftForge.EVENT_BUS.register(new KeyRegister());

        this.registerHUDItems();
    }

    private void registerHUDItems() {
        HUDRegistry.registerHudItem(new HudItemHotbar());
        HUDRegistry.registerHudItem(new HudItemCrosshairs());
        HUDRegistry.registerHudItem(new HudItemBossBar());
        HUDRegistry.registerHudItem(new HudItemHealth());
        HUDRegistry.registerHudItem(new HudItemArmor());
        HUDRegistry.registerHudItem(new HudItemFood());
        HUDRegistry.registerHudItem(new HudItemHealthMount());
        HUDRegistry.registerHudItem(new HudItemAir());
        HUDRegistry.registerHudItem(new HudItemJumpBar());
        HUDRegistry.registerHudItem(new HudItemExperienceBar());
        HUDRegistry.registerHudItem(new HudItemTooltips());
        HUDRegistry.registerHudItem(new HudItemRecordDisplay());
        HUDRegistry.setInitialLoadComplete(true);
    }
}
