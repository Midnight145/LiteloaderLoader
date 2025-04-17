package com.midnight.liteloaderfix.mixin.early.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.midnight.liteloaderfix.lib.R;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.WorldSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IntegratedServer.class)
public class MixinIntegratedServer {

    @Unique
    private static R lf$handlerClass;

    static {
        try {
            lf$handlerClass = R.of(Class.forName("com.mumfrey.liteloader.core.event.EventProxy.2"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci, @Local(ordinal = 0, argsOnly = true) Minecraft instance, @Local(ordinal = 0, argsOnly = true) String folder, @Local(ordinal = 1, argsOnly = true) String save, @Local(ordinal = 0, argsOnly = true) WorldSettings settings) {
        lf$handlerClass.call("$event00012", new EventInfo("oncreateintegratedserver", this, false), instance, folder, save, settings);
    }
}
