package net.glowstone.net.message.game;

import net.glowstone.net.message.Message;
import org.jboss.netty.buffer.ChannelBuffer;

public final class TimeMessage extends Message {

    private final long worldAge, time;

    public TimeMessage(long worldAge, long time) {
        this.worldAge = worldAge;
        this.time = time;
    }

    @Override
    public void encode(ChannelBuffer buf) {
        buf.writeLong(worldAge);
        buf.writeLong(time);
    }

    public long getWorldAge() {
        return worldAge;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "TimeMessage{worldAge=" + worldAge + ",time=" + time + "}";
    }
}
