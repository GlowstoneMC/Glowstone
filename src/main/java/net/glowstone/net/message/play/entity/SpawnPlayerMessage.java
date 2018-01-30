package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import net.glowstone.entity.meta.MetadataMap.Entry;

@Data
public final class SpawnPlayerMessage implements Message {

    private final int id;
    private final UUID uuid;
    private final double x;
    private final double y;
    private final double z;
    private final int rotation;
    private final int pitch;
    private final List<Entry> metadata;

}
