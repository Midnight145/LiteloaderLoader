package com.midnight.liteloaderloader.mixin.early.client;

import net.minecraft.network.INetHandler;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.midnight.liteloaderloader.lib.StringUtil;
import com.mumfrey.liteloader.common.transformers.PacketEventInfo;
import com.mumfrey.liteloader.core.PacketEvents;
import com.mumfrey.liteloader.core.runtime.Packets;

@Mixin({ C00Handshake.class, C00PacketLoginStart.class, C01PacketEncryptionResponse.class, C00PacketKeepAlive.class,
    C01PacketChatMessage.class, C02PacketUseEntity.class, C03PacketPlayer.class,
    C03PacketPlayer.C04PacketPlayerPosition.class, C03PacketPlayer.C06PacketPlayerPosLook.class,
    C03PacketPlayer.C05PacketPlayerLook.class, C07PacketPlayerDigging.class, C08PacketPlayerBlockPlacement.class,
    C09PacketHeldItemChange.class, C0APacketAnimation.class, C0BPacketEntityAction.class, C0CPacketInput.class,
    C0DPacketCloseWindow.class, C0EPacketClickWindow.class, C0FPacketConfirmTransaction.class,
    C10PacketCreativeInventoryAction.class, C11PacketEnchantItem.class, C12PacketUpdateSign.class,
    C13PacketPlayerAbilities.class, C14PacketTabComplete.class, C15PacketClientSettings.class,
    C16PacketClientStatus.class, C17PacketCustomPayload.class })
public class MixinClientPackets {

    @Inject(method = "processPacket(Lnet/minecraft/network/INetHandler;)V", at = @At("HEAD"))
    private void throwPacketEvent(INetHandler handler, CallbackInfo ci) {
        String className = StringUtil.getShortClassName(this.getClass());
        PacketEventInfo eventInfo = new PacketEventInfo("on" + className, this, true, Packets.indexOf(className));
        PacketEvents.handlePacket(eventInfo, handler);
    }
}
