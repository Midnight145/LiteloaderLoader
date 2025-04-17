package com.midnight.liteloaderloader.mixin.early.client;

import net.minecraft.client.entity.EntityClientPlayerMP;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mumfrey.liteloader.client.CallbackProxyClient;
import com.mumfrey.liteloader.transformers.event.EventInfo;

@Mixin(EntityClientPlayerMP.class)
public class MixinEntityClientPlayerMP {

    @Inject(method = "sendChatMessage", at = @At("HEAD"))
    private void fl$sendEvent(String message, CallbackInfo ci) {
        CallbackProxyClient.onOutboundChat(new EventInfo("onoutboundchat", this, true), message);
    }
}
