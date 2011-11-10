package net.glowstone.msg;

public final class RespawnMessage extends Message {

    private final byte dimension, difficulty, mode;
    private final short worldHeight;
    private final long seed;
    
    public RespawnMessage(byte dimension, byte difficulty, byte mode, short worldHeight, long seed) {
        this.dimension = dimension;
        this.difficulty = difficulty;
        this.mode = mode;
        this.worldHeight = worldHeight;
        this.seed = seed;
    }
    
    public byte getDimension() {
        return dimension;
    }
    
    public byte getDifficulty() {
        return difficulty;
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

    @Override
    public String toString() {
        return "RespawnMessage{dimension=" + dimension + ",difficulty=" + difficulty + ",gameMode=" + mode + ",worldHeight=" + worldHeight + ",seed=" + seed + "}";
    }
}
