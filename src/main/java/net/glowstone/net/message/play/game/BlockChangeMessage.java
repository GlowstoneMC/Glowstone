package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

public final class BlockChangeMessage implements Message {

    private final int x, y, z, type;

    public BlockChangeMessage(int x, int y, int z, int type, int metadata) {
        this(x, y, z, (type << 4) | (metadata & 0xf));
    }

    public BlockChangeMessage(int x, int y, int z, int fullType) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = fullType;
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

    @Override
    public String toString() {
        return "BlockChangeMessage{x=" + x + ",y=" + y + ",z=" + z + ",type=" + type + "}";
    }

}
