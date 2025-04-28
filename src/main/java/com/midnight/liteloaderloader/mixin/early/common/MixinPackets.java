package com.midnight.liteloaderloader.mixin.early.common;

import net.minecraft.network.INetHandler;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.midnight.liteloaderloader.lib.StringUtil;
import com.mumfrey.liteloader.common.transformers.PacketEventInfo;
import com.mumfrey.liteloader.core.PacketEvents;
import com.mumfrey.liteloader.core.runtime.Packets;

@Mixin(
    value = { S02PacketChat.class, S01PacketJoinGame.class, S02PacketLoginSuccess.class, S3FPacketCustomPayload.class,
        C17PacketCustomPayload.class, C01PacketChatMessage.class })
public class MixinPackets {

    @Inject(method = "processPacket(Lnet/minecraft/network/INetHandler;)V", at = @At("HEAD"))
    private void throwPacketEvent(INetHandler handler, CallbackInfo ci) {
        String className = StringUtil.getShortClassName(this.getClass());
        PacketEventInfo eventInfo = new PacketEventInfo("on" + className, this, true, Packets.indexOf(className));
        PacketEvents.handlePacket(eventInfo, handler);
    }
}
