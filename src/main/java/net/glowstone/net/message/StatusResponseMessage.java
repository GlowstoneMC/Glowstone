package net.glowstone.net.message;

import net.glowstone.util.ChannelBufferUtils;
import org.jboss.netty.buffer.ChannelBuffer;

public class StatusResponseMessage extends Message {

    private final String json;

    public StatusResponseMessage(String json) {
        this.json = json;
    }

    @Override
    public void encode(ChannelBuffer buf) {
        ChannelBufferUtils.writeString(buf, json);
    }
}
