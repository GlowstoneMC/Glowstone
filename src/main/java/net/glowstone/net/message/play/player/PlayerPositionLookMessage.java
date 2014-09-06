package net.glowstone.net.message.play.player;

import org.bukkit.Location;

public final class PlayerPositionLookMessage extends PlayerUpdateMessage {

    private final double x, y, z;
    private final float yaw, pitch;

    public PlayerPositionLookMessage(boolean onGround, double x, double y, double z, float yaw, float pitch) {
        super(onGround);
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = (yaw % 360 + 360) % 360;
        this.pitch = pitch;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    @Override
    public void update(Location location) {
        location.setX(x);
        location.setY(y);
        location.setZ(z);
        location.setYaw(yaw);
        location.setPitch(pitch);
    }

    @Override
    public String toString() {
        return "PlayerPositionLookMessage{" +
                "onGround=" + getOnGround() +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }
}
