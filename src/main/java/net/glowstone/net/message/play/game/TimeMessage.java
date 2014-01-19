package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

public final class TimeMessage implements Message {

    private final long worldAge, time;

    public TimeMessage(long worldAge, long time) {
        this.worldAge = worldAge;
        this.time = time;
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

    @Override
    public boolean isAsync() {
        return false;
    }
}
