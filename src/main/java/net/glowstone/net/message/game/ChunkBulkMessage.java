package net.glowstone.net.message.game;

import net.glowstone.GlowChunk;
import net.glowstone.net.message.Message;
import org.jboss.netty.buffer.ChannelBuffer;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.Deflater;

public class ChunkBulkMessage extends Message {

    private final List<ChunkDataMessage> entries = new LinkedList<ChunkDataMessage>();
    private final boolean skyLight;

    public ChunkBulkMessage(boolean skyLight, Collection<GlowChunk> chunks) {
        this.skyLight = skyLight;

        for (GlowChunk chunk : chunks) {
            entries.add(chunk.toMessage(skyLight, true, 0));
        }
    }

    @Override
    public void encode(ChannelBuffer buf) {
        // build the data array
        int inputLength = 0;
        for (ChunkDataMessage entry : entries) {
            inputLength += entry.data.length;
        }
        byte[] input = new byte[inputLength];
        inputLength = 0;
        for (ChunkDataMessage entry : entries) {
            System.arraycopy(entry.data, 0, input, inputLength, entry.data.length);
            inputLength += entry.data.length;
        }

        // compress the whole data
        byte[] compressedData = new byte[inputLength];
        Deflater deflater = new Deflater(ChunkDataMessage.COMPRESSION_LEVEL);
        deflater.setInput(input);
        deflater.finish();
        int compressed = deflater.deflate(compressedData);
        deflater.end();

        if (compressed == 0) {
            throw new RuntimeException("Not all bytes compressed.");
        }

        // write stuff out
        buf.writeShort(entries.size());
        buf.writeInt(compressed);
        buf.writeByte(skyLight ? 1 : 0);
        buf.writeBytes(compressedData, 0, compressed);
        for (ChunkDataMessage entry : entries) {
            buf.writeInt(entry.x);
            buf.writeInt(entry.z);
            buf.writeShort(entry.primaryMask);
            buf.writeShort(entry.addMask);
        }
    }
}
