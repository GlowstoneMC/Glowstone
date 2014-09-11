package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.game.BlockChangeMessage;
import net.glowstone.net.message.play.game.MultiBlockChangeMessage;

import java.io.IOException;
import java.util.List;

public final class MultiBlockChangeCodec implements Codec<MultiBlockChangeMessage> {
    @Override
    public MultiBlockChangeMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode MultiBlockChangeMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, MultiBlockChangeMessage message) throws IOException {
        final List<BlockChangeMessage> records = message.getRecords();

        buf.writeInt(message.getChunkX());
        buf.writeInt(message.getChunkZ());
        ByteBufUtils.writeVarInt(buf, records.size());

        for (BlockChangeMessage record : records) {
            // XZYY
            int pos = ((record.getX() & 0xF) << 12) |
                    ((record.getZ() & 0xF) << 8) |
                    (record.getY() & 0xFF);
            buf.writeShort(pos);
            ByteBufUtils.writeVarInt(buf, record.getType());
        }
        return buf;
    }
}
