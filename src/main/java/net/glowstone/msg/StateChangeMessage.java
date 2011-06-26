package net.glowstone.msg;

public final class StateChangeMessage extends Message {

    private final byte state;

    public StateChangeMessage(byte state) {
        this.state = state;
    }

    public byte getState() {
        return state;
    }

}
