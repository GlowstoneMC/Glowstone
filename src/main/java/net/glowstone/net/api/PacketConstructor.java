package net.glowstone.net.api;

import com.flowpowered.network.Message;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class PacketConstructor {

    private List<Class> types = new ArrayList<>();
    private List<Object> values = new ArrayList<>();
    private final GlowPacket<?> packet;
    private final Class<? extends Message> clazz;

    public PacketConstructor(GlowPacket<?> packet) {
        this.packet = packet;
        this.clazz = packet.getPacketClass();
    }

    public PacketConstructor field(Object field) {
        types.add(field.getClass());
        values.add(field);
        return this;
    }

    public PacketConstructor intField(int field) {
        types.add(int.class);
        values.add(field);
        return this;
    }

    public PacketConstructor longField(long field) {
        types.add(long.class);
        values.add(field);
        return this;
    }

    public PacketConstructor floatField(float field) {
        types.add(float.class);
        values.add(field);
        return this;
    }

    public PacketConstructor doubleField(double field) {
        types.add(double.class);
        values.add(field);
        return this;
    }

    public PacketConstructor boolField(boolean field) {
        types.add(boolean.class);
        values.add(field);
        return this;
    }

    public PacketConstructor byteField(byte field) {
        types.add(byte.class);
        values.add(field);
        return this;
    }

    public PacketConstructor shortField(short field) {
        types.add(short.class);
        values.add(field);
        return this;
    }

    public PacketConstructor charField(char field) {
        types.add(char.class);
        values.add(field);
        return this;
    }

    public Message build() throws PacketConstructException {
        try {
            Constructor<? extends Message> constructor = clazz.getConstructor(types.toArray(new Class[types.size()]));
            constructor.setAccessible(true);
            return constructor.newInstance(values.toArray(new Object[values.size()]));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new PacketConstructException(this, e);
        }
    }

    public GlowPacket<?> getPacket() {
        return packet;
    }

    public class PacketConstructException extends Exception {
        private PacketConstructor constructor;

        public PacketConstructException(PacketConstructor constructor, Exception e) {
            super(e);
            this.constructor = constructor;
        }

        @Override
        public String getMessage() {
            return "Failed to construct packet " + constructor.getPacket() + ", invalid fields";
        }
    }
}
