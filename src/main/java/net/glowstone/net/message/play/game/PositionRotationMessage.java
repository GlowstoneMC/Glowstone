package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@Data
@RequiredArgsConstructor
public final class PositionRotationMessage implements Message {

    private final double x, y, z;
    private final float rotation, pitch;
    private final int flags;

    public PositionRotationMessage(double x, double y, double z, float rotation, float pitch) {
        this(x, y, z, rotation, pitch, 0);
    }

    public PositionRotationMessage(Location location, double yOffset) {
        this(location.getX(), location.getY() + yOffset, location.getZ(), location.getYaw(), location.getPitch());
    }

}
