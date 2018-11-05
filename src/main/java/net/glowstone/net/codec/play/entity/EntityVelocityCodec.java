package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.entity.EntityVelocityMessage;

public final class EntityVelocityCodec implements Codec<EntityVelocityMessage> {

    @Override
    public EntityVelocityMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int velocityX = buf.readShort();
        int velocityY = buf.readShort();
        int velocityZ = buf.readShort();
        return new EntityVelocityMessage(id, velocityX, velocityY, velocityZ);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, EntityVelocityMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeShort(message.getVelocityX());
        buf.writeShort(message.getVelocityY());
        buf.writeShort(message.getVelocityZ());
        return buf;
    }
}
