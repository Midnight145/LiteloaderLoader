package com.midnight.liteloaderloader.mixin.early.client;

import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.WorldSettings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mumfrey.liteloader.client.CallbackProxyClient;
import com.mumfrey.liteloader.transformers.event.EventInfo;

@Mixin(IntegratedServer.class)
public class MixinIntegratedServer {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(Minecraft instance, String folder, String save, WorldSettings settings, CallbackInfo ci) {
        CallbackProxyClient.IntegratedServerCtor(
            new EventInfo("oncreateintegratedserver", this, false),
            instance,
            folder,
            save,
            settings);
    }
}
