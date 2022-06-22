package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@Data
@RequiredArgsConstructor
public final class PositionRotationMessage implements Message {

    private final double x;
    private final double y;
    private final double z;
    private final float rotation;
    private final float pitch;
    private final int flags;
    private final int teleportId;
    private final boolean dismountVehicle;

    public PositionRotationMessage(double x, double y, double z, float rotation, float pitch) {
        this(x, y, z, rotation, pitch, 0, 0, false);
    }

    public PositionRotationMessage(Location location) {
        this(location.getX(), location.getY(), location.getZ(), location.getYaw(),
            location.getPitch());
    }

}
