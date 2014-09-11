package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.EntityVelocityMessage;

import java.io.IOException;

public final class EntityVelocityCodec implements Codec<EntityVelocityMessage> {
    @Override
    public EntityVelocityMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode EntityVelocityMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityVelocityMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeShort(message.getVelocityX());
        buf.writeShort(message.getVelocityY());
        buf.writeShort(message.getVelocityZ());
        return buf;
    }
}
