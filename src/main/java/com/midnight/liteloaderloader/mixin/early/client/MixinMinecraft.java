package com.midnight.liteloaderloader.mixin.early.client;

import java.util.List;

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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mumfrey.liteloader.client.CallbackProxyClient;
import com.mumfrey.liteloader.client.overlays.IMinecraft;
import com.mumfrey.liteloader.launch.LiteLoaderTweaker;
import com.mumfrey.liteloader.transformers.event.EventInfo;

import cpw.mods.fml.common.ProgressManager;

@SuppressWarnings({ "rawtypes", "unchecked" })
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
        ProgressManager.ProgressBar progressBar = ProgressManager.push("LiteLoader Mod Initialization", 1);
        LiteLoaderTweaker.init();
        LiteLoaderTweaker.postInit();
        progressBar.step("LiteLoader Mod Initialization Complete");
        ProgressManager.pop(progressBar);
    }

    @Inject(method = "startGame", at = @At(value = "TAIL"))
    public void fl$startGame(CallbackInfo ci) {
        CallbackProxyClient.onStartupComplete(new EventInfo("onstartupcomplete", this, false));
    }

    @Inject(
        method = "runGameLoop",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V"))
    public void fl$runGameLoop(CallbackInfo ci) {
        CallbackProxyClient.onTimerUpdate(new EventInfo("ontimerupdate", this, false));
    }

    @Inject(
        method = "runGameLoop",
        at = @At(value = "INVOKE", target = "Lcpw/mods/fml/common/FMLCommonHandler;onRenderTickStart(F)V"),
        remap = false)
    public void fl$onRenderTickStart(CallbackInfo ci) {
        CallbackProxyClient.onRender(new EventInfo("onrender", this, false));
        CallbackProxyClient.onTick(new EventInfo("ontick", this, false));
    }

    @WrapOperation(
        method = "runGameLoop",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/shader/Framebuffer;framebufferRender(II)V"))
    public void fl$framebufferRender(Framebuffer instance, int f1, int f2, Operation<Void> original) {
        CallbackProxyClient.preRenderFBO(new EventInfo("prerenderfbo", this, false));
        original.call(instance, f1, f2);
        CallbackProxyClient.postRenderFBO(new EventInfo("postrenderfbo", this, false));
    }

    @Inject(method = "updateFramebufferSize", at = @At(value = "HEAD"))
    public void fl$updateFramebufferSize(CallbackInfo ci) {
        CallbackProxyClient.onResize(new EventInfo("updateframebuffersize", this, false));
    }

    @Inject(method = "runTick", at = @At(value = "HEAD"))
    public void fl$runTick(CallbackInfo ci) {
        CallbackProxyClient.newTick(new EventInfo("newtick", this, false));
    }
}
