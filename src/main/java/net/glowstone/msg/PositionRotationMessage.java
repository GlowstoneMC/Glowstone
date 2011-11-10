package net.glowstone.msg;

public final class PositionRotationMessage extends Message {

    private final double x, y, z, stance;
    private final float rotation, pitch;
    private final boolean onGround;

    public PositionRotationMessage(double x, double y, double z, double stance, float rotation, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.stance = stance;
        this.rotation = rotation;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getStance() {
        return stance;
    }

    public double getZ() {
        return z;
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
        return "PositionRotationMessage{x=" + x + ",y=" + y + ",z=" + z +
                ",stance=" + stance + ",rotation=" + rotation + ",pitch=" +
                pitch + ",onGround=" + onGround + "}";
    }
}
