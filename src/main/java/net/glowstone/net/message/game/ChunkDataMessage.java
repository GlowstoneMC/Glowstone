package net.glowstone.net.message.game;

import net.glowstone.net.message.Message;
import org.jboss.netty.buffer.ChannelBuffer;

import java.util.zip.Deflater;

public final class ChunkDataMessage extends Message {

    private static final int COMPRESSION_LEVEL = Deflater.BEST_SPEED;

    private final int x, z;
    private final boolean continuous;
    private final int primaryMask, addMask;
    private final byte[] data;

    public ChunkDataMessage(int x, int z, boolean continuous, int primaryMask, int addMask, byte[] data) {
        this.x = x;
        this.z = z;
        this.continuous = continuous;
        this.primaryMask = primaryMask;
        this.addMask = addMask;
        this.data = data;
    }

    public static ChunkDataMessage unload(int x, int z) {
        return new ChunkDataMessage(x, z, true, 0, 0, new byte[0]);
    }

    @Override
    public void encode(ChannelBuffer buf) {
        buf.writeInt(x);
        buf.writeInt(z);
        buf.writeByte(continuous ? 1 : 0);
        buf.writeShort(primaryMask);
        buf.writeShort(addMask);

        if (data.length == 0) {
            buf.writeInt(0);
            return;
        }

        byte[] compressedData = new byte[data.length];

        Deflater deflater = new Deflater(COMPRESSION_LEVEL);
        deflater.setInput(data);
        deflater.finish();

        int compressed = deflater.deflate(compressedData);
        deflater.end();
        if (compressed == 0) {
            throw new RuntimeException("Not all bytes compressed.");
        }

        buf.writeInt(compressed);
        buf.writeBytes(compressedData, 0, compressed);
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
}
