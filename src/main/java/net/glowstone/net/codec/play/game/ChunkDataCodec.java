package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.ChunkDataPacket;
import net.glowstone.util.nbt.CompoundTag;

import java.io.IOException;

public final class ChunkDataCodec implements Codec<ChunkDataPacket> {

    @Override
    public ChunkDataPacket decode(ByteBuf buffer) throws IOException {
        throw new RuntimeException("Cannot decode ChunkDataMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, ChunkDataPacket message) throws IOException {
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
        ByteBufUtils.writeVarInt(buf, message.getTileEntities().length);
        for (CompoundTag tag : message.getTileEntities()) {
            GlowBufUtils.writeCompound(buf, tag);
        }
        return buf;
    }
}
