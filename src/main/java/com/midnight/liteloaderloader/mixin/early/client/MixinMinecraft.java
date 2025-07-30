package com.midnight.liteloaderloader.mixin.early.client;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
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

    @SuppressWarnings("deprecation")
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
}
