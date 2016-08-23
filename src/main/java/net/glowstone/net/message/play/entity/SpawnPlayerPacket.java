package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import net.glowstone.entity.meta.MetadataMap.Entry;

import java.util.List;
import java.util.UUID;

@Data
public final class SpawnPlayerPacket implements Message {

    private final int id;
    private final UUID uuid;
    private final double x, y, z;
    private final int rotation, pitch;
    private final List<Entry> metadata;

}
