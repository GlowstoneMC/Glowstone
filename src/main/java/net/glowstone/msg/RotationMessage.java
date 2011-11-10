package net.glowstone.msg;

public final class RotationMessage extends Message {

    private final float rotation, pitch;
    private final boolean onGround;

    public RotationMessage(float rotation, float pitch, boolean onGround) {
        this.rotation = rotation;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public float getRotation() {
        return rotation;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    @Override
    public String toString() {
        return "RotationMessage{rotation=" + rotation + ",pitch=" + pitch + ",onGround=" + onGround + "}";
    }
}
