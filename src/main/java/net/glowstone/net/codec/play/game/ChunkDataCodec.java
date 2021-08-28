package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
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
        ByteBufUtils.writeVarInt(buf, message.getPrimaryMask());
        ByteBuf data = message.getData();
        try {
            ByteBufUtils.writeVarInt(buf, data.writerIndex());
            buf.writeBytes(data);
        } finally {
            data.release();
        }
        // TODO: Re-enable block entities (1.13)
        ByteBufUtils.writeVarInt(buf, 0);
        // for (CompoundTag tag : message.getBlockEntities()) {
        //     GlowBufUtils.writeCompound(buf, tag);
        // }
        return buf;
    }
}
