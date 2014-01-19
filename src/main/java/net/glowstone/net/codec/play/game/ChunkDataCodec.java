package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.ChunkDataMessage;

import java.io.IOException;
import java.util.zip.Deflater;

public final class ChunkDataCodec implements Codec<ChunkDataMessage> {

    @Override
    public ChunkDataMessage decode(ByteBuf buffer) throws IOException {
        throw new RuntimeException("the fck client?!");
    }

    @Override
    public void encode(ByteBuf buf, ChunkDataMessage message) throws IOException {
        buf.writeInt(message.getX());
        buf.writeInt(message.getZ());
        buf.writeByte(message.isContinuous() ? 1 : 0);
        buf.writeShort(message.getPrimaryMask());
        buf.writeShort(message.getAddMask());

        if (message.getData().length == 0) {
            buf.writeInt(0);
            return;
        }

        byte[] compressedData = new byte[message.getData().length];

        Deflater deflater = new Deflater(ChunkDataMessage.getCompressionLevel());
        deflater.setInput(message.getData());
        deflater.finish();

        int compressed = deflater.deflate(compressedData);
        deflater.end();
        if (compressed == 0) {
            throw new RuntimeException("Not all bytes compressed.");
        }

        buf.writeInt(compressed);
        buf.writeBytes(compressedData, 0, compressed);

    }
}
