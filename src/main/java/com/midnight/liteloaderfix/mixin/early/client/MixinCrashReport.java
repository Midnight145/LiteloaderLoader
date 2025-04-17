package com.midnight.liteloaderfix.mixin.early.client;

import net.minecraft.crash.CrashReport;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mumfrey.liteloader.core.LiteLoader;

@Mixin(CrashReport.class)
public class MixinCrashReport {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectPopulateCrashReport(CallbackInfo ci) {
        LiteLoader.populateCrashReport(this);
    }
}
