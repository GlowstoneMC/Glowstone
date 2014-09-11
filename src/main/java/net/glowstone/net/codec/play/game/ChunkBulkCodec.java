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
    @Override
    public ChunkBulkMessage decode(ByteBuf buffer) throws IOException {
        throw new DecoderException("Cannot decode ChunkBulkMessage");
    }

    @Override
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
    }
}
