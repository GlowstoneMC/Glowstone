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
        return new RespawnMessage(dimension);
    }

    @Override
    public ChannelBuffer encode(RespawnMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.buffer(1);
        buffer.writeByte(message.getDimension());
        return buffer;
    }

}
