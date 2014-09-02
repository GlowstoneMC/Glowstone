package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

public final class ChunkDataMessage implements Message {

    private final int x, z;
    private final boolean continuous;
    private final int primaryMask;
    private final byte[] data;

    public ChunkDataMessage(int x, int z, boolean continuous, int primaryMask, byte[] data) {
        this.x = x;
        this.z = z;
        this.continuous = continuous;
        this.primaryMask = primaryMask;
        this.data = data;
    }

    public static ChunkDataMessage empty(int x, int z) {
        return new ChunkDataMessage(x, z, true, 0, new byte[0]);
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
                ", data[" + data.length + ']' +
                '}';
    }

}
