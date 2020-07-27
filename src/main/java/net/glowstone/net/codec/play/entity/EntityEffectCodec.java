package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.entity.EntityEffectMessage;

public final class EntityEffectCodec implements Codec<EntityEffectMessage> {

    @Override
    public EntityEffectMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        byte effect = buf.readByte();
        byte amplifier = buf.readByte();
        int duration = ByteBufUtils.readVarInt(buf);
        byte flags = buf.readByte();
        boolean ambient = (flags & 1) == 1;
        boolean showParticles = (flags & 2) == 2;
        return new EntityEffectMessage(id, effect, amplifier, duration, showParticles, ambient);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityEffectMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getEffect());
        buf.writeByte(message.getAmplifier());
        ByteBufUtils.writeVarInt(buf, message.getDuration());

        byte flags = 0;
        if (message.isAmbient()) {
            flags |= 1;
        }
        if (message.isShowParticles()) {
            flags |= 2;
        }

        buf.writeByte(flags);
        return buf;
    }
}
