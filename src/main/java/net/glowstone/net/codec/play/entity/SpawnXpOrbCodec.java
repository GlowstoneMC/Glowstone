package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.SpawnXpOrbMessage;

import java.io.IOException;

public final class SpawnXpOrbCodec implements Codec<SpawnXpOrbMessage> {
    @Override
    public SpawnXpOrbMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode SpawnXpOrbMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnXpOrbMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeInt(message.getX());
        buf.writeInt(message.getY());
        buf.writeInt(message.getZ());
        buf.writeShort(message.getCount());
        return buf;
    }
}
