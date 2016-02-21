package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import lombok.Data;
import net.glowstone.entity.meta.MetadataMap;

import java.util.List;
import java.util.UUID;

@Data
public final class SpawnMobMessage implements Message {

    private final int id;
    private final UUID uuid; //TODO: Handle UUID
    private final int type, x, y, z, rotation, pitch, headPitch, velX, velY, velZ;
    private final List<MetadataMap.Entry> metadata;

}
