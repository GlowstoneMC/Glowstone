package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.EntityRemoveEffectPacket;

import java.io.IOException;

public final class EntityRemoveEffectCodec implements Codec<EntityRemoveEffectPacket> {
    @Override
    public EntityRemoveEffectPacket decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        byte effect = buf.readByte();
        return new EntityRemoveEffectPacket(id, effect);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityRemoveEffectPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getEffect());
        return buf;
    }
}
