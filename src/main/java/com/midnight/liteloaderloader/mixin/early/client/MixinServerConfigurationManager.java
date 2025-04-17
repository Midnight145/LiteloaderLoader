package com.midnight.liteloaderloader.mixin.early.client;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;
import com.mumfrey.liteloader.client.CallbackProxyClient;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import com.mumfrey.liteloader.transformers.event.ReturnEventInfo;

@Mixin(ServerConfigurationManager.class)
public class MixinServerConfigurationManager {

    @Inject(method = "initializeConnectionToPlayer", at = @At("RETURN"), remap = false)
    private void throwEvent(CallbackInfo ci) {}

    @Inject(method = "playerLoggedIn", at = @At("RETURN"))
    private void onLogin(EntityPlayerMP player, CallbackInfo ci) {
        CallbackProxyClient.onPlayerLogin(new EventInfo("onplayerlogin", this, false), player);
    }

    @Inject(method = "playerLoggedOut", at = @At("RETURN"))
    private void onLogout(EntityPlayerMP player, CallbackInfo ci) {
        CallbackProxyClient.onPlayerLogout(new EventInfo("onplayerlogout", this, false), player);
    }

    @Inject(method = "createPlayerForUser", at = @At("RETURN"))
    private void onSpawn(GameProfile profile, CallbackInfoReturnable<EntityPlayerMP> cir) {
        CallbackProxyClient
            .onSpawnPlayer(new ReturnEventInfo("onspawnplayer", this, false, cir.getReturnValue()), profile);
    }

    @Inject(method = "respawnPlayer", at = @At("RETURN"))
    private void onRespawn(EntityPlayerMP player, int dimension, boolean conqueredEnd,
        CallbackInfoReturnable<EntityPlayerMP> cir) {
        CallbackProxyClient.onRespawnPlayer(
            new ReturnEventInfo("onrespawnplayer", this, false, cir.getReturnValue()),
            player,
            dimension,
            conqueredEnd);
    }
}
