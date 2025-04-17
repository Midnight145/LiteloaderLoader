package com.midnight.liteloaderloader.mixin.early.client;

import net.minecraft.client.main.Main;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mumfrey.liteloader.launch.LiteLoaderTweaker;

@Mixin(value = Main.class, remap = false)
public class MixinMain {

    @Inject(method = "main", at = @At(value = "HEAD"))
    private static void onMain(String[] p_main_0_, CallbackInfo ci) {
        LiteLoaderTweaker.preBeginGame();
    }
}
