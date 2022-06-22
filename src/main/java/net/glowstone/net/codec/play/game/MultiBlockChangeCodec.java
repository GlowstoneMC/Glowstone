package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
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
        List<BlockChangeMessage> records = message.getRecords();

        buf.writeLong(message.getSectionPosition().asLong());
        buf.writeBoolean(message.isSuppressLightUpdates());

        int size = records.size();
        ByteBufUtils.writeVarInt(buf, size);

        for (int i = 0; i < size; i++) {
            BlockChangeMessage record = records.get(i);
            // TypeIdXZY
            long typeAndPos = ((long) record.getType() << 12) | (record.getX() & 0xF) << 8 | (record.getZ() & 0xF) << 4 | record.getY() & 0xFF;
            ByteBufUtils.writeVarLong(buf, typeAndPos);
        }
        return buf;
    }
}
