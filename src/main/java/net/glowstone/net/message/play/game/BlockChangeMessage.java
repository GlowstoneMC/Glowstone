package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

public final class BlockChangeMessage implements Message {

    private final int x, y, z, type, metadata;

    public BlockChangeMessage(int x, int y, int z, int type, int metadata) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.metadata = metadata;
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

    public int getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "BlockChangeMessage{x=" + x + ",y=" + y +",z=" + z + ",type=" + type + ",metadata=" + metadata + "}";
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
