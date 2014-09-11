package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.entity.SpawnMobMessage;

import java.io.IOException;

public final class SpawnMobCodec implements Codec<SpawnMobMessage> {
    @Override
    public SpawnMobMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode SpawnMobMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnMobMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getType());
        buf.writeInt(message.getX());
        buf.writeInt(message.getY());
        buf.writeInt(message.getZ());
        buf.writeByte(message.getHeadPitch());
        buf.writeByte(message.getPitch());
        buf.writeByte(message.getRotation());
        buf.writeShort(message.getVelX());
        buf.writeShort(message.getVelY());
        buf.writeShort(message.getVelZ());
        GlowBufUtils.writeMetadata(buf, message.getMetadata());
        return buf;
    }
}
