package advancedhud;

import org.apache.logging.log4j.LogManager;
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
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = AdvancedHUD.MODID, name = AdvancedHUD.NAME, version = AdvancedHUD.VERSION)
public class AdvancedHUD {

    public final static String MODID = "advancedhud";
    public final static String VERSION = "${version}";
    public final static String NAME = "AdvancedHUD";

    public static final Logger log = LogManager.getLogger(MODID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.registerHUDItems();
    }

    @EventHandler
    public void onInit(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new TickHandler());
        FMLCommonHandler.instance().bus().register(new KeyRegister());
    }

    private void registerHUDItems() {
        HUDRegistry.registerHudItem(new HudItemHotbar());
        HUDRegistry.registerHudItem(new HudItemHealth());
        HUDRegistry.registerHudItem(new HudItemAir());
        HUDRegistry.registerHudItem(new HudItemFood());
        HUDRegistry.registerHudItem(new HudItemArmor());
        HUDRegistry.registerHudItem(new HudItemBossBar());
        HUDRegistry.registerHudItem(new HudItemJumpBar());
        HUDRegistry.registerHudItem(new HudItemHealthMount());
        HUDRegistry.registerHudItem(new HudItemExperienceBar());
        HUDRegistry.registerHudItem(new HudItemCrosshairs());
        HUDRegistry.registerHudItem(new HudItemTooltips());
        HUDRegistry.registerHudItem(new HudItemRecordDisplay());
        HUDRegistry.setInitialLoadComplete(true);
    }
}
