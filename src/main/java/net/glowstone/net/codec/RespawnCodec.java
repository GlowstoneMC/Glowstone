package net.glowstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.glowstone.msg.RespawnMessage;

public final class RespawnCodec extends MessageCodec<RespawnMessage> {

    public RespawnCodec() {
        super(RespawnMessage.class, 0x09);
    }

    @Override
    public RespawnMessage decode(ChannelBuffer buffer) throws IOException {
        byte dimension = buffer.readByte();
        byte difficulty = buffer.readByte();
        byte mode = buffer.readByte();
        short worldHeight = buffer.readShort();
        long seed = buffer.readLong();
        return new RespawnMessage(dimension, difficulty, mode, worldHeight, seed);
    }

    @Override
    public ChannelBuffer encode(RespawnMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.buffer(14);
        buffer.writeByte(message.getDimension());
        buffer.writeByte(message.getDifficulty());
        buffer.writeByte(message.getGameMode());
        buffer.writeShort(message.getWorldHeight());
        buffer.writeLong(message.getSeed());
        return buffer;
    }

}
