package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;

public final class RelativeEntityPositionMessage implements Message {

    private final int id, deltaX, deltaY, deltaZ;

    public RelativeEntityPositionMessage(int id, int deltaX, int deltaY, int deltaZ) {
        this.id = id;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.deltaZ = deltaZ;
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

    @Override
    public String toString() {
        return "RelativeEntityPositionMessage{id=" + id + ",deltaX=" + deltaX + ",deltaY=" + deltaY + ",deltaZ=" + deltaZ + "}";
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
