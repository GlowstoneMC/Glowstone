package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.entity.meta.PlayerProfile;

import java.util.List;

public final class SpawnPlayerMessage implements Message {

    private final int id;
    private final PlayerProfile profile;
    private final int x, y, z;
    private final int rotation, pitch;
    private final int item;
    private final List<MetadataMap.Entry> metadata;

    public SpawnPlayerMessage(int id, PlayerProfile profile, int x, int y, int z, int rotation, int pitch, int item, List<MetadataMap.Entry> metadata) {
        this.id = id;
        this.profile = profile;
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

    public PlayerProfile getProfile() {
        return profile;
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

    public String toString() {
        return "SpawnPlayerMessage{" +
                "id=" + id +
                ", profile=" + profile +
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
