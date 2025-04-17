package com.midnight.liteloaderfix.core;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class LiteloaderFixCore implements IFMLLoadingPlugin, IEarlyMixinLoader {

    public LiteloaderFixCore() {
        // todo: unset final mods on all Block, Item fields
    }

    @Override
    public String getMixinConfig() {
        return "";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        return List.of();
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
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
