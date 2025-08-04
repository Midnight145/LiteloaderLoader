package com.midnight.liteloaderloader.core;

import java.nio.file.Paths;
import java.util.Map;

import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
public class LiteloaderLoader implements IFMLLoadingPlugin {

    public static Logger LOG = LogManager.getLogger("LiteloaderLoader");

    public static boolean handlePacketSubclasses = false;

    public LiteloaderLoader() {
        Configuration config = new Configuration(
            Paths.get("config", "liteloaderloader.cfg")
                .toFile());
        handlePacketSubclasses = config.getBoolean(
            "handlePacketSubclasses",
            "liteloaderloader",
            true,
            "Enable handling of packet subclasses. This might cause issues with mods, but it's unlikely.");

        if (config.hasChanged()) {
            config.save();
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "com.midnight.liteloaderloader.core.LiteloaderTransformer" };
    }

    @Override
    public String getModContainerClass() {
        return "";
    }

    @Override
    public String getSetupClass() {
        return "";
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return "";
    }
}
