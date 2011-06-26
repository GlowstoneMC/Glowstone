package net.glowstone.net.codec;

import java.io.IOException;

import net.glowstone.msg.GroundMessage;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class GroundCodec extends MessageCodec<GroundMessage> {

    public GroundCodec() {
        super(GroundMessage.class, 0x0A);
    }

    @Override
    public GroundMessage decode(ChannelBuffer buffer) throws IOException {
        return new GroundMessage(buffer.readByte() == 1);
    }

    @Override
    public ChannelBuffer encode(GroundMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.buffer(1);
        buffer.writeByte(message.isOnGround() ? 1 : 0);
        return buffer;
    }

}
