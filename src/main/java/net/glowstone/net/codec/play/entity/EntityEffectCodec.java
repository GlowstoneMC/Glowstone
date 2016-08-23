package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.EntityEffectPacket;

import java.io.IOException;

public final class EntityEffectCodec implements Codec<EntityEffectPacket> {
    @Override
    public EntityEffectPacket decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        byte effect = buf.readByte();
        byte amplifier = buf.readByte();
        int duration = ByteBufUtils.readVarInt(buf);
        boolean hideParticles = buf.readBoolean();
        return new EntityEffectPacket(id, effect, amplifier, duration, hideParticles);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityEffectPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getEffect());
        buf.writeByte(message.getAmplifier());
        ByteBufUtils.writeVarInt(buf, message.getDuration());
        buf.writeBoolean(message.isHideParticles());
        return buf;
    }
}
