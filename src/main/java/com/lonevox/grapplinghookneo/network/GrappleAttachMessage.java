package com.lonevox.grapplinghookneo.network;

import com.lonevox.grapplinghookneo.GrapplingHookNeo;
import com.lonevox.grapplinghookneo.utils.GrappleCustomization;
import com.lonevox.grapplinghookneo.utils.Vec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class GrappleAttachMessage implements CustomPacketPayload {
    public static final Type<GrappleAttachMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GrapplingHookNeo.MODID, "grapple_attach"));
    public static final StreamCodec<RegistryFriendlyByteBuf, GrappleAttachMessage> STREAM_CODEC = StreamCodec.of((buf, msg) -> msg.encode(buf), GrappleAttachMessage::new);

    public int id;
    public double x;
    public double y;
    public double z;
    public int controlId;
    public int entityId;
    public BlockPos blockPos;
    public LinkedList<Vec> segments;
    public LinkedList<Direction> segmentTopSides;
    public LinkedList<Direction> segmentBottomSides;
    public GrappleCustomization custom;

    public GrappleAttachMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    public GrappleAttachMessage(int id, double x, double y, double z, int controlid, int entityid, BlockPos blockpos, LinkedList<Vec> segments, LinkedList<Direction> segmenttopsides, LinkedList<Direction> segmentbottomsides, GrappleCustomization custom) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.controlId = controlid;
        this.entityId = entityid;
        this.blockPos = blockpos;
        this.segments = segments;
        this.segmentTopSides = segmenttopsides;
        this.segmentBottomSides = segmentbottomsides;
        this.custom = custom;
    }

    public void decode(FriendlyByteBuf buf) {
        this.id = buf.readInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.controlId = buf.readInt();
        this.entityId = buf.readInt();
        int blockx = buf.readInt();
        int blocky = buf.readInt();
        int blockz = buf.readInt();
        this.blockPos = new BlockPos(blockx, blocky, blockz);

        this.custom = new GrappleCustomization();
        this.custom.readFromBuf(buf);

        int size = buf.readInt();
        this.segments = new LinkedList<>();
        this.segmentBottomSides = new LinkedList<>();
        this.segmentTopSides = new LinkedList<>();

        segments.add(new Vec(0, 0, 0));
        segmentBottomSides.add(null);
        segmentTopSides.add(null);

        for (int i = 1; i < size - 1; i++) {
            this.segments.add(new Vec(buf.readDouble(), buf.readDouble(), buf.readDouble()));
            this.segmentBottomSides.add(buf.readEnum(Direction.class));
            this.segmentTopSides.add(buf.readEnum(Direction.class));
        }

        segments.add(new Vec(0, 0, 0));
        segmentBottomSides.add(null);
        segmentTopSides.add(null);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeInt(this.controlId);
        buf.writeInt(this.entityId);
        buf.writeInt(this.blockPos.getX());
        buf.writeInt(this.blockPos.getY());
        buf.writeInt(this.blockPos.getZ());

        this.custom.writeToBuf(buf);

        buf.writeInt(this.segments.size());
        for (int i = 1; i < this.segments.size() - 1; i++) {
            buf.writeDouble(this.segments.get(i).x);
            buf.writeDouble(this.segments.get(i).y);
            buf.writeDouble(this.segments.get(i).z);
            buf.writeEnum(this.segmentBottomSides.get(i));
            buf.writeEnum(this.segmentTopSides.get(i));
        }
    }

    public static void handle(GrappleAttachMessage message, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketBridgeAccess.get().handleGrappleAttach(message));
    }

    @Override
    public @NotNull Type<GrappleAttachMessage> type() {
        return TYPE;
    }
}
