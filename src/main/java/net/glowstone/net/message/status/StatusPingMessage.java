package net.glowstone.net.message.status;

import com.flowpowered.networking.Message;

public final class StatusPingMessage implements Message {

    private final long time;

    public StatusPingMessage(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
