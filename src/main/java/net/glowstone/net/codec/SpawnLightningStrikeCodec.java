package net.glowstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.glowstone.msg.SpawnLightningStrikeMessage;

public final class SpawnLightningStrikeCodec extends MessageCodec<SpawnLightningStrikeMessage> {

    public SpawnLightningStrikeCodec() {
        super(SpawnLightningStrikeMessage.class, 0x47);
    }

    @Override
    public SpawnLightningStrikeMessage decode(ChannelBuffer buffer) throws IOException {
        int id = buffer.readInt();
        int mode = buffer.readUnsignedByte();
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        return new SpawnLightningStrikeMessage(id, mode, x, y, z);
    }

    @Override
    public ChannelBuffer encode(SpawnLightningStrikeMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.buffer(17);
        buffer.writeInt(message.getId());
        buffer.writeByte(message.getMode());
        buffer.writeInt(message.getX());
        buffer.writeInt(message.getY());
        buffer.writeInt(message.getZ());
        return buffer;
    }

}
