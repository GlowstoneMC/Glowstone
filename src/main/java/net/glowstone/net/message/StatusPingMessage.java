package net.glowstone.net.message;

import org.jboss.netty.buffer.ChannelBuffer;

public class StatusPingMessage extends Message {

    private final long time;

    public StatusPingMessage(long time) {
        this.time = time;
    }

    public StatusPingMessage(ChannelBuffer buf) {
        time = buf.readLong();
    }

    @Override
    public void encode(ChannelBuffer buf) {
        buf.writeLong(time);
    }

    public long getTime() {
        return time;
    }
}
