package com.midnight.liteloaderloader.core;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
public class LiteloaderLoader implements IFMLLoadingPlugin {

    public static Logger LOG = LogManager.getLogger("LiteloaderLoader");

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
