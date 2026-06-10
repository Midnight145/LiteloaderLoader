package com.midnight.liteloaderloader.core;

import com.midnight.liteloaderloader.core.event.AngelicaEventHandler;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;

@Mod(modid = "liteloaderloader", name = "LLLMod", version = "0.2.0", acceptedMinecraftVersions = "[1.7.10]")
public class LLLMod {

    public LLLMod() {
        if (Loader.isModLoaded("angelica")) {
            new AngelicaEventHandler();
        }
    }
}
