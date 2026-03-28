package com.lonevox.grapplinghookneo.network;

import com.lonevox.grapplinghookneo.client.ClientControllerManager;
import com.lonevox.grapplinghookneo.GrapplingHookNeo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class DetachSingleHookMessage implements CustomPacketPayload {
    public static final Type<DetachSingleHookMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GrapplingHookNeo.MODID, "detach_single_hook"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DetachSingleHookMessage> STREAM_CODEC = StreamCodec.of((buf, msg) -> msg.encode(buf), DetachSingleHookMessage::new);

    public int id;
    public int hookid;

    public DetachSingleHookMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    public DetachSingleHookMessage(int id, int hookid) {
        this.id = id;
        this.hookid = hookid;
    }

    public void decode(FriendlyByteBuf buf) {
        this.id = buf.readInt();
        this.hookid = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeInt(this.hookid);
    }

    public static void handle(DetachSingleHookMessage message, IPayloadContext context) {
        ClientControllerManager.receiveGrappleDetachHook(message.id, message.hookid);
    }

    @Override
    public Type<DetachSingleHookMessage> type() {
        return TYPE;
    }
}
