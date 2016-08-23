package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@Data
@RequiredArgsConstructor
public final class PositionRotationPacket implements Message {

    private final double x, y, z;
    private final float rotation, pitch;
    private final int flags, teleportID;

    public PositionRotationPacket(double x, double y, double z, float rotation, float pitch) {
        this(x, y, z, rotation, pitch, 0, 0);
    }

    public PositionRotationPacket(Location location) {
        this(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

}
