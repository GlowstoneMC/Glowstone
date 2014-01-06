package net.glowstone.net.message.game;

import net.glowstone.net.message.Message;
import net.glowstone.util.ChannelBufferUtils;
import org.jboss.netty.buffer.ChannelBuffer;

public final class IncomingChatMessage extends Message {

    private final String text;

    public IncomingChatMessage(ChannelBuffer buf) {
        text = ChannelBufferUtils.readString(buf);
    }

    @Override
    public void encode(ChannelBuffer buf) {
        ChannelBufferUtils.writeString(buf, text);
    }

    public String getText() {
        return text;
    }
}
