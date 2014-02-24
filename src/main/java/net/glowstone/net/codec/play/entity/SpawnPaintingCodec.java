package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.SpawnPaintingMessage;

import java.io.IOException;

public final class SpawnPaintingCodec implements Codec<SpawnPaintingMessage> {
    public SpawnPaintingMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode SpawnPaintingMessage");
    }

    public ByteBuf encode(ByteBuf buf, SpawnPaintingMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        ByteBufUtils.writeUTF8(buf, message.getTitle());
        buf.writeInt(message.getX());
        buf.writeInt(message.getY());
        buf.writeInt(message.getZ());
        buf.writeInt(message.getFacing());
        return buf;
    }
}
