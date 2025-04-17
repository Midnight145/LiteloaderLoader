package com.midnight.liteloaderfix.mixin.early.client;

import java.util.List;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.midnight.liteloaderfix.lib.R;
import com.mumfrey.liteloader.core.event.EventProxy;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.Timer;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mumfrey.liteloader.client.overlays.IMinecraft;
import com.mumfrey.liteloader.launch.LiteLoaderTweaker;

@SuppressWarnings({"rawtypes", "unchecked"})
@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IMinecraft {

    @Shadow
    private Timer timer;

    @Shadow
    volatile boolean running;

    @Shadow
    private List defaultResourcePacks;

    @Shadow
    public abstract void resize(int width, int height);

    @Shadow
    private String serverName;

    @Shadow
    private int serverPort;

    @Override
    public Timer getTimer() {
        return this.timer;
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public List<IResourcePack> getDefaultResourcePacks() {
        return this.defaultResourcePacks;
    }

    @Override
    public void setSize(int width, int height) {
        try {
            Display.setDisplayMode(new DisplayMode(width, height));
        } catch (LWJGLException ignored) {}
        this.resize(width, height);
        Display.setVSyncEnabled(Minecraft.getMinecraft().gameSettings.enableVsync);
    }

    @Override
    public String getServerName() {
        return this.serverName;
    }

    @Override
    public int getServerPort() {
        return this.serverPort;
    }

    @Inject(
        method = "startGame",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/EntityRenderer;<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/resources/IResourceManager;)V"))
    private void initMods(CallbackInfo ci) {
        LiteLoaderTweaker.init();
        LiteLoaderTweaker.postInit();
    }

    @Inject(method = "startGame", at = @At(value = "TAIL"))
    public void fl$startGame(CallbackInfo ci) {
        EventInfo event = new EventInfo("onstartupcomplete", this, false);
        R.of(EventProxy.class).call("$event00000", event);
    }

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V", shift = At.Shift.BEFORE))
    public void fl$runGameLoop(CallbackInfo ci) {
        EventInfo event = new EventInfo("ontimerupdate", this, false);
        R.of(EventProxy.class).call("$event00003", event);
    }

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lcpw/mods/fml/common/FMLCommonHandler;onRenderTickStart(F)V"), remap = false)
    public void fl$onRenderTickStart(CallbackInfo ci) {
        EventInfo event = new EventInfo("onrender", this, false);
        R.of(EventProxy.class).call("$event00004", event);
        EventInfo event2 = new EventInfo("ontick", this, false);
        R.of(EventProxy.class).call("$event00005", event2);
    }

    @WrapOperation(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/shader/Framebuffer;framebufferRender(II)V"))
    public void fl$framebufferRender(Framebuffer instance, int f1, int f2, Operation<Void> original) {
        R.of(EventProxy.class).call("$event00001", new EventInfo<>("prerenderfbo", this, false));
        original.call(instance, f1, f2);
        R.of(EventProxy.class).call("$event00002", new EventInfo<>("postrenderfbo", this, false));
    }

    @Inject(method = "shutdown", at = @At(value = "HEAD"))
    public void fl$shutdown(CallbackInfo ci) {
        EventInfo event = new EventInfo("shutdown", this, true);
        R.of(EventProxy.class).call("$event00006", event);
    }

    @Inject(method = "updateFramebufferSize", at = @At(value = "HEAD"))
    public void fl$updateFramebufferSize(CallbackInfo ci) {
        EventInfo event = new EventInfo("updateframebuffersize", this, false);
        R.of(EventProxy.class).call("$event00007", event);
    }

    @Inject(method = "runTick", at = @At(value = "HEAD"))
    public void fl$runTick(CallbackInfo ci) {
        EventInfo event = new EventInfo("newtick", this, false);
        R.of(EventProxy.class).call("$event00008", event);
    }
}
