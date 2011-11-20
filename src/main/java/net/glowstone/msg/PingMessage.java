package net.glowstone.msg;

public final class PingMessage extends Message {
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
}
