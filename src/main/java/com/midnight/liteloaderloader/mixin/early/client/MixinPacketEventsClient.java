package com.midnight.liteloaderloader.mixin.early.client;

import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S01PacketJoinGame;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mumfrey.liteloader.client.PacketEventsClient;
import com.mumfrey.liteloader.core.PacketEvents;

@Mixin(value = { PacketEventsClient.class }, remap = false)
public abstract class MixinPacketEventsClient extends PacketEvents {

    @WrapOperation(
        method = "handlePacket(Lcom/mumfrey/liteloader/common/transformers/PacketEventInfo;Lnet/minecraft/network/INetHandler;Lnet/minecraft/network/play/server/S01PacketJoinGame;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/play/INetHandlerPlayClient;handleJoinGame(Lnet/minecraft/network/play/server/S01PacketJoinGame;)V",
            remap = true))
    private void cancelJoinGame(INetHandlerPlayClient instance, S01PacketJoinGame s01PacketJoinGame,
        Operation<Void> original) {
        // do nothing
    }
}
