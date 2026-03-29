package com.lonevox.grapplinghookneo.network;

import com.lonevox.grapplinghookneo.GrapplingHookNeo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class GrappleAttachPosMessage implements CustomPacketPayload {
    public static final Type<GrappleAttachPosMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GrapplingHookNeo.MODID, "grapple_attach_pos"));
    public static final StreamCodec<RegistryFriendlyByteBuf, GrappleAttachPosMessage> STREAM_CODEC = StreamCodec.of((buf, msg) -> msg.encode(buf), GrappleAttachPosMessage::new);

    public int id;
    public double x;
    public double y;
    public double z;

    public GrappleAttachPosMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    public GrappleAttachPosMessage(int id, double x, double y, double z) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void decode(FriendlyByteBuf buf) {
        this.id = buf.readInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }

    public static void handle(GrappleAttachPosMessage message, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketBridgeAccess.get().handleGrappleAttachPos(message));
    }

    @Override
    public @NotNull Type<GrappleAttachPosMessage> type() {
        return TYPE;
    }
}
