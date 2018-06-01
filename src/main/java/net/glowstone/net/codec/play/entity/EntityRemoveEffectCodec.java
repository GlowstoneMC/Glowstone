package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.entity.EntityRemoveEffectMessage;

public final class EntityRemoveEffectCodec implements Codec<EntityRemoveEffectMessage> {

    @Override
    public EntityRemoveEffectMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        byte effect = buf.readByte();
        return new EntityRemoveEffectMessage(id, effect);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityRemoveEffectMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getEffect());
        return buf;
    }
}
