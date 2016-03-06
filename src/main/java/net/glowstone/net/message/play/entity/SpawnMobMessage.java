package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.entity.meta.MetadataMap.Entry;

import java.util.List;
import java.util.UUID;

@Data
public final class SpawnMobMessage implements Message {

    private final int id;
    private final UUID uuid; //TODO: Handle UUID
    private final int type;
    private final double x, y, z;
    private final int rotation, pitch, headPitch, velX, velY, velZ;
    private final List<Entry> metadata;

}
