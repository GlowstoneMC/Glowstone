package net.glowstone.msg;

public final class PlayEffectMessage extends Message {

    private final int id;
    private final int x, y, z;
    private final int data;

    public PlayEffectMessage(int id, int x, int y, int z, int data) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.data = data;
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
    
    public int getData() {
        return data;
    }

    @Override
    public String toString() {
        return "PlayEffectMessage{id=" + id + ",x=" + x + ",y=" + y + ",z=" + z + ",data=" + data + "}";
    }
}
