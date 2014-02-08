package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.entity.SpawnPlayerMessage;
import net.glowstone.util.UuidUtils;

import java.io.IOException;

public final class SpawnPlayerCodec implements Codec<SpawnPlayerMessage> {
    public SpawnPlayerMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode SpawnPlayerMessage");
    }

    public ByteBuf encode(ByteBuf buf, SpawnPlayerMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        ByteBufUtils.writeUTF8(buf, UuidUtils.toFlatString(message.getUuid()));
        ByteBufUtils.writeUTF8(buf, message.getName());
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
