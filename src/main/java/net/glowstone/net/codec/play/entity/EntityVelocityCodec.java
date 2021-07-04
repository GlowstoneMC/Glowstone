package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.EntityVelocityMessage;

import java.io.IOException;

public final class EntityVelocityCodec implements Codec<EntityVelocityMessage> {

    @Override
    public EntityVelocityMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int velocityX = buf.readShort();
        int velocityY = buf.readShort();
        int velocityZ = buf.readShort();
        return new EntityVelocityMessage(id, velocityX, velocityY, velocityZ);
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
