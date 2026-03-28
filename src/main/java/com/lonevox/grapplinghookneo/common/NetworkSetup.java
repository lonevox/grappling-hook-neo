package com.lonevox.grapplinghookneo.common;

import com.lonevox.grapplinghookneo.GrapplingHookNeo;
import com.lonevox.grapplinghookneo.network.DetachSingleHookMessage;
import com.lonevox.grapplinghookneo.network.GrappleAttachMessage;
import com.lonevox.grapplinghookneo.network.GrappleAttachPosMessage;
import com.lonevox.grapplinghookneo.network.GrappleDetachMessage;
import com.lonevox.grapplinghookneo.network.GrappleEndMessage;
import com.lonevox.grapplinghookneo.network.GrappleModifierMessage;
import com.lonevox.grapplinghookneo.network.KeypressMessage;
import com.lonevox.grapplinghookneo.network.LoggedInMessage;
import com.lonevox.grapplinghookneo.network.PlayerMovementMessage;
import com.lonevox.grapplinghookneo.network.SegmentMessage;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = GrapplingHookNeo.MODID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkSetup {
    private static final String VERSION = "1";

    private NetworkSetup() {
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(VERSION);

        registrar.playToServer(PlayerMovementMessage.TYPE, PlayerMovementMessage.STREAM_CODEC, PlayerMovementMessage::handle);
        registrar.playToServer(GrappleEndMessage.TYPE, GrappleEndMessage.STREAM_CODEC, GrappleEndMessage::handle);
        registrar.playToServer(GrappleModifierMessage.TYPE, GrappleModifierMessage.STREAM_CODEC, GrappleModifierMessage::handle);
        registrar.playToServer(KeypressMessage.TYPE, KeypressMessage.STREAM_CODEC, KeypressMessage::handle);

        registrar.playToClient(LoggedInMessage.TYPE, LoggedInMessage.STREAM_CODEC, LoggedInMessage::handle);
        registrar.playToClient(GrappleAttachMessage.TYPE, GrappleAttachMessage.STREAM_CODEC, GrappleAttachMessage::handle);
        registrar.playToClient(GrappleDetachMessage.TYPE, GrappleDetachMessage.STREAM_CODEC, GrappleDetachMessage::handle);
        registrar.playToClient(DetachSingleHookMessage.TYPE, DetachSingleHookMessage.STREAM_CODEC, DetachSingleHookMessage::handle);
        registrar.playToClient(GrappleAttachPosMessage.TYPE, GrappleAttachPosMessage.STREAM_CODEC, GrappleAttachPosMessage::handle);
        registrar.playToClient(SegmentMessage.TYPE, SegmentMessage.STREAM_CODEC, SegmentMessage::handle);
    }

    public static void sendToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    public static void sendToTrackingChunk(ServerLevel level, ChunkPos chunkPos, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayersTrackingChunk(level, chunkPos, payload);
    }
}
