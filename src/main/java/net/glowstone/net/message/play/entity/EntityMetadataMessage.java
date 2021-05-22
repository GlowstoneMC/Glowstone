package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import java.util.List;
import lombok.Data;
import net.glowstone.entity.meta.MetadataMap.Entry;

@Data
public final class EntityMetadataMessage implements Message {

    private final int id;
    private final List<Entry> entries;

}
