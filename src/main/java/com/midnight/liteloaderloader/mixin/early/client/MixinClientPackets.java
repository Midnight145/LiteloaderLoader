package com.midnight.liteloaderloader.mixin.early.client;

import net.minecraft.network.INetHandler;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
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
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.midnight.liteloaderloader.lib.StringUtil;
import com.mumfrey.liteloader.common.transformers.PacketEventInfo;
import com.mumfrey.liteloader.core.PacketEvents;
import com.mumfrey.liteloader.core.runtime.Packets;

@Mixin({ C00Handshake.class, C00PacketLoginStart.class, C01PacketEncryptionResponse.class, C00PacketKeepAlive.class,
    C02PacketUseEntity.class, C03PacketPlayer.class, C03PacketPlayer.C04PacketPlayerPosition.class,
    C03PacketPlayer.C06PacketPlayerPosLook.class, C03PacketPlayer.C05PacketPlayerLook.class,
    C07PacketPlayerDigging.class, C08PacketPlayerBlockPlacement.class, C09PacketHeldItemChange.class,
    C0APacketAnimation.class, C0BPacketEntityAction.class, C0CPacketInput.class, C0DPacketCloseWindow.class,
    C0EPacketClickWindow.class, C0FPacketConfirmTransaction.class, C10PacketCreativeInventoryAction.class,
    C11PacketEnchantItem.class, C12PacketUpdateSign.class, C13PacketPlayerAbilities.class, C14PacketTabComplete.class,
    C15PacketClientSettings.class, C16PacketClientStatus.class, S3FPacketCustomPayload.class,
    C17PacketCustomPayload.class, C01PacketChatMessage.class, S08PacketPlayerPosLook.class, S0EPacketSpawnObject.class,
    S11PacketSpawnExperienceOrb.class, S2CPacketSpawnGlobalEntity.class, S0FPacketSpawnMob.class,
    S10PacketSpawnPainting.class, S0CPacketSpawnPlayer.class, S0BPacketAnimation.class, S37PacketStatistics.class,
    S25PacketBlockBreakAnim.class, S35PacketUpdateTileEntity.class, S24PacketBlockAction.class,
    S23PacketBlockChange.class, S3APacketTabComplete.class, S22PacketMultiBlockChange.class,
    S32PacketConfirmTransaction.class, S2EPacketCloseWindow.class, S2DPacketOpenWindow.class,
    S30PacketWindowItems.class, S31PacketWindowProperty.class, S2FPacketSetSlot.class, S40PacketDisconnect.class,
    S19PacketEntityStatus.class, S27PacketExplosion.class, S2BPacketChangeGameState.class, S00PacketKeepAlive.class,
    S21PacketChunkData.class, S26PacketMapChunkBulk.class, S28PacketEffect.class, S2APacketParticles.class,
    S29PacketSoundEffect.class, S34PacketMaps.class, S14PacketEntity.class,
    S14PacketEntity.S15PacketEntityRelMove.class, S14PacketEntity.S17PacketEntityLookMove.class,
    S14PacketEntity.S16PacketEntityLook.class, S36PacketSignEditorOpen.class, S39PacketPlayerAbilities.class,
    S38PacketPlayerListItem.class, S0APacketUseBed.class, S13PacketDestroyEntities.class,
    S1EPacketRemoveEntityEffect.class, S07PacketRespawn.class, S19PacketEntityHeadLook.class,
    S09PacketHeldItemChange.class, S3DPacketDisplayScoreboard.class, S1CPacketEntityMetadata.class,
    S1BPacketEntityAttach.class, S12PacketEntityVelocity.class, S04PacketEntityEquipment.class,
    S1FPacketSetExperience.class, S06PacketUpdateHealth.class, S3BPacketScoreboardObjective.class, S3EPacketTeams.class,
    S3CPacketUpdateScore.class, S05PacketSpawnPosition.class, S03PacketTimeUpdate.class, S33PacketUpdateSign.class,
    S0DPacketCollectItem.class, S18PacketEntityTeleport.class, S20PacketEntityProperties.class,
    S1DPacketEntityEffect.class, S01PacketEncryptionRequest.class, S00PacketDisconnect.class, S01PacketPong.class,
    S00PacketServerInfo.class, S02PacketLoginSuccess.class, S02PacketChat.class, S01PacketJoinGame.class })
public class MixinClientPackets {

    @Inject(method = "processPacket(Lnet/minecraft/network/INetHandler;)V", at = @At("HEAD"))
    private void throwPacketEvent(INetHandler handler, CallbackInfo ci) {
        String className = StringUtil.getShortClassName(this.getClass());
        PacketEventInfo eventInfo = new PacketEventInfo("on" + className, this, true, Packets.indexOf(className));
        PacketEvents.handlePacket(eventInfo, handler);
    }
}
