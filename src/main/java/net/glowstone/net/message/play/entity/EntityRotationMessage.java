package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;

public final class EntityRotationMessage implements Message {

    private final int id, rotation, pitch;
    private final boolean onGround;

    public EntityRotationMessage(int id, int rotation, int pitch) {
        this(id, rotation, pitch, true);
    }

    public EntityRotationMessage(int id, int rotation, int pitch, boolean onGround) {
        this.id = id;
        this.rotation = rotation;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public int getId() {
        return id;
    }

    public int getRotation() {
        return rotation;
    }

    public int getPitch() {
        return pitch;
    }

    public boolean getOnGround() {
        return onGround;
    }

    @Override
    public String toString() {
        return "EntityRotationMessage{" +
                "id=" + id +
                ", rotation=" + rotation +
                ", pitch=" + pitch +
                ", onGround=" + onGround +
                '}';
    }

}
