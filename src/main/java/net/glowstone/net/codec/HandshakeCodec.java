package net.glowstone.net.codec;

import net.glowstone.msg.HandshakeMessage;
import net.glowstone.util.ChannelBufferUtils;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class HandshakeCodec extends MessageCodec<HandshakeMessage> {

    public HandshakeCodec() {
        super(HandshakeMessage.class, 0x02);
    }

    @Override
    public HandshakeMessage decode(ChannelBuffer buffer) {
        String identifier = ChannelBufferUtils.readString(buffer);
        return new HandshakeMessage(identifier);
    }

    @Override
    public ChannelBuffer encode(HandshakeMessage message) {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        ChannelBufferUtils.writeString(buffer, message.getIdentifier());
        return buffer;
    }

}
