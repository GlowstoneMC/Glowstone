package net.glowstone.msg;

public class ExperienceOrbMessage extends Message {
    private final int id, x, y, z;
    private final short count;

    public ExperienceOrbMessage(int id, int x, int y, int z, short count) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.count = count;
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

    public short getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "ExperienceOrbMessage{id=" + id + ",x=" + x + ",y=" + y + ",z=" + z + ",count=" + count + "}";
    }
}
