package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

import java.util.Arrays;
import java.util.List;

public final class MultiBlockChangeMessage implements Message {

    private final int chunkX, chunkZ;
    private final List<BlockChangeMessage> records;

    public MultiBlockChangeMessage(int chunkX, int chunkZ, List<BlockChangeMessage> records) {
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

    public List<BlockChangeMessage> getRecords() {
        return records;
    }

    @Override
    public String toString() {
        return "MultiBlockChangeMessage{" +
                "chunkX=" + chunkX +
                ", chunkZ=" + chunkZ +
                ", records=" + records +
                '}';
    }
}
