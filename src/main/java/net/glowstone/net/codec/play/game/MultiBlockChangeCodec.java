package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.game.BlockChangePacket;
import net.glowstone.net.message.play.game.MultiBlockChangePacket;

import java.io.IOException;
import java.util.List;

public final class MultiBlockChangeCodec implements Codec<MultiBlockChangePacket> {
    @Override
    public MultiBlockChangePacket decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode MultiBlockChangeMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, MultiBlockChangePacket message) throws IOException {
        List<BlockChangePacket> records = message.getRecords();

        buf.writeInt(message.getChunkX());
        buf.writeInt(message.getChunkZ());
        ByteBufUtils.writeVarInt(buf, records.size());

        for (BlockChangePacket record : records) {
            // XZY
            int pos = (record.getX() & 0xF) << 12 |
                    (record.getZ() & 0xF) << 8 |
                    record.getY() & 0xFF;
            buf.writeShort(pos);
            ByteBufUtils.writeVarInt(buf, record.getType());
        }
        return buf;
    }
}
