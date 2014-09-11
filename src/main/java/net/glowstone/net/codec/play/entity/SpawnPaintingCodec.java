package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.entity.SpawnPaintingMessage;

import java.io.IOException;

public final class SpawnPaintingCodec implements Codec<SpawnPaintingMessage> {
    @Override
    public SpawnPaintingMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode SpawnPaintingMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnPaintingMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        ByteBufUtils.writeUTF8(buf, message.getTitle());
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        buf.writeByte(message.getFacing());
        return buf;
    }
}
