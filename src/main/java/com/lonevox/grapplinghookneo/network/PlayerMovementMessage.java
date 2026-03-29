package com.lonevox.grapplinghookneo.network;

import com.lonevox.grapplinghookneo.GrapplingHookNeo;
import com.lonevox.grapplinghookneo.utils.Vec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class PlayerMovementMessage implements CustomPacketPayload {
    public static final Type<PlayerMovementMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GrapplingHookNeo.MODID, "player_movement"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerMovementMessage> STREAM_CODEC = StreamCodec.of((buf, msg) -> msg.encode(buf), PlayerMovementMessage::new);

    public int entityId;
    public double x;
    public double y;
    public double z;
    public double mx;
    public double my;
    public double mz;

    public PlayerMovementMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    public PlayerMovementMessage(int entityId, double x, double y, double z, double mx, double my, double mz) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mx = mx;
        this.my = my;
        this.mz = mz;
    }

    public void decode(FriendlyByteBuf buf) {
        try {
            this.entityId = buf.readInt();
            this.x = buf.readDouble();
            this.y = buf.readDouble();
            this.z = buf.readDouble();
            this.mx = buf.readDouble();
            this.my = buf.readDouble();
            this.mz = buf.readDouble();
        } catch (Exception e) {
            System.out.print("Playermovement error: ");
            System.out.println(buf);
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(mx);
        buf.writeDouble(my);
        buf.writeDouble(mz);
    }

    public static void handle(PlayerMovementMessage message, IPayloadContext context) {
        final ServerPlayer referencedPlayer = (ServerPlayer) context.player();

        if (referencedPlayer.getId() == message.entityId) {
            new Vec(message.x, message.y, message.z).setPos(referencedPlayer);
            new Vec(message.mx, message.my, message.mz).setMotion(referencedPlayer);

            referencedPlayer.connection.resetPosition();

            if (!referencedPlayer.onGround()) {
                if (message.my >= 0) {
                    referencedPlayer.fallDistance = 0;
                } else {
                    double gravity = 0.05 * 2;
                    referencedPlayer.fallDistance = (float) (Math.pow(message.my, 2) / (2 * gravity));
                }
            }
        }
    }

    @Override
    public @NotNull Type<PlayerMovementMessage> type() {
        return TYPE;
    }
}
