package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.game.BlockChangeMessage;
import net.glowstone.net.message.play.game.MultiBlockChangeMessage;

import java.io.IOException;
import java.util.List;

public final class MultiBlockChangeCodec implements Codec<MultiBlockChangeMessage> {
    public MultiBlockChangeMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode MultiBlockChangeMessage");
    }

    public ByteBuf encode(ByteBuf buf, MultiBlockChangeMessage message) throws IOException {
        final List<BlockChangeMessage> records = message.getRecords();

        buf.writeInt(message.getChunkX());
        buf.writeInt(message.getChunkZ());
        buf.writeShort(records.size());
        buf.writeInt(records.size() * 4);

        for (BlockChangeMessage record : records) {
            // XZYYTTTM
            int value = (record.getMetadata() & 0xF) |
                    ((record.getType() & 0xFFF) << 4) |
                    ((record.getY() & 0xFF) << 16) |
                    ((record.getZ() & 0xF) << 24) |
                    ((record.getX() & 0xF) << 28);
            buf.writeInt(value);
        }
        return buf;
    }
}
