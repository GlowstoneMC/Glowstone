package net.glowstone.msg;

public final class SpawnPaintingMessage extends Message {

    private final int id, x, y, z, type;
    private final String title;

    public SpawnPaintingMessage(int id, String title, int x, int y, int z, int type) {
        this.id = id;
        this.title = title;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
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

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "SpawnPaintingMessage{id=" + id + ",x=" + x + ",y=" + y + ",z=" + z + ",type=" + type + ",title=" + title + "}";
    }
}
