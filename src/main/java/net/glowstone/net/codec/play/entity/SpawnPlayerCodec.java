package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.entity.SpawnPlayerMessage;

import java.io.IOException;

public final class SpawnPlayerCodec implements Codec<SpawnPlayerMessage> {
    @Override
    public SpawnPlayerMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode SpawnPlayerMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnPlayerMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        GlowBufUtils.writeUuid(buf, message.getUuid());
        buf.writeInt(message.getX());
        buf.writeInt(message.getY());
        buf.writeInt(message.getZ());
        buf.writeByte(message.getRotation());
        buf.writeByte(message.getPitch());
        buf.writeShort(message.getItem());
        GlowBufUtils.writeMetadata(buf, message.getMetadata());
        return buf;
    }
}
