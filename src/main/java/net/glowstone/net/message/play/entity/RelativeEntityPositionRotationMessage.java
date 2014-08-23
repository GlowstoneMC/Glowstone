package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;

public final class RelativeEntityPositionRotationMessage implements Message {

    private final int id, deltaX, deltaY, deltaZ, rotation, pitch;
    private final boolean onGround;

    public RelativeEntityPositionRotationMessage(int id, int deltaX, int deltaY, int deltaZ, int rotation, int pitch) {
        this(id, deltaX, deltaY, deltaZ, rotation, pitch, true);
    }

    public RelativeEntityPositionRotationMessage(int id, int deltaX, int deltaY, int deltaZ, int rotation, int pitch, boolean onGround) {
        this.id = id;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.deltaZ = deltaZ;
        this.rotation = rotation;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public int getId() {
        return id;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public int getDeltaZ() {
        return deltaZ;
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
        return "RelativeEntityPositionRotationMessage{" +
                "id=" + id +
                ", deltaX=" + deltaX +
                ", deltaY=" + deltaY +
                ", deltaZ=" + deltaZ +
                ", rotation=" + rotation +
                ", pitch=" + pitch +
                ", onGround=" + onGround +
                '}';
    }

}
