package net.glowstone.net.message.game;

import net.glowstone.net.message.Message;
import org.jboss.netty.buffer.ChannelBuffer;

public final class PingMessage extends Message {
    private final int pingId;

    public PingMessage(int pingId) {
        this.pingId = pingId;
    }

    public PingMessage(ChannelBuffer buf) {
        pingId = buf.readInt();
    }

    @Override
    public void encode(ChannelBuffer buf) {
        buf.writeInt(pingId);
    }

    public int getPingId() {
        return pingId;
    }

    @Override
    public String toString() {
        return "PingMessage{id=" + pingId + "}";
    }
}
