package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import lombok.Data;
import net.glowstone.entity.meta.MetadataMap;

import java.util.List;

@Data
public final class EntityMetadataMessage implements Message {

    private final int id;
    private final List<MetadataMap.Entry> entries;

}
