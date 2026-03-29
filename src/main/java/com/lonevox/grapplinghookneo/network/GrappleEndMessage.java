package com.lonevox.grapplinghookneo.network;

import com.lonevox.grapplinghookneo.GrapplingHookNeo;
import com.lonevox.grapplinghookneo.server.ServerControllerManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class GrappleEndMessage implements CustomPacketPayload {
    public static final Type<GrappleEndMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GrapplingHookNeo.MODID, "grapple_end"));
    public static final StreamCodec<RegistryFriendlyByteBuf, GrappleEndMessage> STREAM_CODEC = StreamCodec.of((buf, msg) -> msg.encode(buf), GrappleEndMessage::new);

    public int entityId;
    public HashSet<Integer> hookEntityIds;

    public GrappleEndMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    public GrappleEndMessage(int entityId, HashSet<Integer> hookEntityIds) {
        this.entityId = entityId;
        this.hookEntityIds = hookEntityIds;
    }

    public void decode(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        int size = buf.readInt();
        this.hookEntityIds = new HashSet<>();
        for (int i = 0; i < size; i++) {
            this.hookEntityIds.add(buf.readInt());
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeInt(this.hookEntityIds.size());
        for (int id : this.hookEntityIds) {
            buf.writeInt(id);
        }
    }

    public static void handle(GrappleEndMessage message, IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
		Level w = player.level();

        ServerControllerManager.receiveGrappleEnd(message.entityId, w, message.hookEntityIds);
    }

    @Override
    public @NotNull Type<GrappleEndMessage> type() {
        return TYPE;
    }
}
