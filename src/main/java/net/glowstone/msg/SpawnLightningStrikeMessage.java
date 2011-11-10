package net.glowstone.msg;

public final class SpawnLightningStrikeMessage extends Message {

    private final int id, mode, x, y, z;

    public SpawnLightningStrikeMessage(int id, int x, int y, int z) {
        this(id, 1, x, y, z);
    }

    public SpawnLightningStrikeMessage(int id, int mode, int x, int y, int z) {
        this.id = id;
        this.mode = mode;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getId() {
        return id;
    }

    public int getMode() {
        return mode;
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

    @Override
    public String toString() {
        return "SpawnLightningStrikeMessage{id=" + id + ",mode=" + mode + ",x=" + x + ",y=" + y + ",z=" + z + "}";
    }
}
