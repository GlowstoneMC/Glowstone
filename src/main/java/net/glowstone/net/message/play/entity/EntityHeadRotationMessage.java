package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;

public final class EntityHeadRotationMessage implements Message {

    private final int id;
    private final int rotation;

    public EntityHeadRotationMessage(int id, int rotation) {
        this.id = id;
        this.rotation = rotation;
    }

    public int getId() {
        return id;
    }

    public int getRotation() {
        return rotation;
    }

}

