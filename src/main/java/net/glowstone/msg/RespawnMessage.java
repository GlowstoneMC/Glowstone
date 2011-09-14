package net.glowstone.msg;

public final class RespawnMessage extends Message {

    private final byte dimension, unknown, mode;
    private final short worldHeight;
    private final long seed;
    
    public RespawnMessage(byte dimension, byte unknown, byte mode, short worldHeight, long seed) {
        this.dimension = dimension;
        this.unknown = unknown;
        this.mode = mode;
        this.worldHeight = worldHeight;
        this.seed = seed;
    }
    
    public byte getDimension() {
        return dimension;
    }
    
    public byte getUnknown() {
        return unknown;
    }

    public byte getGameMode() {
        return mode;
    }

    public short getWorldHeight() {
        return worldHeight;
    }

    public long getSeed() {
        return seed;
    }
    
}
