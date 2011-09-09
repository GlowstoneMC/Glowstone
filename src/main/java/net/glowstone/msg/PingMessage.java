package net.glowstone.msg;

public final class PingMessage extends Message {
    private int pingId;

    public PingMessage(int pingId) {
        this.pingId = pingId;
    }

    public int getPingId() {
        return pingId;
    }

}
