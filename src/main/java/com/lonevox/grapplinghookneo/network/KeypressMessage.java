package com.lonevox.grapplinghookneo.network;

import com.lonevox.grapplinghookneo.GrapplingHookNeo;
import com.lonevox.grapplinghookneo.items.KeypressItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class KeypressMessage implements CustomPacketPayload {
    public static final Type<KeypressMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GrapplingHookNeo.MODID, "keypress"));
    public static final StreamCodec<RegistryFriendlyByteBuf, KeypressMessage> STREAM_CODEC = StreamCodec.of((buf, msg) -> msg.encode(buf), KeypressMessage::new);

    KeypressItem.Keys key;
    boolean isDown;

    public KeypressMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    public KeypressMessage(KeypressItem.Keys thekey, boolean isDown) {
        this.key = thekey;
        this.isDown = isDown;
    }

    public void decode(FriendlyByteBuf buf) {
        this.key = KeypressItem.Keys.values()[buf.readInt()];
        this.isDown = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.key.ordinal());
        buf.writeBoolean(this.isDown);
    }

    public static void handle(KeypressMessage message, IPayloadContext context) {
        final ServerPlayer player = (ServerPlayer) context.player();
        if (player != null) {
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (stack != null) {
                Item item = stack.getItem();
                if (item instanceof KeypressItem keypressItem) {
                    if (message.isDown) {
                        keypressItem.onCustomKeyDown(stack, player, message.key, true);
                    } else {
                        keypressItem.onCustomKeyUp(stack, player, message.key, true);
                    }
                    return;
                }
            }

            stack = player.getItemInHand(InteractionHand.OFF_HAND);
            if (stack != null) {
                Item item = stack.getItem();
                if (item instanceof KeypressItem keypressItem) {
                    if (message.isDown) {
                        keypressItem.onCustomKeyDown(stack, player, message.key, false);
                    } else {
                        keypressItem.onCustomKeyUp(stack, player, message.key, false);
                    }
                }
            }
        }
    }

    @Override
    public Type<KeypressMessage> type() {
        return TYPE;
    }
}
