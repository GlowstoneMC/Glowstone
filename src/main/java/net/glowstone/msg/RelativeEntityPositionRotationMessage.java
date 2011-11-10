package net.glowstone.msg;

public final class RelativeEntityPositionRotationMessage extends Message {

    private final int id, deltaX, deltaY, deltaZ, rotation, pitch;

    public RelativeEntityPositionRotationMessage(int id, int deltaX, int deltaY, int deltaZ, int rotation, int pitch) {
        this.id = id;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.deltaZ = deltaZ;
        this.rotation = rotation;
        this.pitch = pitch;
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

    @Override
    public String toString() {
        return "RelativeEntityPositionRotationMessage{id=" + id + ",deltaX=" +
                deltaX + ",deltaY=" + deltaY + ",deltaZ=" + deltaZ + "rotation=" +
                rotation + ",pitch=" + pitch + "}";
    }
}
