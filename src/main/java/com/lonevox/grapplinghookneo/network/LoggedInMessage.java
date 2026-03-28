package com.lonevox.grapplinghookneo.network;

import com.lonevox.grapplinghookneo.GrapplingHookNeo;
import com.lonevox.grapplinghookneo.config.GrappleConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;

public class LoggedInMessage implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<LoggedInMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GrapplingHookNeo.MODID, "logged_in"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LoggedInMessage> STREAM_CODEC = StreamCodec.of((buf, msg) -> msg.encode(buf), LoggedInMessage::new);

    public GrappleConfig.Config conf;

    public LoggedInMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    public LoggedInMessage(GrappleConfig.Config serverconf) {
        this.conf = serverconf;
    }

    public <T> void decodeClass(FriendlyByteBuf buf, Class<T> theClass, T theObject) {
        Field[] fields = theClass.getDeclaredFields();
        Arrays.sort(fields, Comparator.comparing(Field::getName));

        for (Field field : fields) {
            java.lang.reflect.Type fieldtype = field.getGenericType();
            try {
                if (fieldtype.getTypeName().equals("int")) {
                    field.setInt(theObject, buf.readInt());
                } else if (fieldtype.getTypeName().equals("double")) {
                    field.setDouble(theObject, buf.readDouble());
                } else if (fieldtype.getTypeName().equals("boolean")) {
                    field.setBoolean(theObject, buf.readBoolean());
                } else if (fieldtype.getTypeName().equals("java.lang.String")) {
                    int len = buf.readInt();
                    CharSequence charseq = buf.readCharSequence(len, Charset.defaultCharset());
                    field.set(theObject, charseq.toString());
                } else if (field.getType() != null && Object.class.isAssignableFrom(field.getType())) {
                    Class<?> newClass = field.getType();
                    decodeClass(buf, (Class<Object>) newClass, newClass.cast(field.get(theObject)));
                } else {
                    System.out.println("Unknown Type");
                    System.out.println(fieldtype.getTypeName());
                }
            } catch (IllegalAccessException e) {
                System.out.println(e);
            }
        }
    }

    public void decode(FriendlyByteBuf buf) {
        Class<GrappleConfig.Config> confclass = GrappleConfig.Config.class;
        this.conf = new GrappleConfig.Config();

        decodeClass(buf, confclass, this.conf);
    }

    public <T> void encodeClass(FriendlyByteBuf buf, Class<T> theClass, T theObject) {
        Field[] fields = theClass.getDeclaredFields();
        Arrays.sort(fields, Comparator.comparing(Field::getName));

        for (Field field : fields) {
            java.lang.reflect.Type fieldtype = field.getGenericType();
            try {
                if (fieldtype.getTypeName().equals("int")) {
                    buf.writeInt(field.getInt(theObject));
                } else if (fieldtype.getTypeName().equals("double")) {
                    buf.writeDouble(field.getDouble(theObject));
                } else if (fieldtype.getTypeName().equals("boolean")) {
                    buf.writeBoolean(field.getBoolean(theObject));
                } else if (fieldtype.getTypeName().equals("java.lang.String")) {
                    String str = (String) field.get(theObject);
                    buf.writeInt(str.length());
                    buf.writeCharSequence(str.subSequence(0, str.length()), Charset.defaultCharset());
                } else if (field.getType() != null && Object.class.isAssignableFrom(field.getType())) {
                    Class<?> newClass = field.getType();
                    encodeClass(buf, (Class<Object>) newClass, newClass.cast(field.get(theObject)));
                } else {
                    System.out.println("Unknown Type");
                    System.out.println(fieldtype.getTypeName());
                }
            } catch (IllegalAccessException e) {
                System.out.println(e);
            }
        }
    }

    public void encode(FriendlyByteBuf buf) {
        Class<GrappleConfig.Config> confclass = GrappleConfig.Config.class;
        encodeClass(buf, confclass, this.conf);
    }

    public static void handle(LoggedInMessage message, IPayloadContext context) {
        GrappleConfig.setServerOptions(message.conf);
    }

    @Override
    public CustomPacketPayload.Type<LoggedInMessage> type() {
        return TYPE;
    }
}
