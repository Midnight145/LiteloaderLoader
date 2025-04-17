package com.midnight.liteloaderloader.mixin.early.client;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.profiler.Profiler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mumfrey.liteloader.client.CallbackProxyClient;
import com.mumfrey.liteloader.transformers.event.EventInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @WrapOperation(
        method = "renderWorld",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V"))
    private void fl$throwEventAtEndStartSection(Profiler instance, String name, Operation<Void> original,
        float p_78471_1_, long p_78471_2_) {
        switch (name) {
            case "pick":
                CallbackProxyClient.onRenderWorld(new EventInfo("onrenderworld", this, false), p_78471_1_, p_78471_2_);
                break;
            case "frustrum":
                CallbackProxyClient.onSetupCameraTransform(
                    new EventInfo("onsetupcameratransform", this, false),
                    p_78471_1_,
                    p_78471_2_);;
                break;
            case "litParticles":
                CallbackProxyClient
                    .postRenderEntities(new EventInfo("postrenderentities", this, false), p_78471_1_, p_78471_2_);;
                break;
            default:
                break;
        }
        original.call(instance, name);
    }

    @Inject(
        method = "updateCameraAndRender",
        at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glClear(I)V", remap = false))
    private void fl$onUpdateCameraAndRender(float p_78480_1_, CallbackInfo ci) {
        CallbackProxyClient.preRenderGUI(new EventInfo("prerendergui", this, false), p_78480_1_);
    }

    @Inject(
        method = "renderWorld",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V", ordinal = 0))
    private void fl$onRenderWorld(float p_78471_1_, long p_78471_2_, CallbackInfo ci) {
        CallbackProxyClient.postRender(new EventInfo("postrender", this, false), p_78471_1_, p_78471_2_);
    }

    @Inject(
        method = "renderWorld",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V", ordinal = 1))
    private void fl$onRenderWorldSecond(float p_78471_1_, long p_78471_2_, CallbackInfo ci) {
        CallbackProxyClient.postRender(new EventInfo("postrender", this, false), p_78471_1_, p_78471_2_);
    }

    @Inject(
        method = "updateCameraAndRender",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(FZII)V",
            shift = At.Shift.AFTER))
    private void fl$afterRenderGameOverlay(float p_78480_1_, CallbackInfo ci) {
        CallbackProxyClient.postRenderHUD(new EventInfo("postrenderhud", this, false), p_78480_1_);
    }

    @Inject(
        method = "updateCameraAndRender",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(FZII)V"))
    private void fl$beforeRenderGameOverlay(float p_78480_1_, CallbackInfo ci) {
        CallbackProxyClient.postRenderHUD(new EventInfo("prerenderhud", this, false), p_78480_1_);
    }

}
