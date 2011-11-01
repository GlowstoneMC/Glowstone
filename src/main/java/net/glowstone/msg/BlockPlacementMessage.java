package net.glowstone.msg;

import net.glowstone.util.nbt.Tag;

import java.util.Map;

public final class BlockPlacementMessage extends Message {

    private final int id, x, y, z, direction, count, damage;
    private Map<String, Tag> nbtData;

    public BlockPlacementMessage(int x, int y, int z, int direction) {
        this(x, y, z, direction, -1, 0, 0, null);
    }

    public BlockPlacementMessage(int x, int y, int z, int direction, int id, int count, int damage, Map<String, Tag> nbtData) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.direction = direction;
        this.count = count;
        this.damage = damage;
        this.nbtData = nbtData;
    }

    public int getCount() {
        return count;
    }

    public int getDamage() {
        return damage;
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

    public int getDirection() {
        return direction;
    }

    public Map<String, Tag> getNbtData() {
        return nbtData;
    }

    @Override
    public String toString() {
        return "BlockPlacementMessage{x=" + x + ",y=" + y +",z=" + z + ",direction=" + direction + ",id=" + id + ",count=" + count + ",damage=" + damage + ",nbtData="  + nbtData + "}";
    }

}
