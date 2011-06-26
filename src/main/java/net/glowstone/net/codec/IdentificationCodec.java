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
        int dimension = buffer.readByte();
        return new IdentificationMessage(version, name, seed, dimension);
    }

    @Override
    public ChannelBuffer encode(IdentificationMessage message) {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        buffer.writeInt(message.getId());
        ChannelBufferUtils.writeString(buffer, message.getName());
        buffer.writeLong(message.getSeed());
        buffer.writeByte(message.getDimension());
        return buffer;
    }

}
