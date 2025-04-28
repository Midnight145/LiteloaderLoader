
package com.midnight.liteloaderloader.mixin.early.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.midnight.liteloaderloader.lib.StringUtil;
import com.mumfrey.liteloader.api.LiteAPI;
import com.mumfrey.liteloader.client.PacketEventsClient;
import com.mumfrey.liteloader.client.api.LiteLoaderCoreAPIClient;
import com.mumfrey.liteloader.common.transformers.PacketEventInfo;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.PacketEvents;
import com.mumfrey.liteloader.core.runtime.Packets;

@Mixin(value = { S01PacketJoinGame.class, S02PacketLoginSuccess.class, S02PacketChat.class })
public class MixinSpecialPackets {

    private static Method handlePacket;

    private Method getHandlePacket() {
        if (handlePacket != null) {
            return handlePacket;
        }
        try {
            handlePacket = PacketEventsClient.class
                .getDeclaredMethod("handlePacket", PacketEventInfo.class, INetHandler.class, this.getClass());
            handlePacket.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to find handlePacket method", e);
        }
        return handlePacket;
    }

    @Inject(method = "processPacket(Lnet/minecraft/network/INetHandler;)V", at = @At("HEAD"))
    private void throwPacketEvent(INetHandler handler, CallbackInfo ci) {
        String className = StringUtil.getShortClassName(this.getClass());
        PacketEventInfo<Packet> eventInfo = new PacketEventInfo(
            "on" + className,
            this,
            true,
            Packets.indexOf(className));
        LiteAPI api = LiteLoader.getAPI("liteloader");
        if (api instanceof LiteLoaderCoreAPIClient client) {
            PacketEvents packetHandler = client.getObjectFactory()
                .getPacketEventBroker();
            if (!(packetHandler instanceof PacketEventsClient)) {
                return;
            }
            try {
                this.getHandlePacket()
                    .invoke(packetHandler, eventInfo, handler, this);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
