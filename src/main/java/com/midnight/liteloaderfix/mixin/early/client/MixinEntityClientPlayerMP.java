package com.midnight.liteloaderfix.mixin.early.client;

import com.midnight.liteloaderfix.lib.R;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import net.minecraft.client.entity.EntityClientPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityClientPlayerMP.class)
public class MixinEntityClientPlayerMP {
    @Unique
    private static Class fl$handlerClass;
    static {
        try {
            fl$handlerClass = Class.forName("com.mumfrey.liteloader.client.event.EventProxy.2");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"))
    private void fl$sendEvent(String message, CallbackInfo ci) {
        if (fl$handlerClass != null) {
            R.of(fl$handlerClass).call("$event00011", new EventInfo("onoutboundchat", this, true), message);
        }
    }
}
