package com.lonevox.grapplinghookneo.network;

import com.lonevox.grapplinghookneo.GrapplingHookNeo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class GrappleDetachMessage implements CustomPacketPayload {
    public static final Type<GrappleDetachMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GrapplingHookNeo.MODID, "grapple_detach"));
    public static final StreamCodec<RegistryFriendlyByteBuf, GrappleDetachMessage> STREAM_CODEC = StreamCodec.of((buf, msg) -> msg.encode(buf), GrappleDetachMessage::new);

    public int id;

    public GrappleDetachMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    public GrappleDetachMessage(int id) {
        this.id = id;
    }

    public void decode(FriendlyByteBuf buf) {
        this.id = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
    }

    public static void handle(GrappleDetachMessage message, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketBridgeAccess.get().handleGrappleDetach(message));
    }

    @Override
    public @NotNull Type<GrappleDetachMessage> type() {
        return TYPE;
    }
}
