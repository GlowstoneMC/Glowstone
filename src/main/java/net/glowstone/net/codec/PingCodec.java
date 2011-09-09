package net.glowstone.net.codec;

import net.glowstone.msg.PingMessage;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class PingCodec extends MessageCodec<PingMessage> {

    public PingCodec() {
        super(PingMessage.class, 0x00);
    }

    @Override
    public PingMessage decode(ChannelBuffer buffer) {
        int id = buffer.readInt();
        return new PingMessage(id);
    }

    @Override
    public ChannelBuffer encode(PingMessage message) {
        ChannelBuffer buffer = ChannelBuffers.buffer(5);
        buffer.writeInt(message.getPingId());
        return buffer;
    }

}
