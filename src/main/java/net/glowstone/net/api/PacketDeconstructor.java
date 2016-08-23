package net.glowstone.net.api;

import com.flowpowered.network.Message;

import java.lang.reflect.Field;

public class PacketDeconstructor {

    private GlowPacket<?> packet;
    private Class<? extends Message> clazz;
    private Message message;

    public PacketDeconstructor(GlowPacket<?> packet, Message message) {
        this.packet = packet;
        this.clazz = packet.getPacketClass();
        this.message = message;
    }

    public Object getField(String name) throws PacketDeconstructionException {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(message);
        } catch (Exception e) {
            throw new PacketDeconstructionException(this);
        }
    }

    @Deprecated
    public Object getField(int index) throws PacketDeconstructionException {
        try {
            Field field = clazz.getDeclaredFields()[index];
            field.setAccessible(true);
            return field.get(message);
        } catch (Exception e) {
            throw new PacketDeconstructionException(this);
        }
    }

    public void setField(String name, Object value) throws PacketDeconstructionException {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            field.set(message, value);
        } catch (Exception e) {
            throw new PacketDeconstructionException(this);
        }
    }

    @Deprecated
    public void setField(int index, Object value) throws PacketDeconstructionException {
        try {
            Field field = clazz.getDeclaredFields()[index];
            field.setAccessible(true);
            field.set(message, value);
        } catch (Exception e) {
            throw new PacketDeconstructionException(this);
        }
    }

    public Message build() {
        return message;
    }

    public GlowPacket<?> getPacket() {
        return packet;
    }

    public class PacketDeconstructionException extends Exception {
        private PacketDeconstructor deconstructor;

        public PacketDeconstructionException(PacketDeconstructor deconstructor) {
            this.deconstructor = deconstructor;
        }

        @Override
        public String getMessage() {
            return "Failed to deconstruct packet " + deconstructor.getPacket() + ", unknown field";
        }
    }
}
