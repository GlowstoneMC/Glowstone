package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

public final class PingMessage implements Message {

    private final int pingId;

    public PingMessage(int pingId) {
        this.pingId = pingId;
    }

    public int getPingId() {
        return pingId;
    }

    @Override
    public String toString() {
        return "PingMessage{id=" + pingId + "}";
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
