package com.midnight.liteloaderloader.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import com.midnight.liteloaderloader.Tags;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class LiteloaderLoader extends DummyModContainer implements IFMLLoadingPlugin, IEarlyMixinLoader {

    public static Logger LOG = LogManager.getLogger("LiteloaderFix");

    private static ModMetadata metadata = new ModMetadata();
    static {
        metadata.name = "LiteloaderLoader";
        metadata.version = Tags.VERSION;
        metadata.authorList.add("Midnight145");
        metadata.modId = "liteloaderloader";
    }

    public LiteloaderLoader() {
        super(metadata);
    }

    @Override
    public String getMixinConfig() {
        return "mixins.liteloaderloader.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        return new ArrayList<>();
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
