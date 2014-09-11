package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.SpawnLightningStrikeMessage;

import java.io.IOException;

public final class SpawnLightningStrikeCodec implements Codec<SpawnLightningStrikeMessage> {
    @Override
    public SpawnLightningStrikeMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode SpawnLightningStrikeMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnLightningStrikeMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getMode());
        buf.writeInt(message.getX());
        buf.writeInt(message.getY());
        buf.writeInt(message.getZ());
        return buf;
    }
}
