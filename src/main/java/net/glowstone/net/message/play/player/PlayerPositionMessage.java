package net.glowstone.net.message.play.player;

import org.bukkit.Location;

public final class PlayerPositionMessage extends PlayerUpdateMessage {

    private final double x, y, headY, z;

    public PlayerPositionMessage(boolean onGround, double x, double y, double headY, double z) {
        super(onGround);
        this.x = x;
        this.y = y;
        this.headY = headY;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getHeadY() {
        return headY;
    }

    public double getZ() {
        return z;
    }

    @Override
    public void update(Location location) {
        location.setX(x);
        location.setY(y);
        location.setZ(z);
    }

    @Override
    public String toString() {
        return "PlayerPositionMessage{" +
                "onGround=" + getOnGround() +
                ", x=" + x +
                ", y=" + y +
                ", headY=" + headY +
                ", z=" + z +
                '}';
    }
}
