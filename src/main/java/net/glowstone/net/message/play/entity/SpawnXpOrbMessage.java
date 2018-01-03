package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@Data
@RequiredArgsConstructor
public final class SpawnXpOrbMessage implements Message {

    private final int id;
    private final double x;
    private final double y;
    private final double z;
    private final short count;

    public SpawnXpOrbMessage(int id, Location location, short count) {
        this(id, location.getX(), location.getY(), location.getZ(), count);
    }

}
