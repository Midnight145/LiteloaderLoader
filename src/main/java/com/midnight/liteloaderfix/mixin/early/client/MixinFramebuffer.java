package com.midnight.liteloaderfix.mixin.early.client;

import com.midnight.liteloaderfix.lib.R;
import com.mumfrey.liteloader.core.event.EventProxy;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import net.minecraft.client.shader.Framebuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Framebuffer.class)
public class MixinFramebuffer {
    @Inject(method = "framebufferRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/shader/Framebuffer;bindFramebufferTexture()V", shift = At.Shift.BEFORE))
    private void fl$throwEvent(int p_147615_1_, int p_147615_2_, CallbackInfo ci) {
        R.of(EventProxy.class).call("$event00009", new EventInfo("renderfbo", this, false), p_147615_1_, p_147615_2_);
    }

}
