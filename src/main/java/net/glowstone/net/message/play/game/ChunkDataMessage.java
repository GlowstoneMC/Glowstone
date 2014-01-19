package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

import java.util.zip.Deflater;

public final class ChunkDataMessage implements Message {

    static final int COMPRESSION_LEVEL = Deflater.DEFAULT_COMPRESSION;

    final int x, z;
    final boolean continuous;
    final int primaryMask, addMask;
    final byte[] data;

    public ChunkDataMessage(int x, int z, boolean continuous, int primaryMask, int addMask, byte[] data) {
        this.x = x;
        this.z = z;
        this.continuous = continuous;
        this.primaryMask = primaryMask;
        this.addMask = addMask;
        this.data = data;
    }

    public static ChunkDataMessage empty(int x, int z) {
        return new ChunkDataMessage(x, z, true, 0, 0, new byte[0]);
    }

    public static int getCompressionLevel() {
        return COMPRESSION_LEVEL;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public boolean isContinuous() {
        return continuous;
    }

    public int getPrimaryMask() {
        return primaryMask;
    }

    public int getAddMask() {
        return addMask;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ChunkDataMessage{" +
                "x=" + x +
                ", z=" + z +
                ", continuous=" + continuous +
                ", primaryMask=" + primaryMask +
                ", addMask=" + addMask +
                ", data[" + data.length + ']' +
                '}';
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
