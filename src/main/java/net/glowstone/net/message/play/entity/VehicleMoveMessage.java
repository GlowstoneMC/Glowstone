package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.Location;

@Data
public class VehicleMoveMessage implements Message {

    private final double x, y, z;
    private final float yaw, pitch;

    public VehicleMoveMessage(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = (yaw % 360 + 360) % 360;
        this.pitch = pitch;
    }

    public void update(Location location) {
        location.setX(x);
        location.setY(y);
        location.setZ(z);
        location.setYaw(yaw);
        location.setPitch(pitch);
    }
}
