package net.glowstone.net.codec;

import net.glowstone.msg.KickMessage;
import net.glowstone.util.ChannelBufferUtils;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class KickCodec extends MessageCodec<KickMessage> {

    public KickCodec() {
        super(KickMessage.class, 0xFF);
    }

    @Override
    public KickMessage decode(ChannelBuffer buffer) {
        return new KickMessage(ChannelBufferUtils.readString(buffer));
    }

    @Override
    public ChannelBuffer encode(KickMessage message) {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        ChannelBufferUtils.writeString(buffer, message.getReason());
        return buffer;
    }

}
