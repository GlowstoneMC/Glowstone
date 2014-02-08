package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import net.glowstone.entity.meta.MetadataMap;

import java.util.List;

public final class SpawnMobMessage implements Message {

    private final int id, type, x, y, z, rotation, pitch;
    private final List<MetadataMap.Entry> metadata;

    public SpawnMobMessage(int id, int type, int x, int y, int z, int rotation, int pitch, List<MetadataMap.Entry> metadata) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotation = rotation;
        this.pitch = pitch;
        this.metadata = metadata;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getRotation() {
        return rotation;
    }

    public int getPitch() {
        return pitch;
    }

    public List<MetadataMap.Entry> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        StringBuilder build = new StringBuilder("SpawnMobMessage{id=").append(id).
                append(",type=").append(type).append(",x=").append(x).append(",y=").
                append(y).append(",z=").append(z).append(",rotation=").
                append(rotation).append(",pitch=").append(pitch).append(",metadata=[");
        for (MetadataMap.Entry entry : metadata) {
            build.append(entry.index).append('=').append(entry.value).append(",");
        }
        build.append("]}");
        return build.toString();
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
