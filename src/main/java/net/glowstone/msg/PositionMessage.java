package net.glowstone.msg;

public final class PositionMessage extends Message {

    private final double x, y, z, stance;
    private final boolean onGround;

    public PositionMessage(double x, double y, double z, double stance, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.stance = stance;
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

    public boolean isOnGround() {
        return onGround;
    }

    @Override
    public String toString() {
        return "PositionMessage{x=" + x + ",y=" + y + ",z=" + z + ",stance=" + stance + ",onGround=" + onGround + "}";
    }
}
