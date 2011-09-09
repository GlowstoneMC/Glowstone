package net.glowstone.msg;

public final class StateChangeMessage extends Message {

    private final byte state, gameMode;

    public StateChangeMessage(byte state, byte gameMode) {
        this.state = state;
        this.gameMode = gameMode;
    }

    public byte getState() {
        return state;
    }

    public byte getGameMode() {
        return gameMode;
    }

}
