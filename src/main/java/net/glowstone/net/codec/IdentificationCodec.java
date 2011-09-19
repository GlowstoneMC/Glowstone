package net.glowstone.net.codec;

import net.glowstone.msg.IdentificationMessage;
import net.glowstone.util.ChannelBufferUtils;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class IdentificationCodec extends MessageCodec<IdentificationMessage> {

    public IdentificationCodec() {
        super(IdentificationMessage.class, 0x01);
    }

    @Override
    public IdentificationMessage decode(ChannelBuffer buffer) {
        int version = buffer.readInt();
        String name = ChannelBufferUtils.readString(buffer);
        long seed = buffer.readLong();
        int mode = buffer.readInt();
        int dimension = buffer.readByte();
        int difficulty = buffer.readByte();
        int worldHeight = buffer.readByte();
        int maxPlayers = buffer.readByte();
        return new IdentificationMessage(version, name, seed, mode, dimension, difficulty, worldHeight, maxPlayers);
    }

    @Override
    public ChannelBuffer encode(IdentificationMessage message) {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        buffer.writeInt(message.getId());
        ChannelBufferUtils.writeString(buffer, message.getName());
        buffer.writeLong(message.getSeed());
        buffer.writeInt(message.getGameMode());
        buffer.writeByte(message.getDimension());
        buffer.writeByte(message.getDifficulty());
        buffer.writeByte(message.getWorldHeight());
        buffer.writeByte(message.getMaxPlayers());
        return buffer;
    }

}
