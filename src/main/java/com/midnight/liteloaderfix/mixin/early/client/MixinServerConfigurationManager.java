package com.midnight.liteloaderfix.mixin.early.client;

import com.midnight.liteloaderfix.lib.R;
import com.mojang.authlib.GameProfile;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerConfigurationManager.class)
public class MixinServerConfigurationManager {

    @Unique
    private static R lf$handlerClass;

    static {
        try {
            lf$handlerClass = R.of(Class.forName("com.mumfrey.liteloader.core.event.EventProxy.2"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject(method = "initializeConnectionToPlayer", at = @At("RETURN"))
    private void throwEvent(CallbackInfo ci) {}

    @Inject(method = "playerLoggedIn", at = @At("RETURN"))
    private void onLogin(EntityPlayerMP player, CallbackInfo ci) {
        lf$handlerClass.call("$event00016", new EventInfo("onplayerlogin", this, false), player);
    }
    @Inject(method = "playerLoggedOut", at = @At("RETURN"))
    private void onLogout(EntityPlayerMP player, CallbackInfo ci) {
        lf$handlerClass.call("$event00017", new EventInfo("onplayerlogin", this, false), player);
    }
    @Inject(method = "createPlayerForUser", at = @At("RETURN"))
    private void onSpawn(GameProfile profile, CallbackInfoReturnable<EntityPlayerMP> cir) {
        lf$handlerClass.call("$event00018", new EventInfo("onplayerlogin", this, false), profile);
    }
    @Inject(method = "respawnPlayer", at = @At("RETURN"))
    private void onRespawn(EntityPlayerMP player, int dimension, boolean conqueredEnd, CallbackInfoReturnable<EntityPlayerMP> cir) {
        lf$handlerClass.call("$event00019", new EventInfo("onplayerlogin", this, false), player, dimension, conqueredEnd);
    }
}
