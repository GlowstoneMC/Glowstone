package net.glowstone.net.message.play.player;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Location;

@Getter
@EqualsAndHashCode(callSuper = true)
public final class PlayerPositionPacket extends PlayerUpdatePacket {

    private final double x, y, z;

    public PlayerPositionPacket(boolean onGround, double x, double y, double z) {
        super(onGround);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void update(Location location) {
        location.setX(x);
        location.setY(y);
        location.setZ(z);
    }

    @Override
    public boolean moved() {
        return true;
    }

    @Override
    public String toString() {
        return "PlayerPositionMessage(" +
                "onGround=" + isOnGround() +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ')';
    }

}
