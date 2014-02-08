package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import net.glowstone.entity.meta.MetadataMap;

import java.util.List;

public final class EntityMetadataMessage implements Message {

    private final int id;
    private final List<MetadataMap.Entry> entries;

    public EntityMetadataMessage(int id, List<MetadataMap.Entry> entries) {
        this.id = id;
        this.entries = entries;
    }

    public int getId() {
        return id;
    }

    public List<MetadataMap.Entry> getEntries() {
        return entries;
    }

    @Override
    public String toString() {
        StringBuilder build = new StringBuilder("EntityMetadataMessage{id=");
        for (MetadataMap.Entry entry : entries) {
            build.append(',').append(entry.index).append('=').append(entry.value);
        }
        build.append('}');
        return build.toString();
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
