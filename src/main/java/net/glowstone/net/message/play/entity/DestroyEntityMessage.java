package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;

public final class DestroyEntityMessage implements Message {

    private final int id;

    public DestroyEntityMessage(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "DestroyEntityMessage{id=" + id + "}";
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
