package net.glowstone.net.message.play.player;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Location;

@Getter
@EqualsAndHashCode(callSuper = true)
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
        return "PlayerPositionLookMessage(" +
                "onGround=" + isOnGround() +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                ')';
    }

}
