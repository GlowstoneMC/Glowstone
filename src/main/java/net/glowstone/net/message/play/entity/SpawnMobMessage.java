package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.glowstone.entity.meta.MetadataMap.Entry;
import net.glowstone.util.Position;
import org.bukkit.Location;

@Data
@RequiredArgsConstructor
public final class SpawnMobMessage implements Message {

    private final int id;
    private final UUID uuid; //TODO: Handle UUID
    private final int type;
    private final double x;
    private final double y;
    private final double z;
    private final int rotation;
    private final int pitch;
    private final int headPitch;
    private final int velX;
    private final int velY;
    private final int velZ;
    private final List<Entry> metadata;

    /**
     * Creates an instance based on a {@link Location}, with headPitch equal to pitch and with zero
     * velocity.
     * @param id the mob's ID within the world
     * @param uuid the mob's UUID
     * @param type the mob's network type ID
     * @param location the mob's position, pitch and yaw
     * @param metadata the mob's metadata
     */
    public SpawnMobMessage(int id, UUID uuid, int type, Location location, List<Entry> metadata) {
        this(id, uuid, type,
                location.getX(), location.getY(), location.getZ(),
                Position.getIntYaw(location), Position.getIntPitch(location),
                Position.getIntPitch(location),
                0, 0, 0, metadata);
    }
}
