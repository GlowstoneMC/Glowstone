package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import lombok.Data;
import net.glowstone.entity.meta.MetadataMap;

import java.util.List;
import java.util.UUID;

@Data
public final class SpawnPlayerMessage implements Message {

    private final int id;
    private final UUID uuid;
    private final int x, y, z;
    private final int rotation, pitch;
    private final int item;
    private final List<MetadataMap.Entry> metadata;

}
