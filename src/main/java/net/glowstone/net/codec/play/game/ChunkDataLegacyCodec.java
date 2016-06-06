package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.ChunkDataLegacyMessage;

import java.io.IOException;

public final class ChunkDataLegacyCodec implements Codec<ChunkDataLegacyMessage> {

    @Override
    public ChunkDataLegacyMessage decode(ByteBuf buffer) throws IOException {
        throw new RuntimeException("Cannot decode ChunkDataLegacyMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, ChunkDataLegacyMessage message) throws IOException {
        buf.writeInt(message.getX());
        buf.writeInt(message.getZ());
        buf.writeBoolean(message.isContinuous());
        ByteBufUtils.writeVarInt(buf, message.getPrimaryMask());
        ByteBuf data = message.getData();
        try {
            ByteBufUtils.writeVarInt(buf, data.writerIndex());
            buf.writeBytes(data);
        } finally {
            data.release();
        }
        return buf;
    }
}
