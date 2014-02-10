package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;

public final class InteractEntityMessage implements Message {

    private final int id;
    private final int action;

    public InteractEntityMessage(int id, int action) {
        this.id = id;
        this.action = action;
    }

    public int getId() {
        return id;
    }

    public int getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "InteractEntityMessage{" +
                "id=" + id +
                ", action=" + action +
                '}';
    }
}

