package net.glowstone.net.codec;

import java.io.IOException;

import net.glowstone.msg.SpawnVehicleMessage;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class SpawnVehicleCodec extends MessageCodec<SpawnVehicleMessage> {

    public SpawnVehicleCodec() {
        super(SpawnVehicleMessage.class, 0x17);
    }

    @Override
    public SpawnVehicleMessage decode(ChannelBuffer buffer) throws IOException {
        int id = buffer.readInt();
        int type = buffer.readUnsignedByte();
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        int fireballId = buffer.readInt();
        if (fireballId != 0) {
            int fireballX = buffer.readShort();
            int fireballY = buffer.readShort();
            int fireballZ = buffer.readShort();
            return new SpawnVehicleMessage(id, type, x, y, z, fireballId, fireballX, fireballY, fireballZ);
        }
        return new SpawnVehicleMessage(id, type, x, y, z);
    }

    @Override
    public ChannelBuffer encode(SpawnVehicleMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.buffer(message.hasFireball() ? 28 : 22);
        buffer.writeInt(message.getId());
        buffer.writeByte(message.getType());
        buffer.writeInt(message.getX());
        buffer.writeInt(message.getY());
        buffer.writeInt(message.getZ());
        buffer.writeInt(message.getFireballId());
        if (message.hasFireball()) {
            buffer.writeShort(message.getFireballX());
            buffer.writeShort(message.getFireballY());
            buffer.writeShort(message.getFireballZ());
        }
        return buffer;
    }

}
