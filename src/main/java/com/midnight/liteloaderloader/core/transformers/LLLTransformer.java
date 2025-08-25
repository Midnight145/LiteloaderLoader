package com.midnight.liteloaderloader.core.transformers;

import static com.midnight.liteloaderloader.core.LiteloaderLoader.LOG;

import java.util.HashMap;
import java.util.function.Function;

import net.minecraft.launchwrapper.IClassTransformer;

import com.midnight.liteloaderloader.core.LiteloaderLoader;
import com.midnight.liteloaderloader.core.transformers.compat.AngelicaHUDCachingTransformer;
import com.midnight.liteloaderloader.core.transformers.compat.InputHandlerTransformer;
import com.midnight.liteloaderloader.core.transformers.compat.VoxelCommonLiteModTransformer;
import com.midnight.liteloaderloader.core.transformers.forge.FMLCommonHandlerTransformer;
import com.midnight.liteloaderloader.core.transformers.forge.GuiModListTransformer;
import com.midnight.liteloaderloader.core.transformers.loadingbar.LoadingBarTransformer;
import com.midnight.liteloaderloader.core.transformers.loadingbar.ObjectFactoryClientTransformer;

@SuppressWarnings("unused")
public class LLLTransformer implements IClassTransformer {

    // One-off transformations that we want to apply to specific classes.
    // Function should be the apply method of a ClassTransformer subclass
    private static final HashMap<String, Function<byte[], byte[]>> transformations = new HashMap<>();

    static {
        // LiteLoader's ClassTransformer uses the default implementation of COMPUTE_FRAMES, which loads classes and is
        // therefore unsafe. We replace it with our own version that doesn't load classes.
        transformations.put(
            "com.mumfrey.liteloader.transformers.ClassTransformer",
            bytes -> new ClassTransformerTransformer().apply(bytes));

        // Angelica's HUD Caching option overrides EntityRenderer.updateCameraAndRender, which is used for several
        // events. We need to apply this transformer to it to call throw events ourselves.
        transformations.put(
            "com.gtnewhorizons.angelica.hudcaching.HUDCaching",
            bytes -> new AngelicaHUDCachingTransformer().apply(bytes, LiteloaderLoader.angelicaEventCompat));

        // VoxelCommonLiteMod uses a hardcoded TEMP environment variable, which only exists on Windows.
        // We replace it with java.io.tmpdir property, which is the standard temporary directory for Java.
        transformations.put(
            "com.thevoxelbox.common.VoxelCommonLiteMod",
            bytes -> new VoxelCommonLiteModTransformer().apply(bytes, LiteloaderLoader.voxelCommonNixCompat));

        // InputHandler tries to resolve lwjgl2-only fields, which spams exceptions in the log. It mostly works either
        // way, so we just return immediately in the getBuffers method to avoid the spam.
        transformations.put(
            "net.eq2online.macros.input.InputHandler",
            bytes -> new InputHandlerTransformer().apply(bytes, LiteloaderLoader.macroKeybindModLogSpam));

        // We kill LiteLoader's progress bar because it has issues with Forge, and isn't very useful anyway. We
        // reimplement mod loading stages with ProgressManager.
        transformations.put(
            "com.mumfrey.liteloader.client.api.ObjectFactoryClient",
            bytes -> new ObjectFactoryClientTransformer().apply(bytes, LiteloaderLoader.overrideProgressBar));
        transformations.put(
            "com.mumfrey.liteloader.client.gui.startup.LoadingBar",
            bytes -> new LoadingBarTransformer().apply(bytes, LiteloaderLoader.overrideProgressBar));

        // If configured, add LiteLoader mods to the Forge mod list and add branding for LiteLoader mod count
        transformations.put(
            "cpw.mods.fml.client.GuiModList",
            bytes -> new GuiModListTransformer().apply(bytes, LiteloaderLoader.addToForgeCounts));
        transformations.put(
            "cpw.mods.fml.common.FMLCommonHandler",
            bytes -> new FMLCommonHandlerTransformer().apply(bytes, LiteloaderLoader.addToForgeCounts));
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        if (basicClass == null) return null;
        if (transformations.containsKey(transformedName)) {
            LOG.info("Applying transformation for {}", transformedName);

            basicClass = transformations.get(transformedName)
                .apply(basicClass);
        }
        return basicClass;
    }
}
