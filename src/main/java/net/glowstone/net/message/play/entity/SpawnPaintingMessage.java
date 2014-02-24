package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;

public final class SpawnPaintingMessage implements Message {

    private final int id, x, y, z, facing;
    private final String title;

    public SpawnPaintingMessage(int id, String title, int x, int y, int z, int facing) {
        this.id = id;
        this.title = title;
        this.x = x;
        this.y = y;
        this.z = z;
        this.facing = facing;
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

    public int getFacing() {
        return facing;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "SpawnPaintingMessage{id=" + id + ",x=" + x + ",y=" + y + ",z=" + z + ",facing=" + facing + ",title=" + title + "}";
    }
}
