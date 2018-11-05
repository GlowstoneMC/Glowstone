package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.ChunkDataMessage;
import net.glowstone.util.nbt.CompoundTag;

public final class ChunkDataCodec implements Codec<ChunkDataMessage> {

    @Override
    public ChunkDataMessage decode(CodecContext codecContext, ByteBuf buffer) throws IOException {
        throw new RuntimeException("Cannot decode ChunkDataMessage");
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, ChunkDataMessage message) throws IOException {
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
        ByteBufUtils.writeVarInt(buf, message.getBlockEntities().size());
        for (CompoundTag tag : message.getBlockEntities()) {
            GlowBufUtils.writeCompound(buf, tag);
        }
        return buf;
    }
}
