package net.glowstone.net.message.play.player;

import org.bukkit.Location;

public final class PlayerPositionMessage extends PlayerUpdateMessage {

    private final double x, stance, y, z;

    public PlayerPositionMessage(boolean onGround, double x, double stance, double y, double z) {
        super(onGround);
        this.x = x;
        this.stance = stance;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getStance() {
        return stance;
    }

    public double getY() {
        return y;
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
}
