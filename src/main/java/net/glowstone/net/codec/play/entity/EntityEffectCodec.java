package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.EntityEffectMessage;

import java.io.IOException;

public final class EntityEffectCodec implements Codec<EntityEffectMessage> {
    @Override
    public EntityEffectMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode EntityEffectMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityEffectMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getEffect());
        buf.writeByte(message.getAmplifier());
        ByteBufUtils.writeVarInt(buf, message.getDuration());
        buf.writeBoolean(message.getHideParticles());
        return buf;
    }
}
