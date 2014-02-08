package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import net.glowstone.entity.meta.MetadataMap;

import java.util.List;
import java.util.UUID;

public final class SpawnPlayerMessage implements Message {

    private final int id;
    private final UUID uuid;
    private final String name;
    private final int x, y, z;
    private final int rotation, pitch;
    private final int item;
    private final List<MetadataMap.Entry> metadata;

    public SpawnPlayerMessage(int id, UUID uuid, String name, int x, int y, int z, int rotation, int pitch, int item, List<MetadataMap.Entry> metadata) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotation = rotation;
        this.pitch = pitch;
        this.item = item;
        this.metadata = metadata;
    }

    public int getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
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

    public int getItem() {
        return item;
    }

    public List<MetadataMap.Entry> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "SpawnPlayerMessage{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", rotation=" + rotation +
                ", pitch=" + pitch +
                ", item=" + item +
                ", metadata=" + metadata +
                '}';
    }

}
