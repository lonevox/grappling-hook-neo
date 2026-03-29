package com.lonevox.grapplinghookneo.network;

import com.lonevox.grapplinghookneo.blocks.modifierblock.TileEntityGrappleModifier;
import com.lonevox.grapplinghookneo.GrapplingHookNeo;
import com.lonevox.grapplinghookneo.utils.GrappleCustomization;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class GrappleModifierMessage implements CustomPacketPayload {
    public static final Type<GrappleModifierMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GrapplingHookNeo.MODID, "grapple_modifier"));
    public static final StreamCodec<RegistryFriendlyByteBuf, GrappleModifierMessage> STREAM_CODEC = StreamCodec.of((buf, msg) -> msg.encode(buf), GrappleModifierMessage::new);

    public BlockPos pos;
    public GrappleCustomization custom;

    public GrappleModifierMessage(BlockPos pos, GrappleCustomization custom) {
        this.pos = pos;
        this.custom = custom;
    }

    public GrappleModifierMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    public void decode(FriendlyByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.custom = new GrappleCustomization();
        this.custom.readFromBuf(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        this.custom.writeToBuf(buf);
    }

    public static void handle(GrappleModifierMessage message, IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();

		Level w = player.level();
        BlockEntity ent = w.getBlockEntity(message.pos);

        if (ent instanceof TileEntityGrappleModifier tileEntityGrappleModifier) {
            tileEntityGrappleModifier.setCustomizationServer(message.custom);
        }
    }

    @Override
    public @NotNull Type<GrappleModifierMessage> type() {
        return TYPE;
    }
}
