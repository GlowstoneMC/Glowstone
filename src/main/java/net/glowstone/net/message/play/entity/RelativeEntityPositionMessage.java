package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;

public final class RelativeEntityPositionMessage implements Message {

    private final int id, deltaX, deltaY, deltaZ;
    private final boolean onGround;

    public RelativeEntityPositionMessage(int id, int deltaX, int deltaY, int deltaZ) {
        this(id, deltaX, deltaY, deltaZ, true);
    }

    public RelativeEntityPositionMessage(int id, int deltaX, int deltaY, int deltaZ, boolean onGround) {
        this.id = id;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.deltaZ = deltaZ;
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

    public boolean getOnGround() {
        return onGround;
    }

    @Override
    public String toString() {
        return "RelativeEntityPositionMessage{" +
                "id=" + id +
                ", deltaX=" + deltaX +
                ", deltaY=" + deltaY +
                ", deltaZ=" + deltaZ +
                ", onGround=" + onGround +
                '}';
    }

}
