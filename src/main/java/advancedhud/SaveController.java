package advancedhud;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;

public class SaveController {

    private static final File saveFolder = new File(Loader.instance().getConfigDir(), AdvancedHUD.MODID);

    public static boolean loadConfig(String fileName) {

        File file = new File(saveFolder, fileName + ".dat");
        if (!file.exists()) {
            AdvancedHUD.log.warn("Config load canceled, file does not exist. This is normal for first run.");
            return false;
        }

        try {

            NBTTagCompound compound = CompressedStreamTools.readCompressed(new FileInputStream(file));
            HUDRegistry.readFromNBT(compound.getCompoundTag("global"));

            for (HudItem item : HUDRegistry.getHudItemList()) {
                NBTTagCompound itemNBT = compound.getCompoundTag(item.getName());
                item.loadFromNBT(itemNBT);
            }

            AdvancedHUD.log.info("Config load successful.");
            return true;

        } catch (IOException e) {
            AdvancedHUD.log.catching(e);
            return false;
        }
    }

    public static void saveConfig(String fileName) {
        try {

            if (!saveFolder.exists()) saveFolder.mkdirs();

            File file = new File(saveFolder, fileName + ".dat");
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            NBTTagCompound compound = new NBTTagCompound();
            NBTTagCompound globalNBT = new NBTTagCompound();
            HUDRegistry.writeToNBT(globalNBT);
            compound.setTag("global", globalNBT);

            for (HudItem item : HUDRegistry.getHudItemList()) {
                NBTTagCompound itemNBT = new NBTTagCompound();
                item.saveToNBT(itemNBT);
                compound.setTag(item.getName(), itemNBT);
            }

            CompressedStreamTools.writeCompressed(compound, fileOutputStream);
            fileOutputStream.close();

            AdvancedHUD.log.info("Config save successful.");

        } catch (IOException e) {
            AdvancedHUD.log.catching(e);
        }
    }

}