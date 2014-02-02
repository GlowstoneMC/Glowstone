package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.game.ChunkBulkMessage;
import net.glowstone.net.message.play.game.ChunkDataMessage;

import java.io.IOException;
import java.util.List;
import java.util.zip.Deflater;

public final class ChunkBulkCodec implements Codec<ChunkBulkMessage> {
    public ChunkBulkMessage decode(ByteBuf buffer) throws IOException {
        throw new DecoderException("Cannot decode ChunkBulkMessages");
    }

    public ByteBuf encode(ByteBuf buf, ChunkBulkMessage message) {
        List<ChunkDataMessage> entries = message.getEntries();
        boolean skyLight = message.getSkyLight();

        // build the data array
        int inputLength = 0;
        for (ChunkDataMessage entry : entries) {
            inputLength += entry.getData().length;
        }
        byte[] input = new byte[inputLength];
        inputLength = 0;
        for (ChunkDataMessage entry : entries) {
            byte[] data = entry.getData();
            System.arraycopy(data, 0, input, inputLength, data.length);
            inputLength += data.length;
        }

        // compress the whole data
        byte[] compressedData = new byte[inputLength];
        Deflater deflater = new Deflater(ChunkDataCodec.COMPRESSION_LEVEL);
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
            buf.writeInt(entry.getX());
            buf.writeInt(entry.getZ());
            buf.writeShort(entry.getPrimaryMask());
            buf.writeShort(entry.getAddMask());
        }

        return buf;
    }
}
