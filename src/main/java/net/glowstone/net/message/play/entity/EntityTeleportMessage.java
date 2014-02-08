package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;

public final class EntityTeleportMessage implements Message {

    private final int id, x, y, z, rotation, pitch;

    public EntityTeleportMessage(int id, int x, int y, int z, int rotation, int pitch) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotation = rotation;
        this.pitch = pitch;
    }

    public int getId() {
        return id;
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

    @Override
    public String toString() {
        return "EntityTeleportMessage{id=" + id + ",x=" + x + ",y=" + y + ",z=" + z + ",rotation=" + rotation + ",pitch=" + pitch + "}";
    }

}
