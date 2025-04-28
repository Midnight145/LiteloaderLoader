
package com.midnight.liteloaderloader.mixin.early.common;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.client.C01PacketChatMessage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.midnight.liteloaderloader.lib.StringUtil;
import com.mumfrey.liteloader.common.transformers.PacketEventInfo;
import com.mumfrey.liteloader.core.PacketEvents;
import com.mumfrey.liteloader.core.runtime.Packets;

@Mixin(value = C01PacketChatMessage.class)
public class MixinC01PacketChatMessage {

    @Inject(method = "processPacket(Lnet/minecraft/network/INetHandler;)V", at = @At("HEAD"))
    private void throwPacketEvent(INetHandler handler, CallbackInfo ci) {
        String className = StringUtil.getShortClassName(this.getClass());
        PacketEventInfo eventInfo = new PacketEventInfo("on" + className, this, true, Packets.indexOf(className));
        PacketEvents.handlePacket(eventInfo, handler);
    }
}
