package com.midnight.liteloaderloader.mixin.early.client;

import net.minecraft.client.gui.GuiIngame;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mumfrey.liteloader.client.CallbackProxyClient;
import com.mumfrey.liteloader.transformers.event.EventInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @Inject(
        method = "renderGameOverlay",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawChat(I)V"))
    private void beforeDrawChat(float p_73830_1_, boolean p_73830_2_, int p_73830_3_, int p_73830_4_, CallbackInfo ci) {
        CallbackProxyClient
            .onRenderChat(new EventInfo("onrenderchat", this, false), p_73830_1_, p_73830_2_, p_73830_3_, p_73830_4_);
    }

    @Inject(
        method = "renderGameOverlay",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiNewChat;drawChat(I)V",
            shift = At.Shift.AFTER))
    private void afterDrawChat(float p_73830_1_, boolean p_73830_2_, int p_73830_3_, int p_73830_4_, CallbackInfo ci) {
        CallbackProxyClient.postRenderChat(
            new EventInfo("postrenderchat", this, false),
            p_73830_1_,
            p_73830_2_,
            p_73830_3_,
            p_73830_4_);
    }
}
