package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.Location;

@Data
public class VehicleMoveMessage implements Message {

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    /**
     * Creates a message.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @param yaw the yaw angle
     * @param pitch the pitch angle
     */
    public VehicleMoveMessage(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = (yaw % 360 + 360) % 360;
        this.pitch = pitch;
    }

    /**
     * Copies this message's position and orientation to a {@link Location}.
     *
     * @param location the location to update
     */
    public void update(Location location) {
        location.setX(x);
        location.setY(y);
        location.setZ(z);
        location.setYaw(yaw);
        location.setPitch(pitch);
    }
}
