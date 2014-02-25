package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;

public final class ClientStatusMessage implements Message {

    public static final int RESPAWN = 0;
    public static final int REQUEST_STATS = 1;
    public static final int OPEN_INVENTORY = 2;

    private final int action;

    public ClientStatusMessage(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "ClientStatusMessage{" +
                "action=" + action +
                '}';
    }
}

