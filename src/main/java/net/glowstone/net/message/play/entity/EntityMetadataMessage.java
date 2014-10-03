package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import lombok.Data;
import net.glowstone.entity.meta.MetadataMap;

import java.util.List;

@Data
public final class EntityMetadataMessage implements Message {

    private final int id;
    private final List<MetadataMap.Entry> entries;

    @Override
    public String toString() {
        StringBuilder build = new StringBuilder("EntityMetadataMessage{id=");
        build.append(id);
        for (MetadataMap.Entry entry : entries) {
            build.append(',').append(entry.index).append('=').append(entry.value);
        }
        build.append('}');
        return build.toString();
    }

}
