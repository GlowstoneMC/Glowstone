package net.glowstone.msg;

public final class SpawnPlayerMessage extends Message {

    private final int id;
    private final String name;
    private final int x, y, z;
    private final int rotation, pitch;
    private final int item;

    public SpawnPlayerMessage(int id, String name, int x, int y, int z, int rotation, int pitch, int item) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotation = rotation;
        this.pitch = pitch;
        this.item = item;
    }

    public int getId() {
        return id;
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

    @Override
    public String toString() {
        return "SpawnPlayerMessage{id=" + id + ",name=" + name + ",x=" + x + ",y=" + y + ",z=" + z + ",rotation=" + rotation + ",pitch=" + pitch + ",item=" + item + "}";
    }
}
