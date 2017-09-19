package advancedhud.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Register your HUD elements with this. You can register them at init, postinit, world load, really where ever you feel the need to.
 * @author maxpowa
 *
 */
public class HUDRegistry {
    private static Map<String,HudItem> hudItemList = new HashMap<String,HudItem>();
    private static boolean initialLoadComplete = false;
    private static Map<String,HudItem> hudItemListActive = new HashMap<String,HudItem>();

    private static Logger log = LogManager.getLogger("AdvancedHUD-API");
    public static int screenWidth;
    public static int screenHeight;
    public static int updateCounter;

    public static void registerHudItem(HudItem hudItem) {
        if (hudItem.getDefaultID() <= 25 && initialLoadComplete) {
            log.info("Rejecting " + hudItem.getName() + " due to invalid ID.");
        }
        if (!hudItemList.containsValue(hudItem)) {
            hudItemList.put(hudItem.getName(), hudItem);
            if (hudItem.isEnabledByDefault()) {
                enableHudItem(hudItem);
            }
        }
    }

    public static Collection<HudItem> getHudItemList() {
        return hudItemList.values();
    }

    public static void enableHudItem(HudItem hudItem) {
        if (hudItemList.containsValue(hudItem) && !hudItemListActive.containsValue(hudItem)) {
            hudItemListActive.put(hudItem.getName(), hudItem);
        }
    }

    public static void disableHudItem(HudItem hudItem) {
        hudItemListActive.remove(hudItem);
    }

    public static Collection<HudItem> getActiveHudItemList() {
        return hudItemListActive.values();
    }

    public static boolean isActiveHudItem(HudItem hudItem) {
        return getActiveHudItemList().contains(hudItem);
    }

    public static HudItem getHudItemByID(int id) {
        for (HudItem huditem : getHudItemList()) {
            if (id == huditem.getDefaultID())
                return huditem;
        }
        return null;
    }

    public static HudItem getHudItemByName(String name) {
        return hudItemList.get(name);
    }

    public static void resetAllDefaults() {
        for (HudItem huditem : HUDRegistry.getHudItemList()) {
            //huditem.rotated = false;
            huditem.alignment = huditem.getDefaultAlignment();
            huditem.posX = huditem.getDefaultPosX();
            huditem.posY = huditem.getDefaultPosY();
        }
    }

    public static boolean checkForResize() {
        Minecraft mc = Minecraft.getMinecraft();
        return checkForResize(mc, new ScaledResolution(mc, mc.displayWidth, mc.displayHeight));
    }

    public static boolean checkForResize(Minecraft mc, ScaledResolution scaledresolution) {
        if (scaledresolution.getScaledWidth() != screenWidth || scaledresolution.getScaledHeight() != screenHeight) {
            if (screenWidth != 0) {
                fixHudItemOffsets(scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), screenWidth, screenHeight);
            }
            screenWidth = scaledresolution.getScaledWidth();
            screenHeight = scaledresolution.getScaledHeight();
            return true;
        }
        return false;
    }

    private static void fixHudItemOffsets(int newScreenWidth, int newScreenHeight, int oldScreenWidth, int oldScreenHeight) {
        for (HudItem hudItem : hudItemList.values()) {
            if (Alignment.isHorizontalCenter(hudItem.alignment)) {
                int offsetX = hudItem.posX - oldScreenWidth / 2;
                hudItem.posX = newScreenWidth / 2 + offsetX;
            } else if (Alignment.isRight(hudItem.alignment)) {
                int offsetX = hudItem.posX - oldScreenWidth;
                hudItem.posX = newScreenWidth + offsetX;
            }

            if (Alignment.isVerticalCenter(hudItem.alignment)) {
                int offsetY = hudItem.posY - oldScreenHeight / 2;
                hudItem.posY = newScreenHeight / 2 + offsetY;
            } else if (Alignment.isBottom(hudItem.alignment)) {
                int offsetY = hudItem.posY - oldScreenHeight;
                hudItem.posY = newScreenHeight + offsetY;
            }
        }
    }

    public static void readFromNBT(NBTTagCompound compound) {
        screenWidth = compound.getInteger("screenWidth");
        screenHeight = compound.getInteger("screenHeight");

        hudItemListActive = new HashMap<String,HudItem>(hudItemList);
        for (HudItem hudItem : hudItemList.values()) {
            if (!compound.hasKey(hudItem.getName())) {
                disableHudItem(hudItem);
            }
        }
    }

    public static void writeToNBT(NBTTagCompound compound) {
        compound.setInteger("screenWidth", screenWidth);
        compound.setInteger("screenHeight", screenHeight);

        for (HudItem hudItem : hudItemListActive.values()) {
            compound.setBoolean(hudItem.getName(), true);
        }
    }

    public static void setInitialLoadComplete(boolean value) {
        initialLoadComplete = value;
    }
}