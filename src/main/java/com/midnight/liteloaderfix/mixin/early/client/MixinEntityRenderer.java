package com.midnight.liteloaderfix.mixin.early.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.midnight.liteloaderfix.lib.R;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Unique
    private static R lf$handlerClass;

    static {
        try {
            lf$handlerClass = R.of(Class.forName("com.mumfrey.liteloader.core.event.EventProxy.2"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @WrapOperation(
            method = "renderWorld",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V")
    )
    private void fl$throwEventAtEndStartSection(Profiler instance, String name, Operation<Void> original) {
        switch (name) {
            case "pick":
                lf$handlerClass.call("$event0000b", new EventInfo("onrenderworld", this, false));
                break;
            case "frustrum":
                lf$handlerClass.call("$event0000c", new EventInfo("onsetupcameratransform", this, false));
                break;
            case "litParticles":
                lf$handlerClass.call("$event0000d", new EventInfo("postrenderentities", this, false));
                break;
            default:
                break;
        }
        original.call(instance, name);
    }

    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glClear(I)V"))
    private void fl$onUpdateCameraAndRender(float p_78480_1_, CallbackInfo ci) {
        lf$handlerClass.call("$event0000a", new EventInfo("prerendergui", this, false), p_78480_1_);
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V", ordinal = 0))
    private void fl$onRenderWorld(CallbackInfo ci, @Local(name="p_78471_1_") float p_78471_1_, @Local(name = "p_78471_2_") long p_78471_2_) {
        lf$handlerClass.call("$event0000e", new EventInfo("postrender", this, false), p_78471_1_, p_78471_2_);
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V", ordinal = 1))
    private void fl$onRenderWorldSecond(CallbackInfo ci, @Local(name="p_78471_1_") float p_78471_1_, @Local(name = "p_78471_2_") long p_78471_2_) {
        lf$handlerClass.call("$event0000f", new EventInfo("postrender", this, false), p_78471_1_, p_78471_2_);
    }

    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(FZII)V", shift = At.Shift.AFTER))
    private void fl$afterRenderGameOverlay(float p_78480_1_, CallbackInfo ci) {
        lf$handlerClass.call("$event0000f", new EventInfo("postrenderhud", this, false), p_78480_1_);
    }

    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(FZII)V"))
    private void fl$beforeRenderGameOverlay(float p_78480_1_, CallbackInfo ci) {
        lf$handlerClass.call("$event0000f", new EventInfo("postrenderhud", this, false), p_78480_1_);
    }

}
