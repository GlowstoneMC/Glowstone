package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import net.glowstone.entity.meta.MetadataMap.Entry;

import java.util.List;

@Data
public final class EntityMetadataMessage implements Message {

    private final int id;
    private final List<Entry> entries;

}
