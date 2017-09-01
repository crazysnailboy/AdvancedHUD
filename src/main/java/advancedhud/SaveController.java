package advancedhud;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ReportedException;

public class SaveController {
    protected static final String dirName = Minecraft.getMinecraft().mcDataDir + File.separator + "config" + File.separator + "AdvancedHud";
    protected static File dir = new File(dirName);

    public static boolean loadConfig(String name) {
        return loadConfig(name, null);
    }

    public static boolean loadConfig(String name, String dirName) {
        if (dirName != null) {
            dir = new File(Minecraft.getMinecraft().mcDataDir + File.separator + dirName);
        }

        String fileName = name + ".dat";
        File file = new File(dir, fileName);

        if (!file.exists()) {
            AdvancedHUD.log.warn("Config load canceled, file does not exist. This is normal for first run.");
            return false;
        } else {
            AdvancedHUD.log.info("Config load successful.");
        }
        try {
            NBTTagCompound compound = CompressedStreamTools.readCompressed(new FileInputStream(file));

            HUDRegistry.readFromNBT(compound.getCompoundTag("global"));

            for (HudItem item : HUDRegistry.getHudItemList()) {
                NBTTagCompound itemNBT = compound.getCompoundTag(item.getName());
                item.loadFromNBT(itemNBT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void saveConfig(String name) {
        saveConfig(name, null);
    }

    public static void saveConfig(String name, String dirName) {
        AdvancedHUD.log.info("Saving...");

        if (dirName != null) {
            dir = new File(Minecraft.getMinecraft().mcDataDir + File.separator + dirName);
        }

        if (!dir.exists() && !dir.mkdirs())
            throw new ReportedException(new CrashReport("Unable to create the configuration directories", new Throwable()));

        String fileName = name + ".dat";
        File file = new File(dir, fileName);

        try {
            NBTTagCompound compound = new NBTTagCompound();
            FileOutputStream fileOutputStream = new FileOutputStream(file);

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
        } catch (IOException e) {
            throw new ReportedException(new CrashReport("An error occured while saving", new Throwable()));
        }
    }

    public static File[] getConfigs() {
        return dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".dat");
            }
        });
    }
}