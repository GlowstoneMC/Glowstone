package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

import java.util.Arrays;

public final class MultiBlockChangeMessage implements Message {

    private final int chunkX, chunkZ;
    private final BlockChangeMessage[] records;

    public MultiBlockChangeMessage(int chunkX, int chunkZ, BlockChangeMessage[] records) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.records = records;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public BlockChangeMessage[] getRecords() {
        return records;
    }

    @Override
    public String toString() {
        return "MultiBlockChangeMessage{" +
                "chunkX=" + chunkX +
                ", chunkZ=" + chunkZ +
                ", records=" + Arrays.toString(records) +
                '}';
    }
}
