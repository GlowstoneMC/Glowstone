package net.glowstone.net.message.play.player;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Location;

@Getter
@EqualsAndHashCode(callSuper = true)
public final class PlayerPositionMessage extends PlayerUpdateMessage {

    private final double x;
    private final double y;
    private final double z;

    /**
     * Creates a message to update a player's location.
     *
     * @param x the player's X coordinate
     * @param y the player's Y coordinate
     * @param z the player's Z coordinate
     * @param onGround whether the player is on the ground
     */
    public PlayerPositionMessage(boolean onGround, double x, double y, double z) {
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
        return "PlayerPositionMessage("
            + "onGround=" + isOnGround()
            + ", x=" + x
            + ", y=" + y
            + ", z=" + z
            + ')';
    }

}
