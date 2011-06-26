package net.glowstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.glowstone.msg.HealthMessage;

public final class HealthCodec extends MessageCodec<HealthMessage> {

    public HealthCodec() {
        super(HealthMessage.class, 0x08);
    }

    @Override
    public HealthMessage decode(ChannelBuffer buffer) throws IOException {
        int health = buffer.readUnsignedByte();
        return new HealthMessage(health);
    }

    @Override
    public ChannelBuffer encode(HealthMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.buffer(1);
        buffer.writeByte(message.getHealth());
        return buffer;
    }

}
