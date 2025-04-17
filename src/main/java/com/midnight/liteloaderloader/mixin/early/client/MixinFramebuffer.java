package com.midnight.liteloaderloader.mixin.early.client;

import net.minecraft.client.shader.Framebuffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mumfrey.liteloader.client.CallbackProxyClient;
import com.mumfrey.liteloader.transformers.event.EventInfo;

@Mixin(Framebuffer.class)
public class MixinFramebuffer {

    @Inject(
        method = "framebufferRender",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/shader/Framebuffer;bindFramebufferTexture()V"))
    private void fl$throwEvent(int p_147615_1_, int p_147615_2_, CallbackInfo ci) {
        CallbackProxyClient.renderFBO(new EventInfo("renderfbo", this, false), p_147615_1_, p_147615_2_);
    }

}
