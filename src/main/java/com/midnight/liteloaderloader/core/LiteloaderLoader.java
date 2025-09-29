package com.midnight.liteloaderloader.core;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;

import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
public class LiteloaderLoader implements IFMLLoadingPlugin {

    public static boolean overrideProgressBar = true;
    public static boolean angelicaEventCompat = true;
    public static boolean voxelCommonNixCompat = true;
    public static boolean macroKeybindModLogSpam = true;
    public static boolean voxelMapKeyRepeatFix = true;
    public static boolean addToForgeCounts = false;

    public static Logger LOG = LogManager.getLogger("LiteloaderLoader");

    public LiteloaderLoader() {
        String configPath = Paths.get("config", "liteloaderloader.cfg")
            .toString();
        File file = new File(configPath);
        Configuration config = new Configuration(file);

        overrideProgressBar = config.getBoolean(
            "overrideProgressBar",
            Configuration.CATEGORY_GENERAL,
            true,
            "If true, LiteLoader's progress bar will be replaced with a custom implementation that works better with Forge.");
        angelicaEventCompat = config.getBoolean(
            "angelicaEventCompat",
            Configuration.CATEGORY_GENERAL,
            true,
            "Fixes Angelica's HUD Caching module to ensure some events are fired correctly. This is required for VoxelMap and possibly other mods.");
        voxelCommonNixCompat = config.getBoolean(
            "voxelCommonNixCompat",
            Configuration.CATEGORY_GENERAL,
            true,
            "Patches VoxelCommonLiteMod to fix issues on non-Windows OSes involving the TEMP environment variable.");
        macroKeybindModLogSpam = config.getBoolean(
            "macroKeybindModLogSpam",
            Configuration.CATEGORY_GENERAL,
            true,
            "Patches Macro Keybind Mod to reduce log spam due to missing fields in lwjgl3.");
        voxelMapKeyRepeatFix = config.getBoolean(
            "voxelMapKeyRepeatFix",
            Configuration.CATEGORY_GENERAL,
            true,
            "Patches Voxel Map Mod to make the hotkeys running well in lwjgl3.");
        addToForgeCounts = config.getBoolean(
            "addToModList",
            Configuration.CATEGORY_GENERAL,
            true,
            "If true, LiteLoader mods will be added to both the ingame Forge modlist and mod counts on the main menu.");

        try {
            Mouse.class.getDeclaredField("readBuffer");
            Keyboard.class.getDeclaredField("readBuffer");
            macroKeybindModLogSpam = false;
            voxelMapKeyRepeatFix = false;
        } catch (NoSuchFieldException ignored) {}
        if (config.hasChanged()) {
            config.save();
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "com.midnight.liteloaderloader.core.transformers.LLLTransformer" };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
