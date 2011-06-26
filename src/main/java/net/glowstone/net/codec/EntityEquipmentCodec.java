package net.glowstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.glowstone.msg.EntityEquipmentMessage;

public final class EntityEquipmentCodec extends MessageCodec<EntityEquipmentMessage> {

    public EntityEquipmentCodec() {
        super(EntityEquipmentMessage.class, 0x05);
    }

    @Override
    public EntityEquipmentMessage decode(ChannelBuffer buffer) throws IOException {
        int id = buffer.readInt();
        int slot = buffer.readUnsignedShort();
        int item = buffer.readUnsignedShort();
        int damage = buffer.readUnsignedByte();
        return new EntityEquipmentMessage(id, slot, item, damage);
    }

    @Override
    public ChannelBuffer encode(EntityEquipmentMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.buffer(8);
        buffer.writeInt(message.getId());
        buffer.writeShort(message.getSlot());
        buffer.writeShort(message.getItem());
        buffer.writeShort(message.getDamage());
        return buffer;
    }

}
