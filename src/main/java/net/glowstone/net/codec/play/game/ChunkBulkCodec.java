package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.game.ChunkBulkMessage;
import net.glowstone.net.message.play.game.ChunkDataMessage;

import java.io.IOException;
import java.util.List;

public final class ChunkBulkCodec implements Codec<ChunkBulkMessage> {
    public ChunkBulkMessage decode(ByteBuf buffer) throws IOException {
        throw new DecoderException("Cannot decode ChunkBulkMessages");
    }

    public ByteBuf encode(ByteBuf buf, ChunkBulkMessage message) {
        final List<ChunkDataMessage> entries = message.getEntries();

        buf.writeBoolean(message.getSkyLight());
        ByteBufUtils.writeVarInt(buf, entries.size());
        for (ChunkDataMessage entry : entries) {
            buf.writeInt(entry.getX());
            buf.writeInt(entry.getZ());
            buf.writeShort(entry.getPrimaryMask());
        }
        for (ChunkDataMessage entry : entries) {
            buf.writeBytes(entry.getData());
        }

        return buf;

        /*
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
        */
    }
}
