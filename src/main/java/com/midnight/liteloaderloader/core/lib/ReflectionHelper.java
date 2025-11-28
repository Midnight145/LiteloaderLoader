package com.midnight.liteloaderloader.core.lib;

import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.LiteLoaderMods;

import cpw.mods.fml.common.ProgressManager;

public class ReflectionHelper {

    public static LiteLoaderMods getLiteLoaderMods() {
        return CachedR.of(LiteLoader.getInstance())
            .get("mods", LiteLoaderMods.class);
    }

    @SuppressWarnings("deprecation")
    public static void setProgressBarSteps(ProgressManager.ProgressBar bar, int steps) {
        CachedR.of(bar)
            .set("steps", steps);
    }
}
