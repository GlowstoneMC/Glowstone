package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import net.glowstone.entity.meta.MetadataMap;

import java.util.List;

public final class SpawnMobMessage implements Message {

    private final int id, type, x, y, z, rotation, pitch, headPitch, velX, velY, velZ;
    private final List<MetadataMap.Entry> metadata;

    public SpawnMobMessage(int id, int type, int x, int y, int z, int rotation, int pitch, int headPitch, int velX, int velY, int velZ, List<MetadataMap.Entry> metadata) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotation = rotation;
        this.pitch = pitch;
        this.headPitch = pitch;
        this.velX = velX;
        this.velY = velY;
        this.velZ = velZ;
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

    public int getHeadPitch() {
        return headPitch;
    }

    public int getVelX() {
        return velX;
    }

    public int getVelY() {
        return velY;
    }

    public int getVelZ() {
        return velZ;
    }

    public List<MetadataMap.Entry> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "SpawnMobMessage{" +
                "id=" + id +
                ", type=" + type +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", rotation=" + rotation +
                ", pitch=" + pitch +
                ", headPitch=" + headPitch +
                ", velX=" + velX +
                ", velY=" + velY +
                ", velZ=" + velZ +
                ", metadata=" + metadata +
                '}';
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
