package net.glowstone.msg;

public final class KickMessage extends Message {

    private final String reason;

    public KickMessage(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "KickMessage{reason=" + reason + "}";
    }
}
