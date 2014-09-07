package net.glowstone.net.message.status;

import com.flowpowered.networking.AsyncableMessage;

public final class StatusPingMessage implements AsyncableMessage {

    private final long time;

    public StatusPingMessage(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

}
