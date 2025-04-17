package com.midnight.liteloaderfix.mixin.early.client;

import com.midnight.liteloaderfix.lib.R;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @Unique
    private static R lf$handlerClass;

    static {
        try {
            lf$handlerClass = R.of(Class.forName("com.mumfrey.liteloader.core.event.EventProxy.2"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawChat(I)V"))
    private void beforeDrawChat(float p_73830_1_, boolean p_73830_2_, int p_73830_3_, int p_73830_4_, CallbackInfo ci) {
        lf$handlerClass.call("$event00013", new EventInfo("onrenderchat", this, false), p_73830_1_, p_73830_2_, p_73830_3_, p_73830_4_);
    }

    @Inject(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawChat(I)V", shift = At.Shift.AFTER))
    private void afterDrawChat(float p_73830_1_, boolean p_73830_2_, int p_73830_3_, int p_73830_4_, CallbackInfo ci) {
        lf$handlerClass.call("$event00013", new EventInfo("postrenderchat", this, false), p_73830_1_, p_73830_2_, p_73830_3_, p_73830_4_);
    }
}
