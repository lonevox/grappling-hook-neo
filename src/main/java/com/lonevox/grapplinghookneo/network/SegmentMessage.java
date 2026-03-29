package com.lonevox.grapplinghookneo.network;

import com.lonevox.grapplinghookneo.GrapplingHookNeo;
import com.lonevox.grapplinghookneo.utils.Vec;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class SegmentMessage implements CustomPacketPayload {
    public static final Type<SegmentMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GrapplingHookNeo.MODID, "segment"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SegmentMessage> STREAM_CODEC = StreamCodec.of((buf, msg) -> msg.encode(buf), SegmentMessage::new);

    public int id;
    public boolean add;
    public int index;
    public Vec pos;
    public Direction topFacing;
    public Direction bottomFacing;

    public SegmentMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    public SegmentMessage(int id, boolean add, int index, Vec pos, Direction topfacing, Direction bottomfacing) {
        this.id = id;
        this.add = add;
        this.index = index;
        this.pos = pos;
        this.topFacing = topfacing;
        this.bottomFacing = bottomfacing;
    }

    public void decode(FriendlyByteBuf buf) {
        this.id = buf.readInt();
        this.add = buf.readBoolean();
        this.index = buf.readInt();
        this.pos = new Vec(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.topFacing = buf.readEnum(Direction.class);
        this.bottomFacing = buf.readEnum(Direction.class);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeBoolean(this.add);
        buf.writeInt(this.index);
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);
        buf.writeEnum(this.topFacing);
        buf.writeEnum(this.bottomFacing);
    }

    public static void handle(SegmentMessage message, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketBridgeAccess.get().handleSegment(message));
    }

    @Override
    public @NotNull Type<SegmentMessage> type() {
        return TYPE;
    }
}
