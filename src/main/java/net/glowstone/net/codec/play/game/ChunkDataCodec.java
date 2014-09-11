package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.ChunkDataMessage;

import java.io.IOException;

public final class ChunkDataCodec implements Codec<ChunkDataMessage> {

    @Override
    public ChunkDataMessage decode(ByteBuf buffer) throws IOException {
        throw new RuntimeException("Cannot decode ChunkDataMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, ChunkDataMessage message) throws IOException {
        buf.writeInt(message.getX());
        buf.writeInt(message.getZ());
        buf.writeBoolean(message.isContinuous());
        buf.writeShort(message.getPrimaryMask());

        ByteBufUtils.writeVarInt(buf, message.getData().length);
        buf.writeBytes(message.getData());
        return buf;
    }
}
